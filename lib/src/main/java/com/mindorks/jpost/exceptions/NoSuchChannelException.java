package com.mindorks.jpost.exceptions;

/**
 * Created by janisharali on 22/09/16.
 */
public class NoSuchChannelException extends Exception{

    public NoSuchChannelException(String message) {
        super(message);
    }

    public String toString() {
        return "NoSuchChannelException[" + super.getMessage() + "]";
    }
}
