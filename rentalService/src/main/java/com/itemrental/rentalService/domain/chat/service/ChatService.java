package com.itemrental.rentalService.domain.chat.service;

import com.itemrental.rentalService.domain.chat.dto.ChatRoomResponse;
import com.itemrental.rentalService.domain.chat.dto.MessageResponse;
import com.itemrental.rentalService.domain.chat.entity.ChattingParticipant;
import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import com.itemrental.rentalService.domain.chat.repository.ChattingParticipantRepository;
import com.itemrental.rentalService.domain.chat.repository.ChattingRoomRepository;
import com.itemrental.rentalService.domain.chat.repository.MessageRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChattingRoomRepository roomRepository;
    private final ChattingParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createRoom(String title, List<Long> userIds) {
        if (userIds.size() == 2) {
            Long user1Id = userIds.get(0);
            Long user2Id = userIds.get(1);

            Optional<ChattingRoom> existingRoom = participantRepository.findCommonRoom(user1Id, user2Id);
            if (existingRoom.isPresent()) {
                return existingRoom.get().getId();
            }
        }

        ChattingRoom room = ChattingRoom.builder().title(title).build();
        roomRepository.save(room);

        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElseThrow();
            ChattingParticipant participant = ChattingParticipant.builder()
                .user(user)
                .chattingRoom(room)
                .unreadCount(0L)
                .build();
            participantRepository.save(participant);
        }
        return room.getId();
    }

    public List<ChatRoomResponse> getMyRooms(String userEmail) {
        return participantRepository.findAllByUserEmail(userEmail).stream()
            .map(p -> new ChatRoomResponse(p.getChattingRoom(), userEmail)) // 현재 내 이메일 전달
            .toList();
    }

    public Slice<MessageResponse> getMessageHistory(Long roomId, Pageable pageable) {
        return messageRepository.findAllByChattingRoomIdOrderBySendTimeDesc(roomId, pageable)
            .map(MessageResponse::new);
    }
}
