package com.mindorks.jpost.exceptions;

/**
 * Created by janisharali on 22/09/16.
 */
public class NullObjectException extends Exception{

    public NullObjectException(String message) {
        super(message);
    }

    public String toString() {
        return "InvalidPropertyException[" + super.getMessage() + "]";
    }
}
