package com.itemrental.billioyo.domain.chat.entity;

import com.itemrental.billioyo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChattingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chattingRoomId", nullable = false, updatable = false, unique = true)
    private Long id;
    private String title;
    @CreatedDate
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChattingParticipant> participants;

    @OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    public static ChattingRoom create(String title) {
        ChattingRoom room = new ChattingRoom();
        room.title = title;
        room.created_at = LocalDateTime.now();
        room.participants = new ArrayList<>();
        room.messages = new ArrayList<>();
        return room;
    }

    public void addParticipant(User user) {
        ChattingParticipant participant = ChattingParticipant.builder()
                .user(user)
                .chattingRoom(this)
                .build();
        this.participants.add(participant);
    }
}
