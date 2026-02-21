package com.itemrental.billioyo.domain.user.dto.request;

import com.itemrental.billioyo.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfileUpdateRequestDto {
    private String email; // update 불가, user 조회만
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;
    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    private String nickName;
    @NotBlank(message = "휴대폰 번호는 필수 입력값입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$",
        message = "010-0000-0000 형식으로 입력해주세요.")
    private String phoneNumber;
    @NotBlank(message = "생년월일은 필수 입력값입니다.")
    @Pattern(regexp = "^\\d{6}$",
        message = "YYMMDD 형식으로 입력해주세요.")
    private String birthDate;

    public static UserProfileUpdateRequestDto from(User user) {
        return new UserProfileUpdateRequestDto(
                user.getEmail(),
                user.getUsername(),
                user.getNickName(),
                user.getPhoneNumber(),
                user.getBirthDate()
        );
    }
}
