package com.mindorks.jpost.exceptions;

/**
 * Created by janisharali on 22/09/16.
 */
public class AlreadyExistsException extends Exception{

    public AlreadyExistsException(String message) {
        super(message);
    }

    public String toString() {
        return "AlreadyExistsException[" + super.getMessage() + "]";
    }
}
