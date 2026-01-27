package com.itemrental.rentalService.domain.user.controller;

import com.itemrental.rentalService.domain.user.dto.request.EmailVerificationRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.PasswordResetRequestDto;
import com.itemrental.rentalService.domain.user.service.MailService;
import com.itemrental.rentalService.domain.user.service.UserService;
import com.itemrental.rentalService.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Tag(name = "Mail", description = "이메일 인증/비밀번호 재설정 API")
public class EmailController {
    private final MailService mailService;
    private final UserService userService;

    @Operation(
        summary = "이메일 인증 링크 전송",
        description =
            "회원가입 과정에서 사용자가 이메일 인증을 요청했을 때 호출되는 API입니다. 이메일 인증 링크를 포함한 메일을 전송합니다."
    )
    @PostMapping("/send-verification-link")
    public ResponseEntity<ApiResponse<Void>> sendVerificationLink(
            @Valid @RequestBody EmailVerificationRequestDto dto) {
        userService.processInitialRegistration(dto.getEmail());
        mailService.sendVerificationMail(dto.getEmail());

        return ResponseEntity.ok(ApiResponse.success("인증 링크 이메일 전송 요청 완료"));
    }

    @Operation(
        summary = "비밀번호 재설정 메일 전송",
        description =
            "비밀번호 찾기 화면에서 사용자가 비밀번호 재설정을 요청했을 때 호출되는 API입니다. 임시 비밀번호를 생성하고 이메일로 전송합니다."
    )

    @PostMapping("/send-reset-link")
    public ResponseEntity<ApiResponse<Void>> sendResetPassword(@Valid @RequestBody PasswordResetRequestDto dto) {
        String temporalPassword = userService.resetToTemporalPassword(dto.getEmail(), dto.getName());
        mailService.sendTemporalPasswordMail(dto.getEmail(), temporalPassword);

        return ResponseEntity.ok(ApiResponse.success("임시 비밀번호 이메일 전송 요청 완료"));
    }
}
