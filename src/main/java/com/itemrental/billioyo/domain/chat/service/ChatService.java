package com.itemrental.billioyo.domain.chat.service;

import com.itemrental.billioyo.domain.chat.dto.response.ChatRoomResponse;
import com.itemrental.billioyo.domain.chat.dto.response.MessageResponse;
import com.itemrental.billioyo.domain.chat.entity.ChattingParticipant;
import com.itemrental.billioyo.domain.chat.entity.ChattingRoom;
import com.itemrental.billioyo.domain.chat.entity.Message;
import com.itemrental.billioyo.domain.chat.exception.ChatRoomNotFoundException;
import com.itemrental.billioyo.domain.chat.exception.ParticipantNotFoundException;
import com.itemrental.billioyo.domain.chat.repository.ChattingParticipantRepository;
import com.itemrental.billioyo.domain.chat.repository.ChattingRoomRepository;
import com.itemrental.billioyo.domain.chat.repository.MessageRepository;
import com.itemrental.billioyo.domain.user.entity.User;
import com.itemrental.billioyo.domain.user.repository.UserRepository;
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
            return findExistingDirectChatRoom(userIds.get(0), userIds.get(1))
                    .orElseGet(() -> createNewChatRoom(title, userIds))
                    .getId();
        }

        return createNewChatRoom(title, userIds).getId();
    }

    private Optional<ChattingRoom> findExistingDirectChatRoom(Long user1Id, Long user2Id) {
        return participantRepository.findCommonRoom(user1Id, user2Id);
    }

    private ChattingRoom createNewChatRoom(String title, List<Long> userIds) {
        ChattingRoom room = ChattingRoom.create(title);

        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new ParticipantNotFoundException();
        }

        users.forEach(room::addParticipant);

        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getMyRooms(String userEmail) {
        return participantRepository.findAllByUserEmail(userEmail).stream()
                .map(participant -> ChatRoomResponse.from(participant.getChattingRoom(), userEmail))
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<MessageResponse> getMessageHistory(Long roomId, Pageable pageable) {
        if (!roomRepository.existsById(roomId)) {
            throw new ChatRoomNotFoundException();
        }

        Slice<Message> messages = messageRepository.findAllByChattingRoomIdOrderBySendTimeDesc(roomId, pageable);

        return messages.map(MessageResponse::from);
    }

    @Transactional
    public void markAsRead(Long roomId, String userEmail) {
        ChattingParticipant participant = participantRepository
                .findByChattingRoomIdAndUserEmail(roomId, userEmail)
                .orElseThrow(ParticipantNotFoundException::new);

        Long lastMessageId = messageRepository.findTopByChattingRoomIdOrderByIdDesc(roomId)
                .map(Message::getId)
                .orElse(null);

        participant.markAsRead(lastMessageId);
    }
}
