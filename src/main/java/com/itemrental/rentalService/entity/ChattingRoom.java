package com.itemrental.rentalService.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChattingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chattingRoomId", nullable = false, updatable = false, unique = true)
    private Long id;
    private String title;
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "chattingRoom")
    private List<ChattingParticipant> participants;

    @OneToMany(mappedBy = "chattingRoom")
    private List<Message> messages;
}
