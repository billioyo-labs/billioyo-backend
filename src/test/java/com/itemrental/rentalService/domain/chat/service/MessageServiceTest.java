package com.itemrental.rentalService.domain.chat.service;

import com.itemrental.rentalService.domain.chat.dto.request.ChatMessageRequest;
import com.itemrental.rentalService.domain.chat.dto.response.MessageResponse;
import com.itemrental.rentalService.domain.chat.entity.ChattingParticipant;
import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import com.itemrental.rentalService.domain.chat.entity.Message;
import com.itemrental.rentalService.domain.chat.repository.ChattingRoomRepository;
import com.itemrental.rentalService.domain.chat.repository.MessageRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.exception.UserNotFoundException;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock private UserRepository userRepository;
    @Mock private ChattingRoomRepository roomRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("성공: 메시지를 저장하고 모든 참여자에게 실시간 전송 및 알림을 보낸다")
    void saveMessage_Success() {
        // given
        String senderEmail = "sender@test.com";
        String receiverEmail = "receiver@test.com";

        User sender = User.builder().id(1L).email(senderEmail).nickName("발신자").build();
        User receiver = User.builder().id(2L).email(receiverEmail).nickName("수신자").build();

        ChattingRoom room = ChattingRoom.create("테스트방");
        room.addParticipant(sender);
        room.addParticipant(receiver);

        ChatMessageRequest request = ChatMessageRequest.builder()
                .roomId(1L)
                .content("안녕하세요")
                .build();

        Message message = Message.builder()
                .content(request.getContent())
                .chattingRoom(room)
                .user(sender)
                .build();

        ReflectionTestUtils.setField(message, "id", 100L);
        ReflectionTestUtils.setField(message, "sendTime", LocalDateTime.now());

        given(userRepository.findByEmail(senderEmail)).willReturn(Optional.of(sender));
        given(roomRepository.findByIdWithParticipants(1L)).willReturn(Optional.of(room));
        given(messageRepository.save(any(Message.class))).willReturn(message);

        // when
        MessageResponse response = messageService.saveMessage(request, senderEmail);

        // then
        verify(messageRepository, times(1)).save(any(Message.class));

        ChattingParticipant receiverParticipant = room.getParticipants().stream()
                .filter(p -> p.getUser().getEmail().equals(receiverEmail))
                .findFirst().get();
        assertEquals(1, receiverParticipant.getUnreadCount());

        verify(messagingTemplate, atLeastOnce())
                .convertAndSendToUser(eq(senderEmail), eq("/queue/messages"), any(MessageResponse.class));
        verify(messagingTemplate, atLeastOnce())
                .convertAndSendToUser(eq(receiverEmail), eq("/queue/messages"), any(MessageResponse.class));
        verify(messagingTemplate, atLeastOnce())
                .convertAndSendToUser(eq(receiverEmail), eq("/queue/notifications"), any(MessageResponse.class));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 유저가 메시지를 보내면 예외가 발생한다")
    void saveMessage_UserNotFound() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> messageService.saveMessage(ChatMessageRequest.builder().build(), "unknown@test.com"));
    }
}
