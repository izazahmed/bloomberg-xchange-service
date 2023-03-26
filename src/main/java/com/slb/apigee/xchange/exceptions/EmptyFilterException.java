package com.slb.apigee.xchange.exceptions;

public class EmptyFilterException extends Exception {

    public EmptyFilterException() {
        super();
    }

    public EmptyFilterException(String message) {
        super(message);
    }
}
