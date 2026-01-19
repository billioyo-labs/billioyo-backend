package com.itemrental.rentalService.domain.user.exception;

import com.itemrental.rentalService.global.error.exceptions.InvalidValueException;

public class DuplicateUsernameException extends InvalidValueException {
    public DuplicateUsernameException(String nickname) {
        super("이미 사용 중인 닉네임입니다: " + nickname);
    }
}
