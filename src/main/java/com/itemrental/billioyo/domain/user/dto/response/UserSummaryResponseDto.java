package com.itemrental.billioyo.domain.user.dto.response;

import com.itemrental.billioyo.domain.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryResponseDto {
    private Long id;
    private String email;
    private String name;
    private String nickname;

    public static UserSummaryResponseDto from(User user) {
        if (user == null) return null;
        return UserSummaryResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getUsername())
                .nickname(user.getNickName())
                .build();
    }
}

