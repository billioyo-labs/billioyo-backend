package com.itemrental.rentalService.community.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommunityPostList {
  private int postId;
  private String title;
  private LocalDateTime createdAt;
  private int viewCount;
  private int likeCount;
}
