package com.itemrental.billioyo.domain.chat.repository;

import com.itemrental.billioyo.domain.chat.entity.ChattingParticipant;
import com.itemrental.billioyo.domain.chat.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChattingParticipantRepository extends JpaRepository<ChattingParticipant, Long> {
    @Query("select p from ChattingParticipant p " +
            "join fetch p.chattingRoom r " +
            "join fetch p.user u " +
            "where u.email = :email")
    List<ChattingParticipant> findAllByUserEmail(@Param("email") String email);

    Optional<ChattingParticipant> findByChattingRoomIdAndUserEmail(Long roomId, String email);

    @Query("select p1.chattingRoom from ChattingParticipant p1 " +
            "join ChattingParticipant p2 on p1.chattingRoom = p2.chattingRoom " +
            "where p1.user.id = :user1Id and p2.user.id = :user2Id")
    Optional<ChattingRoom> findCommonRoom(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    List<ChattingParticipant> findAllByChattingRoomId(Long roomId);
}
