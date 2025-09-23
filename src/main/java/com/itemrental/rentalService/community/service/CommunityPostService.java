package com.itemrental.rentalService.community.service;

import com.itemrental.rentalService.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.community.dto.request.CommunityPostUpdateRequestDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.community.entity.CommunityPost;
import com.itemrental.rentalService.community.entity.CommunityPostImage;
import com.itemrental.rentalService.community.repository.CommunityPostImageRepository;
import com.itemrental.rentalService.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

  private final CommunityPostRepository repository;
  private final UserRepository userRepository;
  private final CommunityPostImageRepository imageRepository;

  //게시글 생성
  @Transactional
  public CommunityPostCreateResponseDto createCommunityPost(CommunityPostCreateRequestDto dto) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username).get();

    CommunityPost post = new CommunityPost();
    post.setUser(user);
    post.setTitle(dto.getTitle());
    post.setContent(dto.getContent());
    repository.save(post);

    if (dto.getImageUrls() != null) {
      for (String imageUrl : dto.getImageUrls()) {
        CommunityPostImage image = new CommunityPostImage();
        image.setPost(post);
        image.setImageUrl(imageUrl);
        imageRepository.save(image);
      }
    }

    return new CommunityPostCreateResponseDto(
        post.getId(),
        user.getUsername(),
        post.getTitle(),
        post.getContent()
        );
  }

  //게시글 읽기
  @Transactional
  public CommunityPostReadResponseDto getCommunityPost(Long postId) {
    CommunityPost post = repository.findById(postId).get();
    User user = post.getUser();

    return new CommunityPostReadResponseDto(
        user.getUsername(),
        post.getTitle(),
        post.getContent(),
        post.getCreatedAt(),
        post.getImages()
    );
  }

  @Transactional
  public void updateCommunityPost(Long postId, CommunityPostUpdateRequestDto dto) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User currentUser = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다"));

    CommunityPost post = repository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    User postUser = post.getUser();

    if (!postUser.getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
    }
    post.setTitle(dto.getTitle());
    post.setContent(dto.getContent());

    post.getImages().clear();

    if (dto.getImageUrls() != null) {
      for (String imageUrl : dto.getImageUrls()) {
        CommunityPostImage image = new CommunityPostImage();
        image.setPost(post);
        image.setImageUrl(imageUrl);
        imageRepository.save(image);
      }
    }
  }

  @Transactional
  public void deleteCommunityPost(Long postId) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User currentUser = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다"));

    CommunityPost post = repository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    User postUser = post.getUser();

    if (!postUser.getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
    }
    repository.delete(post);
  }
}
