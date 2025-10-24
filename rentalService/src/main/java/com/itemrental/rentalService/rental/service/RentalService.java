package com.itemrental.rentalService.rental.service;

import com.itemrental.rentalService.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.community.entity.CommunityPostImage;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.rental.dto.RentalPostCreateRequestDto;
import com.itemrental.rentalService.rental.dto.RentalPostListResponseDto;
import com.itemrental.rentalService.rental.entity.Post;
import com.itemrental.rentalService.rental.repository.PostRepository;
import com.itemrental.rentalService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
