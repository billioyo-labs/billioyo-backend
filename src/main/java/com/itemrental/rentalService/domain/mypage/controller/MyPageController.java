package com.itemrental.rentalService.domain.mypage.controller;


import com.itemrental.rentalService.domain.mypage.dto.MyOrderPostListResponseDto;
import com.itemrental.rentalService.domain.mypage.dto.MyPageSummaryDto;
import com.itemrental.rentalService.domain.mypage.service.MyPageService;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.itemrental.rentalService.domain.user.service.UserService;
import com.itemrental.rentalService.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "MyPage", description = "마이페이지 API")
public class MyPageController {

    private final MyPageService myPageService;
    private final UserService userService;

    @Operation(
        summary = "마이페이지 요약 조회",
        description = "로그인한 사용자의 마이페이지 요약 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<MyPageSummaryDto>> getMyPage(){
        return ResponseEntity.ok(ApiResponse.success("조회 성공",myPageService.getMyPageSummary()));
    }

    @Operation(
        summary = "내 프로필 조회",
        description = "로그인한 사용자의 프로필 정보를 조회합니다."
    )
    @GetMapping("/profile")
    public ResponseEntity<UserProfileUpdateRequestDto> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }

    @Operation(
        summary = "내 상품 목록 조회",
        description = "로그인한 사용자가 등록한 렌탈 상품 목록을 페이징 조회합니다."
    )
    @GetMapping("/my-products")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyProducts(
        @PageableDefault(size = 10) Pageable pageable) {

        Page<RentalPostListResponseDto> myProducts = myPageService.getMyProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success("내 상품 목록 조회 성공", myProducts));
    }

    @Operation(
        summary = "내 좋아요 목록 조회",
        description = "로그인한 사용자가 좋아요한 렌탈 게시글 목록을 페이징 조회합니다."
    )
    @GetMapping("/my-likes")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyLikedPosts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // userService에서 현재 로그인한 유저 ID를 추출하여 조회 로직 수행
        Page<RentalPostListResponseDto> likedPosts = myPageService.getMyLikedPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success("내 찜 목록 조회 성공", likedPosts));
    }

    @Operation(
        summary = "내 북마크 목록 조회",
        description = "로그인한 사용자가 북마크한 렌탈 게시글 목록을 페이징 조회합니다."
    )
    @GetMapping("/my-bookmarks")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyBookmarkedPosts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RentalPostListResponseDto> bookmarkedPosts = myPageService.getMyBookmarkedPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success("내 북마크 목록 조회 성공", bookmarkedPosts));
    }

    @Operation(
        summary = "내 주문 목록 조회",
        description = "로그인한 사용자의 주문 목록을 페이징 조회합니다."
    )
    @GetMapping( "/my-orders")
    public ResponseEntity<ApiResponse<Page<MyOrderPostListResponseDto>>> getMyOrders(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<MyOrderPostListResponseDto> orderPosts = myPageService.getMyOrderPosts(pageable);
        return ResponseEntity.ok((ApiResponse.success("내 주문 목록 조회 성공", orderPosts)));
    }
}
