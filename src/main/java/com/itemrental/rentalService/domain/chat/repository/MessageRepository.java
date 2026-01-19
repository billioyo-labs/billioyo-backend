package com.itemrental.rentalService.domain.chat.repository;

import com.itemrental.rentalService.domain.chat.dto.response.MessageResponse;
import com.itemrental.rentalService.domain.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m join fetch m.user where m.chattingRoom.id = :roomId order by m.sendTime desc")
    Slice<Message> findAllByChattingRoomIdOrderBySendTimeDesc(Long roomId, Pageable pageable);

    Optional<Message> findTopByChattingRoomIdOrderByIdDesc(Long roomId);
}
