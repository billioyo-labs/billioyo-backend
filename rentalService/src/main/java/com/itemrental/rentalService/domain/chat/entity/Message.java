package com.itemrental.rentalService.domain.chat.entity;

import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messageId", updatable = false, nullable = false, unique = true)
    private Long id;
    private String content;
    @CreatedDate
    private LocalDateTime sendTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chattingRoomId")
    private ChattingRoom chattingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;
}
