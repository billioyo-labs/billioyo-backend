package com.itemrental.rentalService.domain.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itemrental.rentalService.domain.chat.dto.request.ChatRoomCreateRequest;
import com.itemrental.rentalService.domain.chat.exception.ParticipantNotFoundException;
import com.itemrental.rentalService.domain.chat.service.ChatService;
import com.itemrental.rentalService.domain.chat.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRoomController.class)
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("성공: 내 채팅방 목록을 조회한다")
    @WithMockUser(username = "test@test.com")
    void getMyRooms_Success() throws Exception {
        // given
        given(chatService.getMyRooms("test@test.com")).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/chat/rooms")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("성공: 채팅방 생성 요청이 올바르면 200 OK를 반환한다")
    @WithMockUser
    void createRoom_Controller_Success() throws Exception {
        // given
        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .title("신규 채팅방")
                .userIds(List.of(1L, 2L))
                .build();

        given(chatService.createRoom(anyString(), anyList())).willReturn(1L);

        // when & then
        mockMvc.perform(post("/chat/rooms")
                        .with(csrf()) // CSRF 토큰 포함
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("성공: 특정 방의 메시지 내역을 페이징하여 조회한다")
    @WithMockUser
    void getHistory_Success() throws Exception {
        // given
        Long roomId = 1L;
        given(chatService.getMessageHistory(eq(roomId), any(Pageable.class)))
                .willReturn(new SliceImpl<>(List.of()));

        // when & then
        mockMvc.perform(get("/chat/rooms/{roomId}/messages", roomId)
                        .param("page", "0")
                        .param("size", "30"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("성공: 채팅 메시지를 읽음 처리한다")
    @WithMockUser(username = "user@test.com")
    void markAsRead_Success() throws Exception {
        // given
        Long roomId = 1L;

        // when & then
        mockMvc.perform(patch("/chat/rooms/{roomId}/read", roomId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(chatService).markAsRead(roomId, "user@test.com");
    }

    @Test
    @DisplayName("실패: 참여하지 않은 방의 메시지 내역 조회 시 404 에러를 반환한다")
    @WithMockUser
    void getHistory_Fail_AccessDenied() throws Exception {
        // given
        Long roomId = 999L;
        given(chatService.getMessageHistory(eq(roomId), any(Pageable.class)))
                .willThrow(new ParticipantNotFoundException());

        // when & then
        mockMvc.perform(get("/chat/rooms/{roomId}/messages", roomId))
                .andExpect(status().isNotFound());
    }
}
