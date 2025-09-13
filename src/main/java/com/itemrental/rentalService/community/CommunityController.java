package com.itemrental.rentalService.community;

import com.itemrental.rentalService.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

  private final CommunityPostService postService;

//커뮤니티 생성
  @PostMapping
  public ResponseEntity<CommunityPostCreateResponseDto> createCommunityPost(@RequestBody CommunityPostCreateRequestDto dto) {
    return ResponseEntity.ok(postService.createCommunityPost(dto));
  }
//커뮤니티 조회
  @GetMapping("/{id}")
  public ResponseEntity<CommunityPostListResponseDto> getPost(@PathVariable Long postId) {
    return ResponseEntity.ok(postService.getCommunityPost(postId));
  }


//  @GetMapping
//  public ResponseEntity<List<CommunityDto>> getAll() {
//    return ResponseEntity.ok(service.getAllPosts());
  }

//  @PutMapping("/{id}")
//  public ResponseEntity<CommunityDto> update(@PathVariable Long id, @RequestBody CommunityDto dto) {
//    CommunityDto updated = service.updatePost(id, dto);
//    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
//  }

//  @DeleteMapping("/{id}")
//  public ResponseEntity<Void> delete(@PathVariable Long id) {
//    service.deletePost(id);
//    return ResponseEntity.noContent().build();
//  }
//}
