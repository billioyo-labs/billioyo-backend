package com.itemrental.rentalService.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itemrental.rentalService.domain.chat.entity.ChattingParticipant;
import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import com.itemrental.rentalService.domain.chat.entity.Message;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Comparator;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomResponse {
    private Long roomId;
    private String title;
    private String lastMessage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastMessageTime;
    private String opponentEmail;
    private String opponentNickname;

    @Builder(access = AccessLevel.PRIVATE)
    private ChatRoomResponse(Long roomId, String title, String lastMessage,
                             LocalDateTime lastMessageTime, String opponentEmail, String opponentNickname) {
        this.roomId = roomId;
        this.title = title;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.opponentEmail = opponentEmail;
        this.opponentNickname = opponentNickname;
    }

    public static ChatRoomResponse from(ChattingRoom room, String myEmail) {
        Message lastMsg = room.getMessages().stream()
                .max(Comparator.comparing(
                        m -> m.getSendTime() != null ? m.getSendTime() : LocalDateTime.MIN
                ))
                .orElse(null);

        ChattingParticipant opponent = room.getParticipants().stream()
                .filter(p -> !p.getUser().getEmail().equals(myEmail))
                .findFirst()
                .orElse(null);

        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .lastMessage(lastMsg != null ? lastMsg.getContent() : "메시지가 없습니다.")
                .lastMessageTime(lastMsg != null && lastMsg.getSendTime() != null
                        ? lastMsg.getSendTime()
                        : (room.getCreated_at() != null ? room.getCreated_at() : LocalDateTime.now()))
                .opponentEmail(opponent != null ? opponent.getUser().getEmail() : myEmail)
                .opponentNickname(opponent != null ? opponent.getUser().getNickName() : "나와의 채팅")
                .build();
    }
}
