package com.mindorks.jpost.exceptions;

/**
 * Created by janisharali on 22/09/16.
 */
public class InvalidPropertyException extends Exception{

    public InvalidPropertyException(String message) {
        super(message);
    }

    public String toString() {
        return "InvalidPropertyException[" + super.getMessage() + "]";
    }
}
