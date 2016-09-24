package com.mindorks.jpost.exceptions;

/**
 * Created by janisharali on 22/09/16.
 */
public class IllegalChannelStateException extends Exception{

    public IllegalChannelStateException(String message) {
        super(message);
    }

    public String toString() {
        return "IllegalChannelStateException[" + super.getMessage() + "]";
    }
}
