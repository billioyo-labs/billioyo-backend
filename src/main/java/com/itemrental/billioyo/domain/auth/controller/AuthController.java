package com.itemrental.billioyo.domain.auth.controller;

import com.itemrental.billioyo.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
@Tag(name = "Auth", description = "이메일 인증/비밀번호 재설정 관련 엔드포인트")
public class AuthController {
    private final AuthService authService;


    @Operation(
        summary = "이메일 인증 처리",
        description = "이메일 인증 토큰(token)을 검증하고, 성공/실패에 따라 리다이렉트합니다."
    )
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token) {
        if (authService.verifyVerificationToken(token)) {
            return "redirect:https://rental-project-billioyo.vercel.app/signup?message=email_verified";
        } else {
            return "redirect:/auth/error?reason=invalid_token";
        }
    }

    @Operation(
        summary = "비밀번호 재설정 페이지 진입",
        description = "비밀번호 재설정 토큰(token)을 검증하고, 유효하면 reset-password 뷰를 반환합니다."
    )
    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {
        if (authService.verifyResetToken(token)) {
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            return "redirect:/auth/error?reason=invalid_token";
        }
    }

    @Operation(
        summary = "에러 페이지",
        description = "인증/재설정 과정에서 발생한 오류 사유(reason)에 따라 에러 페이지를 반환합니다."
    )
    @GetMapping("/error")
    public String showErrorPage(@RequestParam("reason") String reason, Model model) {
        if ("invalid_token".equals(reason)) {
            model.addAttribute("errorMessage", "유효하지 않거나 만료된 토큰입니다. 다시 시도해 주세요.");
        }

        return "error-page";
    }
}
