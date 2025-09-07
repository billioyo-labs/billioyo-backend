package com.itemrental.rentalService.controller;

import com.itemrental.rentalService.utils.JwtService;
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
public class ReissueController {
    private final JwtService jwtService;

    @PostMapping("/accessToken")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
        return jwtService.recreateJwt(request, response);
    }
}
