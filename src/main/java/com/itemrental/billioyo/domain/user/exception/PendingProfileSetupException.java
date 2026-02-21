package com.itemrental.billioyo.domain.user.exception;

import com.itemrental.billioyo.global.error.exceptions.InvalidValueException;

public class PendingProfileSetupException extends InvalidValueException {
    public PendingProfileSetupException(String email) {
        super("이미 초기 가입 절차가 완료된 계정입니다: " + email);
    }
}
