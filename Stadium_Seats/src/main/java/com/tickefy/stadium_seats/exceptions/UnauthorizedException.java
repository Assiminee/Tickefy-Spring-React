package com.tickefy.stadium_seats.exceptions;




public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
