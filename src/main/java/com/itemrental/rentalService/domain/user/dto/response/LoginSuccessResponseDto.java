package com.itemrental.rentalService.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginSuccessResponseDto {
    private String birthDate;
    private String phoneNumber;
    private String email;
    private Long id;
    private String username;
    private String nickName;
}
