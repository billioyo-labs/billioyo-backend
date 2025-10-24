package com.itemrental.rentalService.rental.service;

import com.itemrental.rentalService.community.dto.response.CommentResponseDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.community.entity.CommunityPost;
import com.itemrental.rentalService.community.entity.CommunityPostImage;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.rental.dto.RentalPostCreateRequestDto;
import com.itemrental.rentalService.rental.dto.RentalPostListResponseDto;
import com.itemrental.rentalService.rental.dto.RentalPostReadResponseDto;
import com.itemrental.rentalService.rental.entity.Post;
import com.itemrental.rentalService.rental.repository.PostRepository;
import com.itemrental.rentalService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RentalService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;


  //대여 게시글 생성
  @Transactional
  public Long createRentalPost(RentalPostCreateRequestDto dto) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(username).get();

    Post post = new Post();
    post.setUser(user);
    post.setTitle(dto.getTitle());
    post.setDescription(dto.getDescription());
    post.setPrice(dto.getPrice());
    post.setLocation(dto.getLocation());
    post.setCategory(dto.getCategory());
    postRepository.save(post);
//
//    if (dto.getImageUrls() != null) {
//      for (String imageUrl : dto.getImageUrls()) {
//        CommunityPostImage image = new CommunityPostImage();
//        image.setPost(post);
//        image.setImageUrl(imageUrl);
//        imageRepository.save(image);
//      }
//    }
    return post.getId();
  }

  //대여 게시글 상세 조회
  @Transactional
  public RentalPostReadResponseDto getRentalPost(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

    post.setViewCount(post.getViewCount() + 1);

//    List<CommentResponseDto> comments = post.getComments().stream().map(
//        comment -> new CommentResponseDto(
//            comment.getId(),
//            comment.getUser().getUsername(),
//            comment.getComment(),
//            comment.getCreatedAt()
//        )).toList();
    return new RentalPostReadResponseDto(
        post.getId(),
        post.getTitle(),
        post.getDescription(),
        post.getPrice(),
        post.getLocation(),
        post.isStatus(),
        post.getCreatedAt(),
        post.getViewCount(),
        post.getReportCount(),
        post.getUser().getUsername(),
        post.getCategory()
    );
  }






  //상품목록 조회
  @Transactional(readOnly = true)
  public Page<RentalPostListResponseDto> getPosts(Pageable pageable) {
    Page<Post> page = postRepository.findAll(pageable);

    return page.map(post->
        new RentalPostListResponseDto(
            post.getId(),
            post.getUser(),
            post.getTitle(),
            post.getPrice(),
            post.isStatus(),
            post.getCreatedAt()
        ));
  }
}
