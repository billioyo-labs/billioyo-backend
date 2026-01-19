package com.itemrental.rentalService.domain.chat.exception;

import com.itemrental.rentalService.global.error.exceptions.EntityNotFoundException;

public class ChatRoomNotFoundException extends EntityNotFoundException {
    public ChatRoomNotFoundException() {
        super("채팅방을 찾을 수 없습니다.");
    }
}
