package com.itemrental.rentalService.rental.service;


import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.rental.dto.RentalPostListResponseDto;
import com.itemrental.rentalService.rental.dto.ReviewCreateRequestDto;
import com.itemrental.rentalService.rental.entity.Post;
import com.itemrental.rentalService.rental.entity.Review;
import com.itemrental.rentalService.rental.repository.PostRepository;
import com.itemrental.rentalService.rental.repository.PostReviewRepository;
import com.itemrental.rentalService.repository.UserRepository;
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


  @Transactional
  public void createPostReview(ReviewCreateRequestDto dto, Long postId){
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(username).get();

    Post post = postRepository.findById(postId).get();

    Review review = new Review();
    review.setContent(dto.getContent());
    review.setRating(dto.getRating());
    review.setUser(user);
    review.setPost(post);

    reviewRepository.save(review);
  }





}
