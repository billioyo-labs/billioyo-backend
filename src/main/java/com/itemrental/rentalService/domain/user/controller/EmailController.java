package com.itemrental.rentalService.domain.user.controller;

import com.itemrental.rentalService.domain.user.dto.request.EmailVerificationRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.PasswordResetRequestDto;
import com.itemrental.rentalService.domain.user.service.MailService;
import com.itemrental.rentalService.domain.user.service.UserService;
import com.itemrental.rentalService.global.response.ApiResponse;
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
@RequestMapping("/mail")
public class EmailController {
    private final MailService mailService;
    private final UserService userService;

    @PostMapping("/send-verification-link")
    public ResponseEntity<ApiResponse<Void>> sendVerificationLink(
            @Valid @RequestBody EmailVerificationRequestDto dto) {
        userService.processInitialRegistration(dto.getEmail());
        mailService.sendVerificationMail(dto.getEmail());

        return ResponseEntity.ok(ApiResponse.success("인증 링크 이메일 전송 요청 완료"));
    }

    @PostMapping("/send-reset-link")
    public ResponseEntity<ApiResponse<Void>> sendResetPassword(@Valid @RequestBody PasswordResetRequestDto dto) {
        String temporalPassword = userService.resetToTemporalPassword(dto.getEmail(), dto.getName());
        mailService.sendTemporalPasswordMail(dto.getEmail(), temporalPassword);

        return ResponseEntity.ok(ApiResponse.success("임시 비밀번호 이메일 전송 요청 완료"));
    }
}
