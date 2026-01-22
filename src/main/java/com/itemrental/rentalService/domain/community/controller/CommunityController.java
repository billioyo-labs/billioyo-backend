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
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/community-posts")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityPostService postService;
    private final CommunityPostInteractionService interactionService;
    private final CommunityCommentService commentService;

    // =====================
    // 커뮤니티 게시글 생성
    // =====================
    @Operation(summary = "커뮤니티 게시글 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<CommunityPostCreateResponseDto>> createCommunityPost(
            @RequestBody CommunityPostCreateRequestDto dto, Principal principal) {
        CommunityPostCreateResponseDto response = postService.createCommunityPost(dto, principal);
        return ResponseEntity.ok(ApiResponse.success("게시글이 생성되었습니다.", response));
    }

    // =====================
    // 커뮤니티 게시글 상세 조회
    // =====================
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommunityPostReadResponseDto>> getCommunityPost(@PathVariable Long postId) {
        CommunityPostReadResponseDto response = postService.getCommunityPost(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글을 조회했습니다.", response));
    }

    // =====================
    // 커뮤니티 게시글 수정
    // =====================
    @Operation(summary = "커뮤니티 게시글 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updateCommunityPost(
            @PathVariable Long postId,
            @RequestBody @Valid CommunityPostUpdateRequestDto dto,
            Principal principal) {
        postService.updateCommunityPost(postId, dto, principal);
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다."));
    }

    // =====================
    // 커뮤니티 게시글 삭제
    // =====================
    @Operation(summary = "커뮤니티 게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteCommunityPost(
            @PathVariable Long postId, Principal principal) {
        postService.deleteCommunityPost(postId, principal);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }

    // =====================
    // 좋아요 토글
    // =====================
    @Operation(summary = "커뮤니티 게시글 북마크 토글")
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Integer>> toggleCommunityPostLike(
            @PathVariable Long postId, Principal principal) {
        int likeCount = interactionService.toggleCommunityPostLike(postId, principal);
        return ResponseEntity.ok(ApiResponse.success("좋아요 상태가 변경되었습니다.", likeCount));
    }

    // =====================
    // 북마크 토글
    // =====================
    @Operation(summary = "커뮤니티 게시글 좋아요 토글")
    @PostMapping("/{postId}/bookmarks")
    public ResponseEntity<ApiResponse<Void>> toggleCommunityPostBookmark(
            @PathVariable Long postId, Principal principal) {
        interactionService.toggleCommunityPostBookmark(postId, principal);
        return ResponseEntity.ok(ApiResponse.success("북마크 상태가 변경되었습니다"));
    }

    // =====================
    // 커뮤니티 게시글 목록 조회
    // =====================
    @GetMapping("/posts")
    @Operation(
        summary = "커뮤니티 게시글 목록 조회",
        description =
            "위치 기반 조건과 페이징 정보에 따라 커뮤니티 게시글 목록을 조회합니다.\n\n" +
                "예시:\n" +
                "- 전체 게시글: /api/community-posts/posts?lat=37.5&lng=126.9&distance=3&page=0&size=10\n" +
                "- 인기글: /api/community-posts/posts?tab=HOT&lat=37.5&lng=126.9&distance=3&page=0&size=10"
    )
    public ResponseEntity<Page<CommunityPostListResponseDto>> getCommunityPosts(
        @ModelAttribute CommunityPostSearchRequestDto searchDto,
        @RequestParam(required = false, defaultValue = "ALL") String tab,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getCommunityPosts(pageable, searchDto,tab));
    }

    // =====================
    // 커뮤니티 게시글 댓글 등록
    // =====================
    @Operation(summary = "커뮤니티 게시글 댓글 등록")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Void>> createCommunityPostComment(
            @RequestBody @Valid CommentCreateRequestDto dto,
            @PathVariable Long postId,
            Principal principal) {
        commentService.createCommunityPostComment(dto, postId, principal);
        return ResponseEntity.ok(ApiResponse.success("댓글이 등록되었습니다."));
    }

    // =====================
    // 커뮤니티 게시글 키워드 검색
    // =====================
    @GetMapping("/posts/search")
    public ResponseEntity<ApiResponse<List<CommunityPostListResponseDto>>> searchCommunityPosts(@RequestParam String keyword) {
        List<CommunityPostListResponseDto> response = postService.searchCommunityPosts(keyword);
        return ResponseEntity.ok(ApiResponse.success("검색 결과를 조회했습니다.", response));
    }


}
