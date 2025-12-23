package com.itemrental.rentalService.rental.service;

import com.itemrental.rentalService.user.dto.UserSummary;
import com.itemrental.rentalService.user.entity.User;
import com.itemrental.rentalService.rental.dto.RentalPostCreateRequestDto;
import com.itemrental.rentalService.rental.dto.RentalPostListResponseDto;
import com.itemrental.rentalService.rental.dto.RentalPostReadResponseDto;
import com.itemrental.rentalService.rental.dto.RentalPostUpdateRequestDto;
import com.itemrental.rentalService.rental.entity.Image;
import com.itemrental.rentalService.rental.entity.Post;
import com.itemrental.rentalService.rental.repository.PostImageRepository;
import com.itemrental.rentalService.rental.repository.PostRepository;
import com.itemrental.rentalService.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class RentalService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostImageRepository imageRepository;


  //대여 게시글 생성
  @Transactional
  public Long createRentalPost(RentalPostCreateRequestDto dto) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    System.out.println(username);
    User user = userRepository.findByEmail(username).get();

    Post post = new Post();
    post.setUser(user);
    post.setTitle(dto.getTitle());
    post.setDescription(dto.getDescription());
    post.setPrice(dto.getPrice());
    post.setLocation(dto.getLocation());
    post.setCategory(dto.getCategory());
    postRepository.save(post);

    if (dto.getImageUrls() != null) {
      for (String imageUrl : dto.getImageUrls()) {
        Image image = new Image();
        image.setPost(post);
        image.setImageUrl(imageUrl);
        imageRepository.save(image);
      }
    }
    return post.getId();
  }

  //대여 게시글 상세 조회
  @Transactional
  public RentalPostReadResponseDto getRentalPost(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

    post.setViewCount(post.getViewCount() + 1);

    User seller = post.getUser();
    UserSummary sellerSummary = null;

    if (seller != null) {
      sellerSummary = UserSummary.builder()
          .id(seller.getId())
          .email(seller.getEmail())
          .name(seller.getUsername())
          .nickname(seller.getNickName())
          .build();
    }


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
        post.getUser().getUsername(),
        post.getCategory(),
        post.getImages(),
        post.getReviewsCount(),
        post.getRating(),
        sellerSummary
    );
  }

  //게시글 수정
  @Transactional
  public void updateRentalPost(Long postId, RentalPostUpdateRequestDto dto) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

    User currentUser = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다"));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

    User postUser = post.getUser();

    if (!postUser.getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
    }

    post.setTitle(dto.getTitle());
    post.setDescription(dto.getDescription());
    post.setPrice(dto.getPrice());
    post.setLocation(dto.getLocation());
    post.setCategory(dto.getCategory());


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
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User currentUser = userRepository.findByEmail(username).get();

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    User postUser = post.getUser();

    if (!postUser.getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
    }
    postRepository.delete(post);
  }

  //상품목록 조회
  @Transactional(readOnly = true)
  public Page<RentalPostListResponseDto> getPosts(Pageable pageable) {
    Page<Post> page = postRepository.findAll(pageable);



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
  //인기글
  @Transactional(readOnly = true)
  public Page<RentalPostListResponseDto> getPopularPosts(Pageable pageable) {
    Page<Post> page = postRepository.findTop5ByStatusTrueOrderByLikeCountDescViewCountDescCreatedAtDesc(pageable);

    return page.map(post ->
        new RentalPostListResponseDto(
            post.getId(),
            post.getUser().getNickName(),
            post.getTitle(),
            post.getPrice(),
            post.isStatus(),
            post.getCreatedAt()
        ));
  }


}
