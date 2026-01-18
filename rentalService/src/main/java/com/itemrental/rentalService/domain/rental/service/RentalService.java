package com.itemrental.rentalService.domain.rental.service;

import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.order.repository.OrderRepository;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostCreateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostSearchRequestDto;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostUpdateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostReadResponseDto;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.repository.PostLikeRepository;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.settlement.entity.SettlementItem;
import com.itemrental.rentalService.domain.settlement.repository.SettlementItemRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RentalService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository likeRepository;
    private final OrderRepository orderRepository;
    private final SettlementItemRepository settlementItemRepository;
    private final List<PostSearchStrategy> strategies;

    //대여 게시글 생성
    @Transactional
    public Long createRentalPost(RentalPostCreateRequestDto dto, String loginEmail) {
        User user = userRepository.findByEmail(loginEmail)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        RentalPost rentalPost = RentalPost.create(
                user, dto.getTitle(), dto.getDescription(), dto.getPrice(),
                dto.getLocation(), new Position(dto.getLatitude(), dto.getLongitude()), dto.getCategory()
        );

        if (dto.getImageUrls() != null) {
            dto.getImageUrls().forEach(rentalPost::addImage);
        }

        return postRepository.save(rentalPost).getId();
    }

    //대여 게시글 상세 조회
    @Transactional
    public RentalPostReadResponseDto getRentalPost(Long postId, String loginEmail) {
        RentalPost rentalPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        rentalPost.incrementViewCount();

        boolean isLiked = false;
        if (loginEmail != null && !loginEmail.equals("anonymousUser")) {
            isLiked = userRepository.findByEmail(loginEmail)
                    .map(user -> likeRepository.existsByUserAndRentalPost(user, rentalPost))
                    .orElse(false);
        }

        return RentalPostReadResponseDto.from(rentalPost, isLiked);
    }

    //게시글 수정
    @Transactional
    public void updateRentalPost(Long postId, RentalPostUpdateRequestDto dto, String loginEmail) {
        RentalPost rentalPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        validateAuthor(rentalPost, loginEmail);

        rentalPost.update(dto.getTitle(), dto.getDescription(), dto.getPrice(), dto.getLocation(), dto.getCategory());
    }

    //인기글
    @Transactional(readOnly = true)
    public Page<RentalPostListResponseDto> getPopularPosts(Pageable pageable) {
        return postRepository.findTop5ByStatusTrueOrderByLikeCountDescViewCountDescCreatedAtDesc(pageable)
                .map(RentalPostListResponseDto::from);
    }

    @Transactional
    public void deleteRentalPost(Long postId, String loginEmail) {
        RentalPost rentalPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        validateAuthor(rentalPost, loginEmail);

        postRepository.delete(rentalPost);
    }

    //상품목록 조회
    public Page<RentalPostListResponseDto> getPosts(Pageable pageable, RentalPostSearchRequestDto searchDto) {
        PostSearchStrategy strategy = (searchDto.getLat() != null)
                ? getStrategy(DistanceSearchStrategy.class)
                : getStrategy(DefaultSearchStrategy.class);

        return strategy.search(searchDto, pageable)
                .map(RentalPostListResponseDto::from);
    }

    private PostSearchStrategy getStrategy(Class<?> strategyClass) {
        return strategies.stream()
                .filter(strategyClass::isInstance)
                .findFirst()
                .orElseThrow();
    }

    //대여 반납
    @Transactional
    public void returnRental(Long orderId, String loginEmail) {
        User user = userRepository.findByEmail(loginEmail)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다"));

        if (!user.getId().equals(order.getUser().getId())) {
            throw new AccessDeniedException("반납 권한이 없습니다.");
        }

        RentalPost post = postRepository.findById(order.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        post.changeStatus(false);
        order.setStatus(Order.OrderStatus.RETURNED);

        SettlementItem si = SettlementItem.builder()
                .postId(post.getId())
                .ownerId(post.getUser().getId())
                .orderId(orderId)
                .amount(order.getAmount())
                .build();
        settlementItemRepository.save(si);
    }

    private void validateAuthor(RentalPost post, String email) {
        if (!post.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("작성자만 수정/삭제할 수 있습니다.");
        }
    }
}
