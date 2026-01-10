package com.itemrental.rentalService.domain.chat.dto;

import com.itemrental.rentalService.domain.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long messageId;
    private String content;
    private String senderName;
    private LocalDateTime sendTime;

    public MessageResponse(Message message) {
        this.messageId = message.getId();
        this.content = message.getContent();
        this.senderName = message.getUser().getEmail();
        this.sendTime = message.getSendTime();
    }
}
