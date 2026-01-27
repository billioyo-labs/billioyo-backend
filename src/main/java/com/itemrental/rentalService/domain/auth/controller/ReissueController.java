package com.itemrental.rentalService.domain.auth.controller;

import com.itemrental.rentalService.global.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reissue")
@Tag(name = "Auth", description = "토큰 재발급 API")
public class ReissueController {
    private final JwtService jwtService;

    @Operation(
        summary = "Access Token 재발급",
        description =
            "Refresh Token을 검증하여 새로운 Access Token을 재발급합니다. Refresh Token은 요청 쿠키 또는 헤더에 포함되어야 합니다."
    )
    @PostMapping("/accessToken")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return jwtService.recreateJwt(request, response);
    }
}
