package com.itemrental.billioyo.domain.chat.controller;

import com.itemrental.billioyo.domain.chat.dto.request.ChatMessageRequest;
import com.itemrental.billioyo.domain.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        messageService.saveMessage(request, principal.getName());
    }
}
