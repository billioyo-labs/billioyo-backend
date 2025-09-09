package com.itemrental.rentalService.community;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDto {
  private Long id;
  private String title;
  private String content;
  private LocalDateTime createdAt;

  public static CommunityDto fromEntity(Community post) {
    return CommunityDto.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .createdAt(post.getCreatedAt())
        .build();
   }
}
