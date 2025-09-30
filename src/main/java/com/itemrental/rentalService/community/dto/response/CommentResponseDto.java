package com.itemrental.rentalService.community.dto.response;


import com.itemrental.rentalService.community.entity.CommunityPost;
import com.itemrental.rentalService.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponseDto {
  private Long id;
  private String username;
  private String comment;
  private LocalDateTime createdAt;

}
