package com.itemrental.rentalService.rental.service;


import com.itemrental.rentalService.community.dto.request.CommentCreateRequestDto;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.rental.dto.ReviewCreateRequestDto;
import com.itemrental.rentalService.rental.entity.Post;
import com.itemrental.rentalService.rental.entity.Review;
import com.itemrental.rentalService.rental.repository.PostRepository;
import com.itemrental.rentalService.rental.repository.PostReviewRepository;
import com.itemrental.rentalService.repository.UserRepository;
import com.itemrental.rentalService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostInteractionService {
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostReviewRepository reviewRepository;


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
