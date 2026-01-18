package com.itemrental.rentalService.domain.user.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminSignUpRequestDto {
    @Valid
    private UserSignUpRequestDto userSignUpRequestDto;
    private String adminSecret;
}
