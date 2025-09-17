package com.itemrental.rentalService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApiResponse<T> {
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(String message, T data){
        return new ApiResponse<>(message, data);
    }

    public static ApiResponse<Void> success(String message){
        return new ApiResponse<>(message, null);
    }
}
