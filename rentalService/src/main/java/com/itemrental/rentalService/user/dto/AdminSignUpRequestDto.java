package com.itemrental.rentalService.user.dto;

import lombok.Getter;

@Getter
public class AdminSignUpRequestDto {
  private SignUpDto signUpDto;
  private String adminSecret;
}
