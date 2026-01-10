package com.itemrental.rentalService.domain.chat.controller;

import com.itemrental.rentalService.domain.chat.dto.ChatMessage;
import com.itemrental.rentalService.domain.chat.dto.ChatRoomCreateRequest;
import com.itemrental.rentalService.domain.chat.dto.ChatRoomResponse;
import com.itemrental.rentalService.domain.chat.dto.MessageResponse;
import com.itemrental.rentalService.domain.chat.service.ChatService;
import com.itemrental.rentalService.domain.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;
    private final MessageService messageService;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(Principal principal) {
        return ResponseEntity.ok(chatService.getMyRooms(principal.getName()));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Slice<MessageResponse>> getHistory(
            @PathVariable Long roomId,
            @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(chatService.getMessageHistory(roomId, pageable));
    }

    @PostMapping("/rooms")
    public ResponseEntity<Long> createRoom(@RequestBody ChatRoomCreateRequest request) {
        return ResponseEntity.ok(chatService.createRoom(request.getTitle(), request.getUserIds()));
    }
}
