package com.itemrental.rentalService.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommunityPostCreateResponseDto {
  private Long id;
  private String username;
  private String title;
  private String content;
  private List<String> imageUrls;
}

