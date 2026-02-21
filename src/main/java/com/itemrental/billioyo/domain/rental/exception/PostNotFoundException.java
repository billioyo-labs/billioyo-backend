package com.itemrental.billioyo.domain.rental.exception;

import com.itemrental.billioyo.global.error.exceptions.EntityNotFoundException;

public class PostNotFoundException extends EntityNotFoundException {
    public PostNotFoundException(Long postId) {
        super("게시글을 찾을 수 없습니다: " + postId);
    }
}
