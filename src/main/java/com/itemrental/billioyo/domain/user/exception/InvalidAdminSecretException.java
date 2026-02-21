package com.itemrental.billioyo.domain.user.exception;

import com.itemrental.billioyo.global.error.exceptions.InvalidValueException;

public class InvalidAdminSecretException extends InvalidValueException {
    public InvalidAdminSecretException() {
        super("관리자 가입 코드가 올바르지 않습니다.");
    }
}
