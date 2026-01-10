package com.itemrental.rentalService.domain.chat.repository;

import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

}
