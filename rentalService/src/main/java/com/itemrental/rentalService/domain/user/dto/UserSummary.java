package com.itemrental.rentalService.domain.user.dto;

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
}

