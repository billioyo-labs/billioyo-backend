package com.itemrental.rentalService.global.exceptions;

public class PendingProfileSetupException extends RuntimeException{
    public PendingProfileSetupException(String message){
        super(message);
    }
}
