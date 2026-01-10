package com.itemrental.rentalService.domain.chat.repository;

import com.itemrental.rentalService.domain.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Slice<Message> findAllByChattingRoomIdOrderBySendTimeDesc(Long chattingRoomId, Pageable pageable);
}
