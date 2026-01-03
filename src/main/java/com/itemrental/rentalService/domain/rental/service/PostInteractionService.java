package com.itemrental.rentalService.domain.rental.service;


import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.rental.dto.request.ReviewCreateRequestDto;
import com.itemrental.rentalService.domain.rental.entity.Post;
import com.itemrental.rentalService.domain.rental.entity.RentalPostBookmark;
import com.itemrental.rentalService.domain.rental.entity.RentalPostLike;
import com.itemrental.rentalService.domain.rental.entity.Review;
import com.itemrental.rentalService.domain.rental.repository.PostBookmarkRepository;
import com.itemrental.rentalService.domain.rental.repository.PostLikeRepository;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.rental.repository.PostReviewRepository;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostInteractionService {
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostReviewRepository reviewRepository;
  private final PostBookmarkRepository bmRepo;
  private final PostLikeRepository likeRepo;
  private final SecurityUtil securityUtil;


  @Transactional
  public void createPostReview(ReviewCreateRequestDto dto, Long postId){
    String username = securityUtil.getCurrentUserEmail();
    User user = userRepository.findByEmail(username).get();

    Post post = postRepository.findById(postId).get();

    Review review = new Review();
    review.setContent(dto.getContent());
    review.setRating(dto.getRating());
    review.setUser(user);
    review.setPost(post);

    reviewRepository.save(review);
  }

  //같은 seller 상품 조회
  @Transactional(readOnly = true)
  public Page<RentalPostListResponseDto> getSellerPosts(Pageable pageable, Long userId) {
    Page<Post> page = postRepository.findByUserId(userId, pageable);
    return page.map(post->
        new RentalPostListResponseDto(
            post.getId(),
            post.getUser().getNickName(),
            post.getTitle(),
            post.getPrice(),
            post.isStatus(),
            post.getCreatedAt()
        ));
  }
  @Transactional
  public Page<RentalPostListResponseDto> getMyPosts(Pageable pageable) {
    String username = securityUtil.getCurrentUserEmail();
    User user = userRepository.findByEmail(username).get();

    Page<Post> page = postRepository.findByUserId(user.getId(),pageable);

    return page.map(post->
        new RentalPostListResponseDto(
            post.getId(),
            post.getUser().getNickName(),
            post.getTitle(),
            post.getPrice(),
            post.isStatus(),
            post.getCreatedAt()
        ));
  }

  //게시글 좋아요
  @Transactional
  public Long toggleLike(Long postId){
    String username = securityUtil.getCurrentUserEmail();
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    if (likeRepo.existsByUser_IdAndPost_Id(user.getId(), post.getId())) {
      // 이미 좋아요 → 삭제
      likeRepo.deleteByUser_IdAndPost_Id(user.getId(), post.getId());
      post.setLikeCount(post.getLikeCount() - 1);
    } else {
      // 없으니까 추가
      RentalPostLike postLike = new RentalPostLike();
      postLike.setUser(user);
      postLike.setPost(post);
      likeRepo.save(postLike);
      post.setLikeCount(post.getLikeCount() + 1);
    }
    return post.getLikeCount();
  }

  //게시글 북마크
  @Transactional
  public String toggleBookmark(Long postId){
    String username = securityUtil.getCurrentUserEmail();
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    if (bmRepo.existsByUser_IdAndPost_Id(user.getId(), post.getId())) {
      // 이미 좋아요 → 삭제
      bmRepo.deleteByUser_IdAndPost_Id(user.getId(), post.getId());
      return "북마크 취소";
    } else {
      // 없으니까 추가
      RentalPostBookmark postBm = new RentalPostBookmark();
      postBm.setUser(user);
      postBm.setPost(post);
      bmRepo.save(postBm);
      return "북마크";
    }
  }

}
