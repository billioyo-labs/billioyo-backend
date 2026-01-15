package com.itemrental.rentalService.domain.chat.service;

import com.itemrental.rentalService.domain.chat.dto.ChatMessage;
import com.itemrental.rentalService.domain.chat.dto.MessageResponse;
import com.itemrental.rentalService.domain.chat.entity.ChattingParticipant;
import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import com.itemrental.rentalService.domain.chat.entity.Message;
import com.itemrental.rentalService.domain.chat.repository.ChattingParticipantRepository;
import com.itemrental.rentalService.domain.chat.repository.ChattingRoomRepository;
import com.itemrental.rentalService.domain.chat.repository.MessageRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final UserRepository userRepository;
    private final ChattingRoomRepository roomRepository;
    private final ChattingParticipantRepository participantRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public MessageResponse saveMessage(ChatMessage chatMessage) {
        // 1. 방 찾기
        ChattingRoom room = roomRepository.findById(chatMessage.getRoomId())
            .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));

        // 2. 보낸 사람 찾기 (chatMessage.getSender()에 이메일이 담겨와야 함)
        User sender = userRepository.findByEmail(chatMessage.getSender())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + chatMessage.getSender()));

        // 3. 메시지 객체 생성 (이때 찾은 sender를 넣어줘야 user_id가 정상 저장됨)
        Message message = Message.builder()
            .content(chatMessage.getContent())
            .user(sender) // 여기서 sender(user_id)가 결정됨
            .chattingRoom(room)
            .sendTime(LocalDateTime.now())
            .build();

        messageRepository.save(message);
        return new MessageResponse(message);
    }

    private ChattingRoom createNewRoom(String title, User sender, User receiver) {
        ChattingRoom newRoom = ChattingRoom.builder().title(title).build();
        roomRepository.save(newRoom);

        participantRepository.save(ChattingParticipant.builder()
            .user(sender).chattingRoom(newRoom).unreadCount(0L).build());

        participantRepository.save(ChattingParticipant.builder()
            .user(receiver).chattingRoom(newRoom).unreadCount(1L).build());

        return newRoom;
    }
}
