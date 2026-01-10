package com.itemrental.rentalService.domain.chat.repository;

import com.itemrental.rentalService.domain.chat.entity.ChattingParticipant;
import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChattingParticipantRepository extends JpaRepository<ChattingParticipant, Long> {
    List<ChattingParticipant> findAllByUserId(Long userId);
    List<ChattingParticipant> findAllByUserEmail(String email);

    @Query("SELECT p1.chattingRoom FROM ChattingParticipant p1 " +
            "JOIN ChattingParticipant p2 ON p1.chattingRoom = p2.chattingRoom " +
            "WHERE p1.user.id = :user1Id AND p2.user.id = :user2Id")
    Optional<ChattingRoom> findCommonRoom(Long user1Id, Long user2Id);
}
