package com.itemrental.rentalService.domain.chat.entity;

import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChattingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chattingParticipantId", updatable = false, nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private Long unreadCount = 0L;

    private Long lastReadMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_id", nullable = false)
    private ChattingRoom chattingRoom;

    @Builder
    private ChattingParticipant(User user, ChattingRoom chattingRoom) {
        this.user = user;
        this.chattingRoom = chattingRoom;
        this.unreadCount = 0L;
    }

    public void incrementUnreadCount() {
        this.unreadCount++;
    }

    public void markAsRead(Long messageId) {
        this.lastReadMessageId = messageId;
        this.unreadCount = 0L;
    }
}
