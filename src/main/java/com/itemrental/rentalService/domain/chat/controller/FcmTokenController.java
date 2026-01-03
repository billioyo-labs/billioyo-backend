package com.itemrental.rentalService.domain.chat.controller;

import com.itemrental.rentalService.domain.chat.dto.FcmTokenRequest;
import com.itemrental.rentalService.domain.chat.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    @PostMapping("/token")
    public ResponseEntity<Void> registerToken(@RequestBody FcmTokenRequest request, Principal principal) throws Exception{
        String Email = principal.getName();

        fcmTokenService.saveOrUpdateToken(Email, request.getFcmToken(), request.getDeviceName());

        return ResponseEntity.ok().build();
    }
}
