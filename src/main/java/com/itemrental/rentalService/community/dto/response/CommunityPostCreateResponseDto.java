package com.itemrental.rentalService.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityPostCreateResponseDto {
  private Long id;
  private String category;
  private String username;
  private String title;
  private String content;
}

