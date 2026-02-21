package com.itemrental.billioyo.domain.user.controller;

import com.itemrental.billioyo.domain.user.dto.request.AdminSignUpRequestDto;
import com.itemrental.billioyo.domain.user.dto.request.UserFindAccountRequestDto;
import com.itemrental.billioyo.domain.user.dto.request.UserSignUpRequestDto;
import com.itemrental.billioyo.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.itemrental.billioyo.domain.user.service.UserService;
import com.itemrental.billioyo.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "회원/계정 관리 API")
public class UserController {

    private final UserService userService;


    @Operation(
        summary = "회원가입",
        description = "사용자가 회원가입 버튼을 클릭했을 때 호출되는 API입니다."
    )
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody UserSignUpRequestDto userSignUpRequestDto) {
        userService.signUp(userSignUpRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
    }

    @Operation(
        summary = "관리자 회원가입",
        description = "관리자 계정을 생성할 때 호출되는 API입니다."
    )
    @PostMapping("/sign-up/admin")
    public ResponseEntity<ApiResponse<Void>> signUpAdmin(@Valid @RequestBody AdminSignUpRequestDto signUpDto) {
        userService.signUpAdmin(signUpDto.getUserSignUpRequestDto(), signUpDto.getAdminSecret());
        return ResponseEntity.ok(ApiResponse.success("관리자 가입이 완료되었습니다."));
    }

    @Operation(
        summary = "닉네임 중복 체크",
        description = "회원가입/프로필 수정 화면에서 닉네임 중복 여부를 확인할 때 호출되는 API입니다."
    )
    @GetMapping("/duplicate-check")
    public ResponseEntity<ApiResponse<Void>> duplicateCheck(@RequestParam("nickName") String nickName) {
        userService.duplicateCheck(nickName);
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 아이디입니다."));
    }

    @Operation(
        summary = "계정 찾기",
        description = "사용자가 계정 찾기(이메일 찾기)를 요청했을 때 호출되는 API입니다."
    )
    @PostMapping("/find-account")
    public ResponseEntity<ApiResponse<String>> findAccount(@Valid @RequestBody UserFindAccountRequestDto userFindAccountRequestDto) {
        String email = userService.findAccount(userFindAccountRequestDto.getPhoneNumber());
        return ResponseEntity.ok(ApiResponse.success("계정 찾기 성공", email));
    }

    @Operation(
        summary = "회원정보 수정",
        description = "사용자가 프로필 수정 저장 버튼을 클릭했을 때 호출되는 API입니다."
    )
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateUser(@Valid @RequestBody UserProfileUpdateRequestDto userProfileUpdateRequestDto, Principal principal) {
        userService.updateUser(principal.getName(), userProfileUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원 정보가 수정되었습니다."));
    }

    @Operation(
        summary = "회원 탈퇴",
        description = "사용자가 회원 탈퇴 버튼을 클릭했을 때 호출되는 API입니다."
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(Principal principal) {
        userService.deleteUser(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 처리되었습니다."));
    }


}