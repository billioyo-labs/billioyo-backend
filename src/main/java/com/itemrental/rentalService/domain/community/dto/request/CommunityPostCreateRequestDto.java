package com.itemrental.rentalService.domain.community.dto.request;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class CommunityPostCreateRequestDto {
  private String category;
  private String title;
  private String content;
  private String location;
  private Double lat;
  private Double lng;
  private List<String> imageUrls;
}
