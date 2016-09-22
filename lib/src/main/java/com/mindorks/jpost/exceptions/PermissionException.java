package com.mindorks.jpost.exceptions;

/**
 * Created by janisharali on 22/09/16.
 */
public class PermissionException extends Exception{

    public PermissionException(String message) {
        super(message);
    }

    public String toString() {
        return "PermissionException[" + super.getMessage() + "]";
    }
}
