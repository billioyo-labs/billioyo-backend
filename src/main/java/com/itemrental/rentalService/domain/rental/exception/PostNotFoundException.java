package com.itemrental.rentalService.domain.rental.exception;

import com.itemrental.rentalService.global.error.exceptions.EntityNotFoundException;

public class PostNotFoundException extends EntityNotFoundException {
    public PostNotFoundException(Long postId) {
        super("게시글을 찾을 수 없습니다: " + postId);
    }
}
