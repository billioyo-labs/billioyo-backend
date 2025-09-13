package com.itemrental.rentalService.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityPostCreateResponseDto {
  private String message;
  private String title;
  private String content;
}

