package com.itemrental.rentalService.community.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
public class CommunityPostCreateRequestDto {
  private String title;
  private String content;
}
