package com.itemrental.rentalService.dto;

import com.itemrental.rentalService.entity.User;
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
