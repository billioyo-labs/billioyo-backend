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

    public CommunityPostCreateRequestDto(String category, String title, String content, String location, double latitude, double longitude, List<String> imageUrls) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrls = imageUrls;
    }
}
