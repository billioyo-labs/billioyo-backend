package com.itemrental.rentalService.dto;

import com.itemrental.rentalService.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignUpDto {
    private String email;
    private String name;
    private String password;
    private String passwordConfirmation;
    public User toEntity(String encodedPassword, List<String> roles){
        return User.builder()
                .username(name)
                .email(email)
                .password(encodedPassword)
                .emailVerified(false)
                .roles(roles)
                .build();
    }
}
