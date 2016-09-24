package com.mindorks.jpost.exceptions;

/**
 * Created by janisharali on 22/09/16.
 */
public class JPostNotRunningException extends Exception{

    public JPostNotRunningException(String message) {
        super(message);
    }

    public String toString() {
        return "JPostNotRunningException[" + super.getMessage() + "]";
    }
}
