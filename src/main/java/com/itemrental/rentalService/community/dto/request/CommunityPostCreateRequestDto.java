package com.itemrental.rentalService.community.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CommunityPostCreateRequestDto {
  private String title;
  private String content;
}
