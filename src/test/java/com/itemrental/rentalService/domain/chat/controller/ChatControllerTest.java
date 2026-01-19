package com.itemrental.rentalService.domain.chat.controller;

import com.itemrental.rentalService.domain.chat.dto.request.ChatMessageRequest;
import com.itemrental.rentalService.domain.chat.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("성공: WebSocket을 통해 메시지를 전송하면 서비스를 호출한다")
    @WithMockUser(username = "sender@test.com")
    void sendMessage_Success() {
        ChatController chatController = new ChatController(messageService);
        ChatMessageRequest request = ChatMessageRequest.builder()
                .roomId(1L)
                .content("Hello")
                .build();
        Principal principal = () -> "sender@test.com";

        // when
        chatController.sendMessage(request, principal);

        // then
        verify(messageService).saveMessage(any(ChatMessageRequest.class), eq("sender@test.com"));
    }
}
