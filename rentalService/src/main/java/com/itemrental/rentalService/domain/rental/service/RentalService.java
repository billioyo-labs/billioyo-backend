package com.itemrental.rentalService.domain.rental.service;

import com.itemrental.rentalService.domain.rental.dto.request.RentalPostSearchRequestDto;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.repository.PostLikeRepository;
import com.itemrental.rentalService.domain.user.dto.UserSummary;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostCreateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostReadResponseDto;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostUpdateRequestDto;
import com.itemrental.rentalService.domain.rental.entity.Image;
import com.itemrental.rentalService.domain.rental.repository.PostImageRepository;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.Position;
import com.itemrental.rentalService.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RentalService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostImageRepository imageRepository;
  private final PostLikeRepository likeRepository;
  private final SecurityUtil securityUtil;

  //대여 게시글 생성
  @Transactional
  public Long createRentalPost(RentalPostCreateRequestDto dto) {
    String username = securityUtil.getCurrentUserEmail();
    User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

    RentalPost rentalPost = new RentalPost();
    rentalPost.setUser(user);
    rentalPost.setTitle(dto.getTitle());
    rentalPost.setDescription(dto.getDescription());
    rentalPost.setPrice(dto.getPrice());
    rentalPost.setLocation(dto.getLocation());
    rentalPost.setCategory(dto.getCategory());

    if (dto.getLatitude() != null && dto.getLongitude() != null) {
      rentalPost.setPosition(new Position(dto.getLatitude(), dto.getLongitude()));
    }

    postRepository.save(rentalPost);

    if (dto.getImageUrls() != null) {
      for (String imageUrl : dto.getImageUrls()) {
        Image image = new Image();
        image.setImageUrl(imageUrl);
        rentalPost.addImage(image);
      }
    }
    postRepository.save(rentalPost);
    return rentalPost.getId();
  }

  //대여 게시글 상세 조회
  @Transactional
  public RentalPostReadResponseDto getRentalPost(Long postId) {
    RentalPost rentalPost = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

    rentalPost.setViewCount(rentalPost.getViewCount() + 1);

    boolean isLiked = false;
    String currentUserEmail = securityUtil.getCurrentUserEmail();

    if (currentUserEmail != null && !currentUserEmail.equals("anonymousUser")) {
      Optional<User> currentUser = userRepository.findByEmail(currentUserEmail);
      if (currentUser.isPresent()) {
        isLiked = likeRepository.existsByUserAndRentalPost(currentUser.get(), rentalPost);
      }
    }

    User seller = rentalPost.getUser();
    UserSummary sellerSummary = null;

    if (seller != null) {
      sellerSummary = UserSummary.builder()
              .id(seller.getId())
              .email(seller.getEmail())
              .name(seller.getUsername())
              .nickname(seller.getNickName())
              .build();
    }

    Double lat = (rentalPost.getPosition() != null) ? rentalPost.getPosition().getLatitude() : null;
    Double lng = (rentalPost.getPosition() != null) ? rentalPost.getPosition().getLongitude() : null;

    return new RentalPostReadResponseDto(
            rentalPost.getId(),
            rentalPost.getTitle(),
            rentalPost.getDescription(),
            rentalPost.getPrice(),
            rentalPost.getLocation(),
            lat,
            lng,
            rentalPost.isStatus(),
            rentalPost.getCreatedAt(),
            rentalPost.getViewCount(),
            rentalPost.getUser().getUsername(),
            rentalPost.getCategory(),
            rentalPost.getImages(),
            rentalPost.getReviewsCount(),
            rentalPost.getRating(),
            rentalPost.getLikeCount(),
            isLiked,
            sellerSummary
    );
  }

  //게시글 수정
  @Transactional
  public void updateRentalPost(Long postId, RentalPostUpdateRequestDto dto) {
    String username = securityUtil.getCurrentUserEmail();

    User currentUser = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다"));

    RentalPost rentalPost = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

    User postUser = rentalPost.getUser();

    if (!postUser.getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
    }

    rentalPost.setTitle(dto.getTitle());
    rentalPost.setDescription(dto.getDescription());
    rentalPost.setPrice(dto.getPrice());
    rentalPost.setLocation(dto.getLocation());
    rentalPost.setCategory(dto.getCategory());


//    post.getImages().clear();
//
//    if (dto.getImageUrls() != null) {
//      for (String imageUrl : dto.getImageUrls()) {
//        CommunityPostImage image = new CommunityPostImage();
//        image.setPost(post);
//        image.setImageUrl(imageUrl);
//        imageRepository.save(image);
//      }
  }
  //게시글 삭제
  @Transactional
  public void deleteRentalPost(Long postId) {
    String username = securityUtil.getCurrentUserEmail();
    User currentUser = userRepository.findByEmail(username).get();

    RentalPost rentalPost = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    User postUser = rentalPost.getUser();

    if (!postUser.getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
    }
    postRepository.delete(rentalPost);
  }

  //상품목록 조회
  @Transactional(readOnly = true)
  public Page<RentalPostListResponseDto> getPosts(Pageable pageable, RentalPostSearchRequestDto searchDto) {
    Page<RentalPost> page;

    if (searchDto.getLat() != null && searchDto.getLng() != null && searchDto.getDistance() != null) {
      page = postRepository.findWithinDistance(
              searchDto.getLat(),
              searchDto.getLng(),
              searchDto.getDistance(),
              pageable
      );
    } else {
      page = postRepository.findAll(pageable);
    }

    return page.map(post -> {
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
  //인기글
  @Transactional(readOnly = true)
  public Page<RentalPostListResponseDto> getPopularPosts(Pageable pageable) {
    Page<RentalPost> page = postRepository.findTop5ByStatusTrueOrderByLikeCountDescViewCountDescCreatedAtDesc(pageable);

    return page.map(post -> {
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


}
