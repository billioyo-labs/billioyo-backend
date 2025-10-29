package com.itemrental.rentalService.service;


import com.itemrental.rentalService.community.dto.response.CommunityPostListResponseDto;
import com.itemrental.rentalService.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.entity.Post;
import com.itemrental.rentalService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RentalService {

  private final PostRepository postRepository;


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
