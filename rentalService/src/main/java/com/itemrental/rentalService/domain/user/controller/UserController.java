package com.itemrental.rentalService.domain.user.controller;

import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.user.dto.AdminSignUpRequestDto;
import com.itemrental.rentalService.domain.user.dto.*;
import com.itemrental.rentalService.domain.user.service.UserService;
import com.itemrental.rentalService.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpDto signUpDto) {
        String message = userService.signUp(signUpDto);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    @PostMapping("/sign-up/admin")
    public ResponseEntity<ApiResponse<Void>> signUpAdmin(@Valid @RequestBody AdminSignUpRequestDto signUpDto) {
        String message = userService.signUpAdmin(signUpDto.getSignUpDto(), signUpDto.getAdminSecret());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/duplicate-check")
    public ResponseEntity<ApiResponse<Void>> duplicateCheck(@RequestParam("nickName") String nickName){
        userService.duplicateCheck(nickName);
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 아이디입니다."));
    }

    @PostMapping("/find-account")
    public ResponseEntity<ApiResponse<String>> findAccount(@Valid @RequestBody FindAccountDto findAccountDto){
        return ResponseEntity.ok(ApiResponse.success("사용자 아이디" ,userService.findAccount(findAccountDto.getPhoneNumber())));
    }

    //회원정보 수정
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateUser(@Valid @RequestBody UpdateUserDto updateUserDto) {
        String message = userService.updateUser(updateUserDto);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/my-products")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyProducts(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RentalPostListResponseDto> myProducts = userService.getMyProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success("내 상품 목록 조회 성공", myProducts));
    }

    @GetMapping("/my-likes")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyLikedPosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // userService에서 현재 로그인한 유저 ID를 추출하여 조회 로직 수행
        Page<RentalPostListResponseDto> likedPosts = userService.getMyLikedPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success("내 찜 목록 조회 성공", likedPosts));
    }

    // 내 북마크 목록 조회
    @GetMapping("/my-bookmarks")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyBookmarkedPosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RentalPostListResponseDto> bookmarkedPosts = userService.getMyBookmarkedPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success("내 북마크 목록 조회 성공", bookmarkedPosts));
    }

    //회원정보 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser() {
        String message = userService.deleteUser();
        return ResponseEntity.ok(ApiResponse.success(message));
    }



}