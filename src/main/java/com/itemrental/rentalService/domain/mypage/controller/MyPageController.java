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


    @GetMapping("/likes")
    public ResponseEntity<List<CommunityPostReadResponseDto>> getLikePosts() {
        return ResponseEntity.ok(myPageService.getLikedPosts());
    }

    @GetMapping("/bms")
    public ResponseEntity<List<CommunityPostReadResponseDto>> getBmPosts() {
        return ResponseEntity.ok(myPageService.getBmPosts());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileUpdateRequestDto> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }

    @GetMapping("/products")
    public ResponseEntity<Page<RentalPostListResponseDto>> getProducts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(myPageService.getMyPosts(pageable));
    }

}
