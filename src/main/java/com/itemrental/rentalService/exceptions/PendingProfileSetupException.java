package com.itemrental.rentalService.exceptions;

public class PendingProfileSetupException extends RuntimeException{
    public PendingProfileSetupException(String message){
        super(message);
    }
}
