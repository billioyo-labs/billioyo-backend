package com.itemrental.rentalService.community.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommunityPostReadResponseDto {
  private String username;
  private String title;
  private String content;
  private LocalDateTime createdAt;
}
