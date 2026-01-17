package com.itemrental.rentalService.domain.user.dto;

import com.itemrental.rentalService.domain.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummary {
    private Long id;
    private String email;
    private String name;
    private String nickname;

    public static UserSummary from(User user) {
        if (user == null) return null;
        return UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getUsername())
                .nickname(user.getNickName())
                .build();
    }
}

