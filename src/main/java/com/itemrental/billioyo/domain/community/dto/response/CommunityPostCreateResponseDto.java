package com.itemrental.billioyo.domain.community.dto.response;

import com.itemrental.billioyo.domain.community.entity.CommunityPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityPostCreateResponseDto {
    private Long id;
    private String category;
    private String username;
    private String title;
    private String content;

    public CommunityPostCreateResponseDto(CommunityPost post) {
        this.id = post.getId();
        this.category = post.getCategory().name();
        this.username = post.getUser().getUsername();
        this.title = post.getTitle();
        this.content = post.getContent();
    }
}

