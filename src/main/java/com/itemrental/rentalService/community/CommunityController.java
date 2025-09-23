package com.itemrental.rentalService.community;

import com.itemrental.rentalService.community.dto.request.CommunityImagePresignedUrlRequestDto;
import com.itemrental.rentalService.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.community.dto.request.CommunityPostUpdateRequestDto;
import com.itemrental.rentalService.community.dto.response.CommunityImagePresignedUrlResponseDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.community.service.CommunityPostLikeService;
import com.itemrental.rentalService.community.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

  private final CommunityPostService postService;
  private final S3Service s3Service;
  private final CommunityPostLikeService likeService;

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

  // 다중 이미지 링크 생성
  @PostMapping("/presigned-url")
  public ResponseEntity<List<CommunityImagePresignedUrlResponseDto>> getPresignedUrls(@RequestBody CommunityImagePresignedUrlRequestDto dto) {
    return ResponseEntity.ok(s3Service.createCommunityImagePresignedUrls(dto.getFileNames()));
  }

  @PostMapping("/{postId}/like")
  public ResponseEntity<String> likePost(@PathVariable Long postId) {
    likeService.toggleLike(postId);
    return ResponseEntity.ok("게시글 하트 완료");
  }




}
