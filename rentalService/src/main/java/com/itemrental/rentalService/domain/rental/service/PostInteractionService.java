package com.itemrental.rentalService.domain.rental.service;


import com.itemrental.rentalService.domain.rental.dto.request.ReviewCreateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.entity.RentalPostBookmark;
import com.itemrental.rentalService.domain.rental.entity.RentalPostLike;
import com.itemrental.rentalService.domain.rental.entity.Review;
import com.itemrental.rentalService.domain.rental.repository.PostBookmarkRepository;
import com.itemrental.rentalService.domain.rental.repository.PostLikeRepository;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.rental.repository.PostReviewRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public void createPostReview(ReviewCreateRequestDto dto, Long postId) {
        String username = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(username).get();

        RentalPost rentalPost = postRepository.findById(postId).get();

        Review review = new Review();
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());
        review.setUser(user);
        review.setRentalPost(rentalPost);

    reviewRepository.save(review);
  }

  //같은 seller 상품 조회
  @Transactional(readOnly = true)
  public Page<RentalPostListResponseDto> getSellerPosts(Pageable pageable, Long userId) {
    Page<RentalPost> page = postRepository.findByUserId(userId, pageable);
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
  @Transactional
  public Page<RentalPostListResponseDto> getMyPosts(Pageable pageable) {
      String username = securityUtil.getCurrentUserEmail();
      User user = userRepository.findByEmail(username).get();

      Page<RentalPost> page = postRepository.findByUserId(user.getId(), pageable);

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

    //게시글 좋아요
    @Transactional
    public Long toggleLike(Long postId) {
        String username = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        RentalPost rentalPost = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        ;
        if (likeRepo.existsByUser_IdAndRentalPost_Id(user.getId(), rentalPost.getId())) {
            // 이미 좋아요 → 삭제
            likeRepo.deleteByUser_IdAndRentalPost_Id(user.getId(), rentalPost.getId());
            rentalPost.setLikeCount(rentalPost.getLikeCount() - 1);
        } else {
            // 없으니까 추가
            RentalPostLike postLike = new RentalPostLike();
            postLike.setUser(user);
            postLike.setRentalPost(rentalPost);
            likeRepo.save(postLike);
            rentalPost.setLikeCount(rentalPost.getLikeCount() + 1);
        }
        return rentalPost.getLikeCount();
    }

    //게시글 북마크
    @Transactional
    public String toggleBookmark(Long postId) {
        String username = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        RentalPost rentalPost = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        ;
        if (bmRepo.existsByUser_IdAndRentalPost_Id(user.getId(), rentalPost.getId())) {
            // 이미 좋아요 → 삭제
            bmRepo.deleteByUser_IdAndRentalPost_Id(user.getId(), rentalPost.getId());
            return "북마크 취소";
        } else {
            // 없으니까 추가
            RentalPostBookmark postBm = new RentalPostBookmark();
            postBm.setUser(user);
            postBm.setRentalPost(rentalPost);
            bmRepo.save(postBm);
            return "북마크";
        }
    }

}
