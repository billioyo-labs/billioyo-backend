package com.itemrental.rentalService.global.utils;

import com.itemrental.rentalService.global.exceptions.DuplicateUsernameException;
import com.itemrental.rentalService.global.exceptions.PasswordMismatchException;
import com.itemrental.rentalService.global.exceptions.PendingProfileSetupException;
import com.itemrental.rentalService.global.response.ErrorResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        ErrorResponse errorResponse = new ErrorResponse("409", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse("400", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PendingProfileSetupException.class)
    public ResponseEntity<ErrorResponse> handlAlreadyRegisteredEmailException(PendingProfileSetupException ex) {
        ErrorResponse errorResponse = new ErrorResponse("409", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse("400", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorResponse> handleMessagingException(MessagingException ex) {
        ErrorResponse errorResponse = new ErrorResponse("400", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex){
//        ErrorResponse errorResponse = new ErrorResponse("500", "처리 중 문제 발생");
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
