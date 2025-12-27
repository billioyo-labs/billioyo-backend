package com.itemrental.rentalService.domain.user.controller;

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
import org.springframework.security.access.prepost.PreAuthorize;
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

    //회원정보 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser() {
        String message = userService.deleteUser();
        return ResponseEntity.ok(ApiResponse.success(message));
    }


    //관리자 유저 차단
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(@RequestParam String email) {
        String message = userService.banUser(email);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/report")
    public ResponseEntity<ApiResponse<Void>> reportPost(@RequestBody ReportRequestDto dto) {
        userService.reportPost(dto);
        return ResponseEntity.ok(ApiResponse.success("게시글 신고 완료"));
    }
    // 관리자 신고글 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reports")
    public ResponseEntity<Page<ReportListResponseDto>> getReports(
        @PageableDefault(size = 10, sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(userService.getReportList(pageable));
    }
    // 관리자 게시글 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/community/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteCommunityPost(@PathVariable Long postId){
        userService.adminDeleteCommunityPost(postId);
        return ResponseEntity.ok(ApiResponse.success("커뮤니티 게시글 삭제 완료"));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/rental/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteRentalPost(@PathVariable Long postId){
        userService.adminDeleteRentalPost(postId);
        return ResponseEntity.ok(ApiResponse.success("렌탈 게시글 삭제 완료"));
    }

}