package com.itemrental.rentalService.domain.chat.controller;

import com.itemrental.rentalService.domain.chat.dto.request.ChatRoomCreateRequest;
import com.itemrental.rentalService.domain.chat.dto.response.ChatRoomResponse;
import com.itemrental.rentalService.domain.chat.dto.response.MessageResponse;
import com.itemrental.rentalService.domain.chat.service.ChatService;
import com.itemrental.rentalService.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chat", description = "채팅방/메시지 조회 API")
public class ChatRoomController {
    private final ChatService chatService;


    @Operation(
        summary = "내 채팅방 목록 조회",
        description = "로그인한 사용자가 참여 중인 채팅방 목록을 조회합니다."
    )
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getMyRooms(Principal principal) {
        List<ChatRoomResponse> rooms = chatService.getMyRooms(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("조회 성공", rooms));
    }

    @Operation(
        summary = "채팅방 메시지 내역 조회",
        description = "특정 채팅방(roomId)의 메시지 내역을 페이징(Slice)으로 조회합니다."
    )
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<Slice<MessageResponse>>> getHistory(
            @PathVariable Long roomId,
            @PageableDefault(size = 30) Pageable pageable) {
        Slice<MessageResponse> history = chatService.getMessageHistory(roomId, pageable);
        return ResponseEntity.ok(ApiResponse.success("메시지 내역 조회 성공", history));
    }

    @Operation(
        summary = "채팅방 생성",
        description = "채팅방 제목(title)과 참여자(userIds) 정보를 받아 채팅방을 생성합니다."
    )
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<Long>> createRoom(@RequestBody ChatRoomCreateRequest request) {
        Long roomId = chatService.createRoom(request.getTitle(), request.getUserIds());
        return ResponseEntity.ok(ApiResponse.success("채팅방 생성 성공", roomId));
    }

    @Operation(
        summary = "채팅방 읽음 처리",
        description = "로그인한 사용자가 특정 채팅방(roomId) 메시지를 읽음 처리합니다."
    )
    @PatchMapping("/rooms/{roomId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long roomId, Principal principal) {
        chatService.markAsRead(roomId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("읽음 처리 완료"));
    }
}
