package com.itemrental.billioyo.domain.user.exception;

import com.itemrental.billioyo.global.error.exceptions.InvalidValueException;

public class UserInformationMismatchException extends InvalidValueException {
    public UserInformationMismatchException() {
        super("사용자 정보가 일치하지 않습니다.");
    }
}
