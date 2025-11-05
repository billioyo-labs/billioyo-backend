package com.itemrental.rentalService.controller;

import com.itemrental.rentalService.dto.ChatMessage;
import com.itemrental.rentalService.service.MessageService;
import com.itemrental.rentalService.service.PushService;
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
    private final SimpUserRegistry userRegistry;
    private final MessageService messageService;
    private final PushService pushService;

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal){
        String sender = principal.getName();
        chatMessage.setSender(sender);
        String receiver = chatMessage.getReceiver();

        boolean isUserConnected = checkUserConnected(receiver);

        if(isUserConnected) {
            messagingTemplate.convertAndSendToUser(
                    receiver,
                    "/queue/messages",
                    chatMessage
            );
        }else{
            messageService.saveOfflineMessage(chatMessage);
        }

    }

    private boolean checkUserConnected(String username){
        return userRegistry.getUser(username) != null;
    }

}
