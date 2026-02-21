package com.itemrental.billioyo.domain.user.exception;

import com.itemrental.billioyo.global.error.exceptions.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String email) {
        super("사용자를 찾을 수 없습니다: " + email);
    }
}
