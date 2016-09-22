package com.mindorks.jpost.exceptions;

/**
 * Created by janisharali on 22/09/16.
 */
public class IllegalStateException extends Exception{

    public IllegalStateException(String message) {
        super(message);
    }

    public String toString() {
        return "IllegalStateException[" + super.getMessage() + "]";
    }
}
