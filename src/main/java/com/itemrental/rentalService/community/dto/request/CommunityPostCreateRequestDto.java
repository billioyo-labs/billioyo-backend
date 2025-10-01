package com.itemrental.rentalService.community.dto.request;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class CommunityPostCreateRequestDto {
  private String category;
  private String title;
  private String content;
  private List<String> imageUrls;
}
