package com.itemrental.rentalService.domain.chat.service;

import com.itemrental.rentalService.domain.chat.dto.request.ChatMessageRequest;
import com.itemrental.rentalService.domain.chat.dto.response.MessageResponse;
import com.itemrental.rentalService.domain.chat.entity.ChattingParticipant;
import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import com.itemrental.rentalService.domain.chat.entity.Message;
import com.itemrental.rentalService.domain.chat.exception.ChatRoomNotFoundException;
import com.itemrental.rentalService.domain.chat.repository.ChattingParticipantRepository;
import com.itemrental.rentalService.domain.chat.repository.ChattingRoomRepository;
import com.itemrental.rentalService.domain.chat.repository.MessageRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.exception.UserNotFoundException;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageService {
    private final UserRepository userRepository;
    private final ChattingRoomRepository roomRepository;
    private final ChattingParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageResponse saveMessage(ChatMessageRequest request, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException(senderEmail));

        ChattingRoom room = roomRepository.findByIdWithParticipants(request.getRoomId())
                .orElseThrow(ChatRoomNotFoundException::new);

        Message message = messageRepository.save(request.toEntity(room, sender));

        List<ChattingParticipant> participants = room.getParticipants();
        participants.stream()
                .filter(p -> !p.getUser().getEmail().equals(senderEmail))
                .forEach(ChattingParticipant::incrementUnreadCount);

        MessageResponse response = MessageResponse.from(message);
        broadcastMessage(response, participants);

        return response;
    }

    private void broadcastMessage(MessageResponse response, List<ChattingParticipant> participants) {
        String roomTopic = "/topic/chat/" + response.getRoomId();
        messagingTemplate.convertAndSend(roomTopic, response);

        for (ChattingParticipant p : participants) {
            messagingTemplate.convertAndSendToUser(p.getUser().getEmail(), "/queue/notifications", response);
        }
    }
}
