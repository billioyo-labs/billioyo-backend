package com.itemrental.rentalService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FcmTokenRequest {
    private String fcmToken;
    private String deviceName;
}
