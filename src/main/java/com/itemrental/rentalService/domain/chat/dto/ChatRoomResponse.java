package com.itemrental.rentalService.domain.chat.dto;

import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import com.itemrental.rentalService.domain.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    private Long roomId;
    private String title;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String opponentEmail;
    private String opponentNickname;

    public ChatRoomResponse(ChattingRoom room, String myEmail) {
        this.roomId = room.getId();
        this.title = room.getTitle();

        if (!room.getMessages().isEmpty()) {
            Message lastMsg = room.getMessages().get(room.getMessages().size() - 1);
            this.lastMessage = lastMsg.getContent();
            this.lastMessageTime = lastMsg.getSendTime();
        }

        room.getParticipants().stream()
            .filter(p -> !p.getUser().getEmail().equals(myEmail)) // 이메일로 정확히 비교
            .findFirst()
            .ifPresent(opponent -> {
                this.opponentNickname = opponent.getUser().getNickName();
                this.opponentEmail = opponent.getUser().getEmail();
            });

        if (this.opponentNickname == null) {
            this.opponentNickname = "나와의 채팅";
            this.opponentEmail = myEmail;
        }
    }
}
