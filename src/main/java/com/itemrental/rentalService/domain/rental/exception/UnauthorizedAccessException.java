package com.itemrental.rentalService.domain.rental.exception;

import com.itemrental.rentalService.global.error.exceptions.ForbiddenException;

public class UnauthorizedAccessException extends ForbiddenException {
    public UnauthorizedAccessException() {
        super("해당 게시글에 대한 접근 권한이 없습니다.");
    }
}
