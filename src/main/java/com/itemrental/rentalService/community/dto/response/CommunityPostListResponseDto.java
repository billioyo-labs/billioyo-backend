package com.itemrental.rentalService.community.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommunityPostListResponseDto {
  private Long postId;
  private String title;
  private String authorName;
  private int likeCount;
  private int viewCount;
  private LocalDateTime createdAt;
  private String thumbnailUrl;
}
