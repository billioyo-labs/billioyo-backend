package com.itemrental.rentalService.controller;

import com.itemrental.rentalService.dto.ApiResponse;
import com.itemrental.rentalService.dto.SendMailDto;
import com.itemrental.rentalService.service.AuthService;
import com.itemrental.rentalService.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final MailService mailService;
    private final AuthService authService;

    @PostMapping("/send-verification-link")
    public ResponseEntity<ApiResponse> sendEmail(@RequestBody()SendMailDto sendMailDto) throws MessagingException {
        mailService.sendVerificationMail(sendMailDto.getEmail(), sendMailDto.getName());
        return ResponseEntity.ok(ApiResponse.success("이메일 전송"));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token") String token){
        authService.verifyToken(token);
        return ResponseEntity.ok(ApiResponse.success("인증 성공"));
    }
}
