package com.itemrental.rentalService.domain.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class CommunityPostUpdateRequestDto {
    private String title;
    private String content;
    private List<String> imageUrls;

    public CommunityPostUpdateRequestDto(String title, String content, List<String> imageUrls) {
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
    }
}
