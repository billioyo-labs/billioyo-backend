package com.itemrental.rentalService.community;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

  private final CommunityRepository repository;

  public CommunityDto createPost(CommunityDto dto) {
    Community post = Community.builder()
      .title(dto.getTitle())
      .content(dto.getContent())
      .build();
    return CommunityDto.fromEntity(repository.save(post));
  }

  public CommunityDto getPost(Long id) {
    return repository.findById(id)
      .map(CommunityDto::fromEntity)
      .orElse(null);
  }

  public List<CommunityDto> getAllPosts() {
    return repository.findAll().stream()
      .map(CommunityDto::fromEntity)
      .collect(Collectors.toList());
  }

  public CommunityDto updatePost(Long id, CommunityDto dto) {
    return repository.findById(id)
      .map(post -> {
        post.update(dto.getTitle(), dto.getContent());
        return CommunityDto.fromEntity(repository.save(post));
      })
      .orElse(null);
  }

   public void deletePost(Long id) {
    repository.deleteById(id);
   }
}
