package com.itemrental.billioyo.domain.mypage.service;

import com.itemrental.billioyo.domain.community.repository.CommunityPostBookmarkRepository;
import com.itemrental.billioyo.domain.community.repository.CommunityPostLikeRepository;
import com.itemrental.billioyo.domain.mypage.dto.MyOrderPostListResponseDto;
import com.itemrental.billioyo.domain.mypage.dto.MyPageSummaryDto;
import com.itemrental.billioyo.domain.order.entity.Order;
import com.itemrental.billioyo.domain.order.repository.OrderRepository;
import com.itemrental.billioyo.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.billioyo.domain.rental.entity.RentalPost;
import com.itemrental.billioyo.domain.rental.repository.PostRepository;
import com.itemrental.billioyo.domain.settlement.entity.Settlement;
import com.itemrental.billioyo.domain.settlement.entity.SettlementItem;
import com.itemrental.billioyo.domain.settlement.repository.SettlementItemRepository;
import com.itemrental.billioyo.domain.settlement.repository.SettlementRepository;
import com.itemrental.billioyo.domain.user.entity.User;
import com.itemrental.billioyo.domain.user.repository.UserRepository;
import com.itemrental.billioyo.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final CommunityPostLikeRepository likeRepo;
    private final CommunityPostBookmarkRepository bmRepo;
    private final SecurityUtil securityUtil;
    private final PostRepository postRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementItemRepository settlementItemRepository;
    private final OrderRepository orderRepository;



    @Transactional(readOnly = true)
    public Page<RentalPostListResponseDto> getMyProducts(Pageable pageable) {
        String email = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Page<RentalPost> posts = postRepository.findByUserId(user.getId(), pageable);

        return posts.map(post -> {
            String firstImageUrl = post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl();

            return new RentalPostListResponseDto(
                post.getId(),
                post.getUser().getNickName(),
                post.getTitle(),
                post.getPrice(),
                post.isStatus(),
                post.getCreatedAt(),
                firstImageUrl,
                post.getRating(),
                post.getReviewsCount()
            );
        });
    }

    @Transactional(readOnly = true)
    public Page<RentalPostListResponseDto> getMyLikedPosts(Pageable pageable) {
        String email = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return postRepository.findByLikesUserId(user.getId(), pageable)
            .map(post -> {
                String firstImageUrl = post.getImages().isEmpty()
                    ? null
                    : post.getImages().get(0).getImageUrl();

                return new RentalPostListResponseDto(
                    post.getId(),
                    post.getUser().getNickName(),
                    post.getTitle(),
                    post.getPrice(),
                    post.isStatus(),
                    post.getCreatedAt(),
                    firstImageUrl,
                    post.getRating(),
                    post.getReviewsCount()
                );
            });
    }

    @Transactional(readOnly = true)
    public Page<RentalPostListResponseDto> getMyBookmarkedPosts(Pageable pageable) {
        String email = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return postRepository.findByBookmarksUserId(user.getId(), pageable)
            .map(post -> {
                String firstImageUrl = post.getImages().isEmpty()
                    ? null
                    : post.getImages().get(0).getImageUrl();

                return new RentalPostListResponseDto(
                    post.getId(),
                    post.getUser().getNickName(),
                    post.getTitle(),
                    post.getPrice(),
                    post.isStatus(),
                    post.getCreatedAt(),
                    firstImageUrl,
                    post.getRating(),
                    post.getReviewsCount()
                );
            });
    }
    @Transactional(readOnly = true)
    public Page<MyOrderPostListResponseDto> getMyOrderPosts(Pageable pageable) {
        String email = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Page<Order> orders = orderRepository.findByUserAndStatus(
            user,
            Order.OrderStatus.PAID,
            pageable
        );
        return orders.map(order->{
            RentalPost post = postRepository.findById(order.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
            String firstImageUrl = post.getImages().isEmpty()
                ? null
                : post.getImages().get(0).getImageUrl();
            return new MyOrderPostListResponseDto(
                post.getId(),
                post.getUser().getNickName(),
                post.getTitle(),
                post.getPrice(),
                post.isStatus(),
                post.getCreatedAt(),
                firstImageUrl,
                post.getRating(),
                post.getReviewsCount(),
                order.getId()
                );
            }
        );
    }


    @Transactional
    public MyPageSummaryDto getMyPageSummary(){
        String username = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다"));

        List<Settlement> settlements = settlementRepository.findAllByOwnerIdAndStatus(
            user.getId(),
            Settlement.SettlementStatus.SETTLED
        );
        Long totalAmount = settlements.stream()
            .mapToLong(Settlement::getTotalAmount)
            .sum();

        int rentedCount = settlementItemRepository.countByOwnerIdAndStatus(
            user.getId(),
            SettlementItem.SettlementItemStatus.SETTLED);
        int lentCount = orderRepository.countByUserAndStatus(user, Order.OrderStatus.PAID);

        return new MyPageSummaryDto(rentedCount, lentCount, totalAmount);
    }
}
