package com.itemrental.rentalService.domain.rental.controller;


import com.itemrental.rentalService.domain.rental.dto.request.RentalPostCreateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostSearchRequestDto;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostUpdateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.request.ReviewCreateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostReadResponseDto;
import com.itemrental.rentalService.domain.rental.service.ImageAnalysisService;
import com.itemrental.rentalService.domain.rental.service.PostInteractionService;
import com.itemrental.rentalService.domain.rental.service.RentalService;
import com.itemrental.rentalService.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class RentalController {

    private final RentalService rentalService;
    private final PostInteractionService interactionService;
    private final ImageAnalysisService imageAnalysisService;

    //대여 게시글 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @RequestBody RentalPostCreateRequestDto dto,
            Principal principal) {
        String email = principal.getName();
        Long postId = rentalService.createRentalPost(dto, email);
        return ResponseEntity.ok(ApiResponse.success("게시글이 등록되었습니다.", postId));
    }

    //대여 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<RentalPostReadResponseDto>> getRentalPost(
            @PathVariable Long postId,
            Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        RentalPostReadResponseDto response = rentalService.getRentalPost(postId, email);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    //대여 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updateRentalPost(
            @PathVariable Long postId,
            @RequestBody RentalPostUpdateRequestDto dto,
            Principal principal) {
        rentalService.updateRentalPost(postId, dto, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다."));
    }

    //대여 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteRentalPost(
            @PathVariable Long postId,
            Principal principal) {
        rentalService.deleteRentalPost(postId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getPosts(
            @ModelAttribute RentalPostSearchRequestDto searchDto,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<RentalPostListResponseDto> posts = rentalService.getPosts(pageable, searchDto);
        return ResponseEntity.ok(ApiResponse.success("목록 조회 성공", posts));
    }


    //게시글 리뷰 작성
    @PostMapping("/review/{postId}")
    public ResponseEntity<ApiResponse<Void>> createPostReview(
            @RequestBody ReviewCreateRequestDto dto,
            @PathVariable Long postId,
            Principal principal) {
        interactionService.createPostReview(dto, postId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("리뷰가 등록되었습니다."));
    }

    // 특정 판매자의 상품 목록 조회
    @GetMapping("/seller/{userId}")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getSellerPosts(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RentalPostListResponseDto> sellerPosts = interactionService.getSellerPosts(pageable, userId);
        return ResponseEntity.ok(ApiResponse.success("판매자 상품 목록 조회 성공", sellerPosts));
    }


    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Long>> likePost(
            @PathVariable Long postId,
            Principal principal) {
        Long likeCount = interactionService.toggleLike(postId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("좋아요 처리 완료", likeCount));
    }

    @PostMapping("/{postId}/bm")
    public ResponseEntity<ApiResponse<String>> bmPost(
            @PathVariable Long postId,
            Principal principal) {
        String message = interactionService.toggleBookmark(postId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("북마크 처리 완료", message));
    }

    //인기글 조회
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getPopularPosts(
            @PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("인기 게시글 조회 성공", rentalService.getPopularPosts(pageable)));
    }

    @PostMapping("/analyze-image")
    public ResponseEntity<ApiResponse<String>> analyzeImageForDescription(@RequestBody Map<String, String> request) {
        String description = imageAnalysisService.generateDescription(request.get("imageUrl"));
        return ResponseEntity.ok(ApiResponse.success("AI가 소개글을 생성했습니다.", description));
    }

    @PostMapping("/return/{orderId}")
    public ResponseEntity<ApiResponse<Void>> returnRental(
            @PathVariable Long orderId,
            Principal principal) {
        rentalService.returnRental(orderId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("반납 처리되었습니다."));
    }

}
