package com.itemrental.rentalService.controller;

import com.itemrental.rentalService.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token){
        if(authService.verifyVerificationToken(token)){
            return "redirect:https://rental-project-billioyo.vercel.app/signup?message=email_verified";
        }else{
            return "redirect:/auth/error?reason=invalid_token";
        }
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model){
        if(authService.verifyResetToken(token)){
            model.addAttribute("token", token);
            return "reset-password";
        }else{
            return "redirect:/auth/error?reason=invalid_token";
        }
    }

    @GetMapping("/error")
    public String showErrorPage(@RequestParam("reason") String reason, Model model){
        if("invalid_token".equals(reason)){
            model.addAttribute("errorMessage", "유효하지 않거나 만료된 토큰입니다. 다시 시도해 주세요.");
        }

        return "error-page";
    }
}
