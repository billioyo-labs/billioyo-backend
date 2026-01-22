package com.itemrental.rentalService.domain.community.dto.response;


import com.itemrental.rentalService.domain.community.entity.CommunityComment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    public static CommentResponseDto from(CommunityComment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getUsername(),
                comment.getComment(),
                comment.getCreatedAt()
        );
    }
}
