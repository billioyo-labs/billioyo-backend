package com.itemrental.rentalService.domain.community.controller;

import com.itemrental.rentalService.domain.community.dto.request.CommentCreateRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostSearchRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostUpdateRequestDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostListResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.domain.community.service.CommunityCommentService;
import com.itemrental.rentalService.domain.community.service.CommunityPostInteractionService;
import com.itemrental.rentalService.domain.community.service.CommunityPostService;
import com.itemrental.rentalService.global.common.ApiResponse;
import com.itemrental.rentalService.global.infra.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityPostService postService;
    private final S3Service s3Service;
    private final CommunityPostInteractionService interactionService;
    private final CommunityCommentService commentService;

    //커뮤니티 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CommunityPostCreateResponseDto>> createCommunityPost(
            @RequestBody CommunityPostCreateRequestDto dto, Principal principal) {
        CommunityPostCreateResponseDto response = postService.createCommunityPost(dto, principal);
        return ResponseEntity.ok(ApiResponse.success("게시글이 생성되었습니다.", response));
    }

    //커뮤니티 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<CommunityPostReadResponseDto> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getCommunityPost(postId));
    }

    //커뮤니티 수정
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid CommunityPostUpdateRequestDto dto,
            Principal principal) {
        postService.updateCommunityPost(postId, dto, principal);
        return ResponseEntity.ok(ApiResponse.success("게시글 수정을 완료했습니다."));
    }

    //커뮤니티 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId, Principal principal) {
        postService.deleteCommunityPost(postId, principal);
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제를 완료했습니다."));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Integer>> likePost(
            @PathVariable Long postId, Principal principal) {
        int likeCount = interactionService.toggleLike(postId, principal);
        return ResponseEntity.ok(ApiResponse.success("좋아요 상태가 변경되었습니다.", likeCount));
    }

    @PostMapping("/{postId}/bm")
    public ResponseEntity<ApiResponse<String>> bmPost(
            @PathVariable Long postId, Principal principal) {
        String message = interactionService.toggleBookmark(postId, principal);
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }


    @GetMapping("/posts")
    public ResponseEntity<Page<CommunityPostListResponseDto>> getPosts(
        @ModelAttribute CommunityPostSearchRequestDto searchDto,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPostList(pageable, searchDto));
    }

    @PostMapping("/comment/{postId}")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @RequestBody @Valid CommentCreateRequestDto dto,
            @PathVariable Long postId,
            Principal principal) {
        commentService.createCommunityComment(dto, postId, principal);
        return ResponseEntity.ok(ApiResponse.success("댓글이 등록되었습니다."));
    }

    @GetMapping("/posts/search")
    public ResponseEntity<ApiResponse<List<CommunityPostListResponseDto>>> searchPosts(@RequestParam String keyword) {
        List<CommunityPostListResponseDto> response = postService.searchPosts(keyword);
        return ResponseEntity.ok(ApiResponse.success("검색 결과를 반환합니다.", response));
    }


}
