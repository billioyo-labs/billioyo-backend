package com.itemrental.rentalService.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class CommunityPostUpdateRequestDto {
  private String title;
  private String content;
  private List<String> imageUrls;
}
