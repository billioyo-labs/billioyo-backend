package com.itemrental.billioyo.domain.community.dto.response;


import com.itemrental.billioyo.domain.community.entity.CommunityPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommunityPostListResponseDto {
    private Long id;
    private String category;
    private String title;
    private String authorName;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
    private String location;

    public CommunityPostListResponseDto(CommunityPost post) {
        this.id = post.getId();
        this.category = post.getCategory().name();
        this.title = post.getTitle();
        this.authorName = post.getUser().getUsername();
        this.likeCount = post.getLikeCount();
        this.viewCount = post.getViewCount();
        this.commentCount = post.getCommentCount();
        this.createdAt = post.getCreatedAt();
        this.location = post.getLocation();

        this.thumbnailUrl = (post.getImages() != null && !post.getImages().isEmpty())
                ? post.getImages().get(0).getImageUrl()
                : null;
    }
}
