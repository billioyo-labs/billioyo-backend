package com.itemrental.rentalService.domain.chat.entity;

import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messageId", updatable = false, nullable = false, unique = true)
    private Long id;
    @Column(columnDefinition = "TEXT", nullable = false) // 긴 메시지 대응 및 null 방지
    private String content;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime sendTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_id", nullable = false)
    private ChattingRoom chattingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    private Message(String content, ChattingRoom chattingRoom, User user) {
        this.content = content;
        this.chattingRoom = chattingRoom;
        this.user = user;
        this.sendTime = LocalDateTime.now();
    }

    public static Message createMessage(String content, ChattingRoom room, User user) {
        return Message.builder()
                .content(content)
                .chattingRoom(room)
                .user(user)
                .build();
    }
}
