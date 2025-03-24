package com.tickefy.stadium_seats.exceptions;



public class ResourceNotFoundException  extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
