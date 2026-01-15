package com.itemrental.rentalService.domain.user.controller;

import com.itemrental.rentalService.domain.user.dto.SendEmailVerificationDto;
import com.itemrental.rentalService.domain.user.dto.SendResetMailDto;
import com.itemrental.rentalService.domain.user.service.MailService;
import com.itemrental.rentalService.global.response.ApiResponse;
import jakarta.mail.MessagingException;
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

    @PostMapping("/send-verification-link")
    public ResponseEntity<ApiResponse<Void>> sendVerificationLink(@Valid @RequestBody() SendEmailVerificationDto sendEmailVerificationDto) throws MessagingException {
        mailService.sendVerificationMail(sendEmailVerificationDto.getEmail());
        return ResponseEntity.ok(ApiResponse.success("인증 링크 이메일 전송"));
    }

    @PostMapping("/send-reset-link")
    public ResponseEntity<ApiResponse<Void>> sendResetPassword(@Valid @RequestBody() SendResetMailDto sendResetMailDto) throws MessagingException {
        mailService.sendTemporalPasswordMail(sendResetMailDto);
        return ResponseEntity.ok(ApiResponse.success("임시 비밀번호 이메일 전송"));
    }
}
