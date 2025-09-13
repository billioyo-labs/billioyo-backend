package com.itemrental.rentalService.community;

import com.itemrental.rentalService.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.community.dto.request.CommunityPostUpdateRequestDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostReadResponseDto;
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
  @GetMapping("/{postId}")
  public ResponseEntity<CommunityPostReadResponseDto> getPost(@PathVariable Long postId) {
    return ResponseEntity.ok(postService.getCommunityPost(postId));
  }

  //커뮤니티 수정
  @PutMapping("/{postId}")
  public ResponseEntity<String> updatePost(@PathVariable Long postId, @RequestBody CommunityPostUpdateRequestDto dto) {
    postService.updateCommunityPost(postId, dto);
    return ResponseEntity.ok("게시글 수정 완료");
  }

  //커뮤니티 삭제
  @DeleteMapping("/{postId}")
  public ResponseEntity<String> deletePost(@PathVariable Long postId) {
    postService.deleteCommunityPost(postId);
    return ResponseEntity.ok("게시글 삭제 완료");
  }
}
