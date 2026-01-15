package com.itemrental.rentalService.domain.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CommunityPostCreateRequestDto {
    private String category;
    private String title;
    private String content;
    private String location;
    private Double latitude;
    private Double longitude;
    private List<String> imageUrls;
}
