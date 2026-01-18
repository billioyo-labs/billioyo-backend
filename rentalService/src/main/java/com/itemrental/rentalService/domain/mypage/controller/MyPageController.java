package com.itemrental.rentalService.domain.mypage.controller;


import com.itemrental.rentalService.domain.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.domain.mypage.dto.MyPageSummaryDto;
import com.itemrental.rentalService.domain.mypage.service.MyPageService;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.itemrental.rentalService.domain.user.service.UserService;
import com.itemrental.rentalService.global.response.ApiResponse;
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
import java.util.List;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<MyPageSummaryDto>> getMyPage(){
        return ResponseEntity.ok(ApiResponse.success("조회 성공",myPageService.getMyPageSummary()));
    }
//
//
//    @GetMapping("/likes")
//    public ResponseEntity<List<CommunityPostReadResponseDto>> getLikePosts() {
//        return ResponseEntity.ok(myPageService.getLikedPosts());
//    }
//
//    @GetMapping("/bms")
//    public ResponseEntity<List<CommunityPostReadResponseDto>> getBmPosts() {
//        return ResponseEntity.ok(myPageService.getBmPosts());
//    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileUpdateRequestDto> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }
//
//    @GetMapping("/products")
//    public ResponseEntity<Page<RentalPostListResponseDto>> getProducts(
//        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        return ResponseEntity.ok(myPageService.getMyPosts(pageable));
//    }


    @GetMapping("/my-products")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyProducts(
        @PageableDefault(size = 10) Pageable pageable) {

        Page<RentalPostListResponseDto> myProducts = myPageService.getMyProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success("내 상품 목록 조회 성공", myProducts));
    }

    @GetMapping("/my-likes")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyLikedPosts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // userService에서 현재 로그인한 유저 ID를 추출하여 조회 로직 수행
        Page<RentalPostListResponseDto> likedPosts = myPageService.getMyLikedPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success("내 찜 목록 조회 성공", likedPosts));
    }

    // 내 북마크 목록 조회
    @GetMapping("/my-bookmarks")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyBookmarkedPosts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RentalPostListResponseDto> bookmarkedPosts = myPageService.getMyBookmarkedPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success("내 북마크 목록 조회 성공", bookmarkedPosts));
    }
    //내 주문 목록 조회
    @GetMapping( "/my-orders")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyOrders(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<RentalPostListResponseDto> orderPosts = myPageService.getMyOrderPosts(pageable);
        return ResponseEntity.ok((ApiResponse.success("내 주문 목록 조회 성공", orderPosts)));
    }
}
