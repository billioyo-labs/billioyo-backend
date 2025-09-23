package com.itemrental.rentalService.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class CommunityPostUpdateRequestDto {
  private String title;
  private String content;
}
