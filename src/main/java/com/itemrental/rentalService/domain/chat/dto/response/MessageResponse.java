package com.itemrental.rentalService.domain.chat.dto.response;

import com.itemrental.rentalService.domain.chat.entity.Message;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageResponse {
    private Long messageId;
    private Long roomId;
    private String content;
    private String senderEmail;
    private String senderNickname;
    private LocalDateTime sendTime;

    @Builder(access = AccessLevel.PRIVATE)
    private MessageResponse(Long messageId, Long roomId, String content,
                            String senderEmail, String senderNickname, LocalDateTime sendTime) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.content = content;
        this.senderEmail = senderEmail;
        this.senderNickname = senderNickname;
        this.sendTime = sendTime;
    }

    public static MessageResponse from(Message message) {
        return MessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChattingRoom().getId())
                .content(message.getContent())
                .senderEmail(message.getUser().getEmail())
                .senderNickname(message.getUser().getNickName())
                .sendTime(message.getSendTime())
                .build();
    }
}
