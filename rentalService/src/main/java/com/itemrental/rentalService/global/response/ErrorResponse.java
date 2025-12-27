package com.itemrental.rentalService.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private final String status;
    private final String message;
}
