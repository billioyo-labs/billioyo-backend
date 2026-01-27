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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Rental", description = "렌탈 게시글/상품 API")
public class RentalController {

    private final RentalService rentalService;
    private final PostInteractionService interactionService;
    private final ImageAnalysisService imageAnalysisService;

    @Operation(
        summary = "렌탈 게시글 생성",
        description = "사용자가 상품 등록 버튼을 클릭했을 때 호출되는 API입니다. 렌탈 게시글을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @RequestBody RentalPostCreateRequestDto dto,
            Principal principal) {
        String email = principal.getName();
        Long postId = rentalService.createRentalPost(dto, email);
        return ResponseEntity.ok(ApiResponse.success("게시글이 등록되었습니다.", postId));
    }

    @Operation(
        summary = "렌탈 게시글 상세 조회",
        description = "상품 상세 페이지 진입 시 호출되는 API입니다. 게시글 상세 정보를 조회합니다."
    )
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<RentalPostReadResponseDto>> getRentalPost(
            @PathVariable Long postId,
            Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        RentalPostReadResponseDto response = rentalService.getRentalPost(postId, email);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    @Operation(
        summary = "렌탈 게시글 수정",
        description = "작성자가 게시글 수정 버튼을 클릭했을 때 호출되는 API입니다. 게시글 정보를 수정합니다."
    )
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updateRentalPost(
            @PathVariable Long postId,
            @RequestBody RentalPostUpdateRequestDto dto,
            Principal principal) {
        rentalService.updateRentalPost(postId, dto, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다."));
    }

    @Operation(
        summary = "렌탈 게시글 삭제",
        description = "작성자가 게시글 삭제 버튼을 클릭했을 때 호출되는 API입니다. 게시글을 삭제합니다."
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteRentalPost(
            @PathVariable Long postId,
            Principal principal) {
        rentalService.deleteRentalPost(postId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }


    @Operation(
        summary = "렌탈 게시글 목록 조회",
        description = "상품 목록 페이지에서 검색 조건과 페이징 정보에 따라 렌탈 게시글 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getPosts(
            @ModelAttribute RentalPostSearchRequestDto searchDto,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<RentalPostListResponseDto> posts = rentalService.getPosts(pageable, searchDto);
        return ResponseEntity.ok(ApiResponse.success("목록 조회 성공", posts));
    }


    @Operation(
        summary = "리뷰 작성",
        description = "대여가 완료된 상품에 대해 사용자가 리뷰를 작성할 때 호출되는 API입니다."
    )
    @PostMapping("/review/{postId}")
    public ResponseEntity<ApiResponse<Void>> createPostReview(
            @RequestBody ReviewCreateRequestDto dto,
            @PathVariable Long postId,
            Principal principal) {
        interactionService.createPostReview(dto, postId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("리뷰가 등록되었습니다."));
    }

    @Operation(
        summary = "판매자 상품 목록 조회",
        description = "특정 판매자(userId)가 등록한 상품 목록을 페이징 조회합니다."
    )
    @GetMapping("/seller/{userId}")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getSellerPosts(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RentalPostListResponseDto> sellerPosts = interactionService.getSellerPosts(pageable, userId);
        return ResponseEntity.ok(ApiResponse.success("판매자 상품 목록 조회 성공", sellerPosts));
    }

    @Operation(
        summary = "좋아요 토글",
        description = "사용자가 상품 상세에서 좋아요 버튼을 눌렀을 때 호출되는 API입니다."
    )
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Long>> likePost(
            @PathVariable Long postId,
            Principal principal) {
        Long likeCount = interactionService.toggleLike(postId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("좋아요 처리 완료", likeCount));
    }

    @Operation(
        summary = "북마크 토글",
        description = "사용자가 상품 상세에서 북마크 버튼을 눌렀을 때 호출되는 API입니다."
    )
    @PostMapping("/{postId}/bm")
    public ResponseEntity<ApiResponse<String>> bmPost(
            @PathVariable Long postId,
            Principal principal) {
        String message = interactionService.toggleBookmark(postId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("북마크 처리 완료", message));
    }

    @Operation(
        summary = "인기 게시글 조회",
        description = "메인/목록 페이지에서 인기 게시글을 조회할 때 호출되는 API입니다."
    )
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getPopularPosts(
            @PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("인기 게시글 조회 성공", rentalService.getPopularPosts(pageable)));
    }

    @Operation(
        summary = "AI 소개글 생성",
        description = "사용자가 이미지 기반으로 소개글 자동 생성 기능을 사용할 때 호출되는 API입니다."
    )
    @PostMapping("/analyze-image")
    public ResponseEntity<ApiResponse<String>> analyzeImageForDescription(@RequestBody Map<String, String> request) {
        String description = imageAnalysisService.generateDescription(request.get("imageUrl"));
        return ResponseEntity.ok(ApiResponse.success("AI가 소개글을 생성했습니다.", description));
    }

    @Operation(
        summary = "반납 처리",
        description = "사용자가 반납 버튼을 눌렀을 때 호출되는 API입니다. 주문(orderId)에 대한 반납을 처리합니다."
    )
    @PostMapping("/return/{orderId}")
    public ResponseEntity<ApiResponse<Void>> returnRental(
            @PathVariable Long orderId,
            Principal principal) {
        rentalService.returnRental(orderId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("반납 처리되었습니다."));
    }

}
