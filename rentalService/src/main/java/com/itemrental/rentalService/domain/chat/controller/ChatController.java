package com.itemrental.rentalService.domain.chat.controller;

import com.itemrental.rentalService.domain.chat.dto.ChatMessage;
import com.itemrental.rentalService.domain.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal){
        String sender = principal.getName();
        chatMessage.setSender(sender);
        System.out.println("보내는 사람(Principal): " + sender);
        System.out.println("받는 사람(Receiver): " + chatMessage.getReceiver());

        messageService.saveMessage(chatMessage);

        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(),
                "/queue/messages",
                chatMessage
        );

        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(),
                "/queue/notifications",
                chatMessage
        );

        messagingTemplate.convertAndSendToUser(
                sender,
                "/queue/messages",
                chatMessage
        );
    }
}
