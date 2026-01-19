package com.itemrental.rentalService.domain.chat.repository;

import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {
    @Query("select r from ChattingRoom r join fetch r.participants p join fetch p.user where r.id = :roomId")
    Optional<ChattingRoom> findByIdWithParticipants(@Param("roomId") Long roomId);
}
