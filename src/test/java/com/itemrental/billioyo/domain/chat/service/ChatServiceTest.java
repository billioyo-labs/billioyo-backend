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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock private ChattingRoomRepository roomRepository;
    @Mock private ChattingParticipantRepository participantRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private UserRepository userRepository;

    @Test
    @DisplayName("성공: 새로운 1:1 채팅방을 생성한다")
    void createRoom_Success() {
        // given
        String title = "테스트방";
        List<Long> userIds = List.of(1L, 2L);
        User user1 = User.builder().id(1L).email("user1@test.com").build();
        User user2 = User.builder().id(2L).email("user2@test.com").build();

        given(participantRepository.findCommonRoom(1L, 2L)).willReturn(Optional.empty());
        given(userRepository.findAllById(userIds)).willReturn(List.of(user1, user2));
        given(roomRepository.save(any(ChattingRoom.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Long roomId = chatService.createRoom(title, userIds);

        // then
        verify(roomRepository, times(1)).save(any(ChattingRoom.class));
        verify(participantRepository, times(1)).findCommonRoom(1L, 2L);
    }

    @Test
    @DisplayName("성공: 이미 존재하는 1:1 채팅방이면 기존 ID를 반환한다")
    void createRoom_Exist_ReturnExistingId() {
        // given
        List<Long> userIds = List.of(1L, 2L);
        ChattingRoom existingRoom = spy(ChattingRoom.create("기존방"));
        given(existingRoom.getId()).willReturn(100L);
        given(participantRepository.findCommonRoom(1L, 2L)).willReturn(Optional.of(existingRoom));

        // when
        Long roomId = chatService.createRoom("기존방", userIds);

        // then
        assertEquals(100L, roomId);
        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패: 참여 유저 중 일부가 존재하지 않으면 예외가 발생한다")
    void createRoom_Fail_UserNotFound() {
        // given
        List<Long> userIds = List.of(1L, 2L);
        given(userRepository.findAllById(userIds)).willReturn(List.of(User.builder().id(1L).build()));

        // when & then
        assertThrows(ParticipantNotFoundException.class, () -> chatService.createRoom("방", userIds));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 방의 이력을 조회하면 예외가 발생한다")
    void getMessageHistory_Fail_RoomNotFound() {
        // given
        given(roomRepository.existsById(1L)).willReturn(false);

        // when & then
        assertThrows(ChatRoomNotFoundException.class, () -> chatService.getMessageHistory(1L, Pageable.unpaged()));
    }

    @Test
    @DisplayName("성공: 3인 이상의 단체 채팅방은 중복 확인 없이 즉시 생성한다")
    void createRoom_Group_Success() {
        // given
        List<Long> userIds = List.of(1L, 2L, 3L); // 3명
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        User user3 = User.builder().id(3L).build();
        ChattingRoom room = ChattingRoom.create("단체방");
        ReflectionTestUtils.setField(room, "id", 999L);

        given(userRepository.findAllById(userIds)).willReturn(List.of(user1, user2, user3));
        given(roomRepository.save(any(ChattingRoom.class))).willReturn(room);

        // when
        Long roomId = chatService.createRoom("단체방", userIds);

        // then
        assertEquals(999L, roomId);
        verify(participantRepository, never()).findCommonRoom(anyLong(), anyLong());
    }

    @Test
    @DisplayName("성공: 메시지가 없는 방에서 읽음 처리 시 lastMessageId는 null이 된다")
    void markAsRead_NoMessage_Success() {
        // given
        String email = "test@test.com";
        Long roomId = 1L;
        ChattingParticipant participant = spy(ChattingParticipant.builder().build());

        given(participantRepository.findByChattingRoomIdAndUserEmail(roomId, email))
                .willReturn(Optional.of(participant));
        given(messageRepository.findTopByChattingRoomIdOrderByIdDesc(roomId))
                .willReturn(Optional.empty()); // 메시지 없음

        // when
        chatService.markAsRead(roomId, email);

        // then
        verify(participant).markAsRead(null);
        assertEquals(0L, participant.getUnreadCount());
    }

    @Test
    @DisplayName("실패: 존재하지 않는 채팅방의 이력을 조회하면 예외가 발생한다")
    void getMessageHistory_RoomNotFound() {
        // given
        Long roomId = 1L;
        given(roomRepository.existsById(roomId)).willReturn(false);

        // when & then
        assertThrows(ChatRoomNotFoundException.class,
                () -> chatService.getMessageHistory(roomId, Pageable.unpaged()));
    }

    @Test
    @DisplayName("성공: 내가 참여 중인 모든 채팅방 목록을 조회한다")
    void getMyRooms_Success() {
        // given
        String email = "test@test.com";
        User me = User.builder().email(email).build();
        User opponent = User.builder().email("opponent@test.com").nickName("상대방").build();

        ChattingRoom room = ChattingRoom.create("채팅방");
        room.addParticipant(me);
        room.addParticipant(opponent);

        ChattingParticipant participant = ChattingParticipant.builder()
                .user(me)
                .chattingRoom(room)
                .build();
        given(participantRepository.findAllByUserEmail(email)).willReturn(List.of(participant));

        // when
        List<ChatRoomResponse> result = chatService.getMyRooms(email);

        // then
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("채팅방", result.getFirst().getTitle()),
                () -> assertEquals("상대방", result.getFirst().getOpponentNickname()),
                () -> assertEquals("opponent@test.com", result.getFirst().getOpponentEmail())
        );
        verify(participantRepository, times(1)).findAllByUserEmail(email);
    }

    @Test
    @DisplayName("성공: 특정 채팅방의 메시지 내역을 페이징(Slice)으로 조회한다")
    void getMessageHistory_Success() {
        // given
        Long roomId = 1L;
        Pageable pageable = PageRequest.of(0, 30);

        User sender = User.builder().email("sender@test.com").nickName("작성자").build();
        ChattingRoom room = ChattingRoom.create("방");

        Message message = Message.builder()
                .content("테스트 메시지")
                .user(sender)
                .chattingRoom(room)
                .build();

        ReflectionTestUtils.setField(message, "id", 100L);
        ReflectionTestUtils.setField(message, "sendTime", LocalDateTime.now());

        Slice<Message> messageSlice = new SliceImpl<>(List.of(message), pageable, false);

        given(roomRepository.existsById(roomId)).willReturn(true);
        given(messageRepository.findAllByChattingRoomIdOrderBySendTimeDesc(roomId, pageable))
                .willReturn(messageSlice);

        // when
        Slice<MessageResponse> result = chatService.getMessageHistory(roomId, pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("테스트 메시지", result.getContent().get(0).getContent()),
                () -> assertEquals("작성자", result.getContent().get(0).getSenderNickname()),
                () -> assertFalse(result.hasNext())
        );
        verify(messageRepository).findAllByChattingRoomIdOrderBySendTimeDesc(roomId, pageable);
    }
}
