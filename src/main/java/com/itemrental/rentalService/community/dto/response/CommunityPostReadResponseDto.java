package com.itemrental.rentalService.community.dto.response;
import com.itemrental.rentalService.community.entity.CommunityPostImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommunityPostReadResponseDto {
  private String username;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private List<CommunityPostImage> imageUrls;
  private int viewCount;
  private int likeCount;
  private List<CommentResponseDto> comments;
}
