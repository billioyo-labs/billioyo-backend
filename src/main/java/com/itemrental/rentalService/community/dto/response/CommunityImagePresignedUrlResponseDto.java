package com.itemrental.rentalService.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityImagePresignedUrlResponseDto {
  private String originalFileName;
  private String objectKey;
  private String uploadUrl;
  private String imageUrl;
}