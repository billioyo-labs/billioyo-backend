package com.itemrental.rentalService.community.dto.response;


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
