package com.itemrental.rentalService.domain.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginSuccessDto {
    private String birthDate;
    private String phoneNumber;
    private String email;
    private Long id;
    private String username;
    private String nickName;
}
