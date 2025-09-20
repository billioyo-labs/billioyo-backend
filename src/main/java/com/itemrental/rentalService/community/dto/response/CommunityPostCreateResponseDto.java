package com.itemrental.rentalService.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityPostCreateResponseDto {
  private Long id;
  private String message;
  private String title;
  private String content;
}

