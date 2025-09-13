package com.itemrental.rentalService.community;

import com.itemrental.rentalService.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostListResponseDto;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

  private final CommunityPostRepository repository;
  private final UserRepository userRepository;

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

    return new CommunityPostCreateResponseDto(
        user.getUsername(),
        post.getTitle(),
        post.getContent());
  }

  //게시글 읽기
  @Transactional
  public CommunityPostListResponseDto getCommunityPost(Long postId) {
    CommunityPost post = repository.findById(postId).get();
    User user = post.getUser();
    return new CommunityPostListResponseDto(
        user.getUsername(),
        post.getTitle(),
        post.getContent(),
        post.getCreatedAt()
    );
  }

//  public CommunityDto createPost(CommunityDto dto) {
//    CommunityPost post = CommunityPost.builder()
//      .title(dto.getTitle())
//      .content(dto.getContent())
//      .build();
//    return CommunityDto.fromEntity(repository.save(post));
//  }
//
//  public CommunityDto getPost(Long id) {
//    return repository.findById(id)
//      .map(CommunityDto::fromEntity)
//      .orElse(null);
//  }
//
//  public List<CommunityDto> getAllPosts() {
//    return repository.findAll().stream()
//      .map(CommunityDto::fromEntity)
//      .collect(Collectors.toList());
//  }

//  public CommunityDto updatePost(Long id, CommunityDto dto) {
//    return repository.findById(id)
//      .map(post -> {
//        post.update(dto.getTitle(), dto.getContent());
//        return CommunityDto.fromEntity(repository.save(post));
//      })
//      .orElse(null);
//  }

   public void deletePost(Long id) {
    repository.deleteById(id);
   }
}
