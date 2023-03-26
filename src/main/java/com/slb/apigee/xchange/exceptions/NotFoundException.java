package com.slb.apigee.xchange.exceptions;


public class NotFoundException extends Exception {

    private static final long serialVersionUID = 190624894762959585L;

    public NotFoundException() {
        super();
    }

    public NotFoundException(final String message) {
        super(message);
    }

}
