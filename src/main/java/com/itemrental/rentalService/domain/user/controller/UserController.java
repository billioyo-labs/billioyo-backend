package com.itemrental.rentalService.domain.user.controller;

import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.user.dto.request.AdminSignUpRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.UserFindAccountRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.UserSignUpRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.itemrental.rentalService.domain.user.service.UserService;
import com.itemrental.rentalService.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody UserSignUpRequestDto userSignUpRequestDto) {
        userService.signUp(userSignUpRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
    }

    @PostMapping("/sign-up/admin")
    public ResponseEntity<ApiResponse<Void>> signUpAdmin(@Valid @RequestBody AdminSignUpRequestDto signUpDto) {
        userService.signUpAdmin(signUpDto.getUserSignUpRequestDto(), signUpDto.getAdminSecret());
        return ResponseEntity.ok(ApiResponse.success("관리자 가입이 완료되었습니다."));
    }

    @GetMapping("/duplicate-check")
    public ResponseEntity<ApiResponse<Void>> duplicateCheck(@RequestParam("nickName") String nickName) {
        userService.duplicateCheck(nickName);
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 아이디입니다."));
    }

    @PostMapping("/find-account")
    public ResponseEntity<ApiResponse<String>> findAccount(@Valid @RequestBody UserFindAccountRequestDto userFindAccountRequestDto) {
        String email = userService.findAccount(userFindAccountRequestDto.getPhoneNumber());
        return ResponseEntity.ok(ApiResponse.success("계정 찾기 성공", email));
    }

    //회원정보 수정
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateUser(@Valid @RequestBody UserProfileUpdateRequestDto userProfileUpdateRequestDto, Principal principal) {
        userService.updateUser(principal.getName(), userProfileUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원 정보가 수정되었습니다."));
    }

    @GetMapping("/my-products")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyProducts(
        @PageableDefault(size = 10) Pageable pageable,
        Principal principal) {

        Page<RentalPostListResponseDto> myProducts = userService.getMyProducts(principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("내 상품 목록 조회 성공", myProducts));
    }

    @GetMapping("/my-likes")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyLikedPosts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        Principal principal
    ) {
        Page<RentalPostListResponseDto> likedPosts = userService.getMyLikedPosts(principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("내 찜 목록 조회 성공", likedPosts));
    }

    @GetMapping("/my-bookmarks")
    public ResponseEntity<ApiResponse<Page<RentalPostListResponseDto>>> getMyBookmarkedPosts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        Principal principal
    ) {
        Page<RentalPostListResponseDto> bookmarkedPosts = userService.getMyBookmarkedPosts(principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("내 북마크 목록 조회 성공", bookmarkedPosts));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(Principal principal) {
        userService.deleteUser(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 처리되었습니다."));
    }


}