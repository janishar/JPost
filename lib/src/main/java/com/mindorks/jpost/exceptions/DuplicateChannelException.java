package com.mindorks.jpost.exceptions;

/**
 * Created by janisharali on 22/09/16.
 */
public class DuplicateChannelException extends Exception{

    public DuplicateChannelException(String message) {
        super(message);
    }

    public String toString() {
        return "DuplicateChannelException[" + super.getMessage() + "]";
    }
}
