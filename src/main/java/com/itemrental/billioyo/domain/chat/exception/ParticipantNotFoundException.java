package com.itemrental.billioyo.domain.chat.exception;

import com.itemrental.billioyo.global.error.exceptions.EntityNotFoundException;

public class ParticipantNotFoundException extends EntityNotFoundException {
    public ParticipantNotFoundException() {
        super("채팅 참여 정보를 찾을 수 없습니다.");
    }
}
