package com.itemrental.rentalService.dto;

import com.itemrental.rentalService.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Email(message = "이메일의 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;
    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    private String nickName;
    @NotBlank(message = "비밀번호은 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 8자리 이상, 영문/숫자/특수문자를 포함해야 합니다.")
    private String password;
    @NotBlank(message = "휴대폰 번호는 필수 입력값입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$",
            message = "010-0000-0000 형식으로 입력해주세요.")
    private String phoneNumber;
    @NotBlank(message = "생년월일은 필수 입력값입니다.")
    @Pattern(regexp = "^\\d{6}$",
            message = "YYMMDD 형식으로 입력해주세요.")
    private String birthDate;
    public User toEntity(String encodedPassword, List<String> roles){
        return User.builder()
                .username(name)
                .email(email)
                .nickName(nickName)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .password(encodedPassword)
                .userState(User.UserState.ACTIVE)
                .roles(roles)
                .build();
    }
}
