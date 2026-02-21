package com.itemrental.billioyo.domain.chat.exception;

import com.itemrental.billioyo.global.error.exceptions.EntityNotFoundException;

public class ChatRoomNotFoundException extends EntityNotFoundException {
    public ChatRoomNotFoundException() {
        super("채팅방을 찾을 수 없습니다.");
    }
}
