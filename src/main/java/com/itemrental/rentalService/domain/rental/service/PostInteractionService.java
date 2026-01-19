package com.itemrental.rentalService.domain.rental.service;


import com.itemrental.rentalService.domain.rental.dto.request.ReviewCreateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.entity.RentalPostBookmark;
import com.itemrental.rentalService.domain.rental.entity.RentalPostLike;
import com.itemrental.rentalService.domain.rental.entity.Review;
import com.itemrental.rentalService.domain.rental.exception.PostNotFoundException;
import com.itemrental.rentalService.domain.rental.repository.PostBookmarkRepository;
import com.itemrental.rentalService.domain.rental.repository.PostLikeRepository;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.rental.repository.PostReviewRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.exception.UserNotFoundException;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
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
    private final PostBookmarkRepository bmRepository;
    private final PostLikeRepository likeRepository;


    @Transactional
    public void createPostReview(ReviewCreateRequestDto dto, Long postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        RentalPost rentalPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        rentalPost.updateRating(dto.getRating());

        Review review = Review.create(user, rentalPost, dto.getContent(), dto.getRating());

        reviewRepository.save(review);
    }

    //같은 seller 상품 조회
    @Transactional(readOnly = true)
    public Page<RentalPostListResponseDto> getSellerPosts(Pageable pageable, Long userId) {
        return postRepository.findByUserId(userId, pageable)
                .map(RentalPostListResponseDto::from);
    }

    //게시글 좋아요
    @Transactional
    public Long toggleLike(Long postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        RentalPost rentalPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        likeRepository.findByUserAndRentalPost(user, rentalPost)
                .ifPresentOrElse(
                        like -> {
                            likeRepository.delete(like);
                            rentalPost.toggleLike(false);
                        },
                        () -> {
                            RentalPostLike newLike = RentalPostLike.create(user, rentalPost);
                            likeRepository.save(newLike);
                            rentalPost.toggleLike(true);
                        }
                );

        return rentalPost.getLikeCount();
    }

    //게시글 북마크
    @Transactional
    public String toggleBookmark(Long postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        RentalPost rentalPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return bmRepository.findByUserAndRentalPost(user, rentalPost)
                .map(bookmark -> {
                    bmRepository.delete(bookmark);
                    return "북마크 취소";
                })
                .orElseGet(() -> {
                    RentalPostBookmark newBookmark = RentalPostBookmark.create(user, rentalPost);
                    bmRepository.save(newBookmark);
                    return "북마크";
                });
    }

}
