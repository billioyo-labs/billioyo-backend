package com.itemrental.rentalService.domain.chat.controller;

import com.itemrental.rentalService.domain.chat.dto.request.ChatRoomCreateRequest;
import com.itemrental.rentalService.domain.chat.dto.response.ChatRoomResponse;
import com.itemrental.rentalService.domain.chat.dto.response.MessageResponse;
import com.itemrental.rentalService.domain.chat.service.ChatService;
import com.itemrental.rentalService.domain.chat.service.MessageService;
import com.itemrental.rentalService.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getMyRooms(Principal principal) {
        List<ChatRoomResponse> rooms = chatService.getMyRooms(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("조회 성공", rooms));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<Slice<MessageResponse>>> getHistory(
            @PathVariable Long roomId,
            @PageableDefault(size = 30) Pageable pageable) {
        Slice<MessageResponse> history = chatService.getMessageHistory(roomId, pageable);
        return ResponseEntity.ok(ApiResponse.success("메시지 내역 조회 성공", history));
    }

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<Long>> createRoom(@RequestBody ChatRoomCreateRequest request) {
        Long roomId = chatService.createRoom(request.getTitle(), request.getUserIds());
        return ResponseEntity.ok(ApiResponse.success("채팅방 생성 성공", roomId));
    }

    @PatchMapping("/rooms/{roomId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long roomId, Principal principal) {
        chatService.markAsRead(roomId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("읽음 처리 완료"));
    }
}
