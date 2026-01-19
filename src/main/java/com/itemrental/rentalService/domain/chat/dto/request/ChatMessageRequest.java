package com.itemrental.rentalService.domain.chat.dto.request;

import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import com.itemrental.rentalService.domain.chat.entity.Message;
import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessageRequest {
    @NotNull(message = "채팅방 ID는 필수입니다.")
    private Long roomId;

    @NotBlank(message = "메시지 내용은 비어 있을 수 없습니다.")
    private String content;

    @Builder
    private ChatMessageRequest(Long roomId, String content) {
        this.roomId = roomId;
        this.content = content;
    }

    public Message toEntity(ChattingRoom room, User sender) {
        return Message.builder()
                .content(this.content)
                .chattingRoom(room)
                .user(sender)
                .build();
    }
}
