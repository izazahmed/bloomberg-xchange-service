package com.slb.apigee.xchange.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody
    ExceptionResponse handleNotFoundException(final Exception exception,
                                              final HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse();
        error.setErrorCode(HttpStatus.NOT_FOUND.value());
        error.setErrorMessage(exception.getMessage());
        error.setRequestedURI(request.getRequestURI() + "?" + request.getQueryString());

        return error;
    }

    @ExceptionHandler(EmptyFilterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionResponse handleEmptyFilterException(final Exception exception,
                                                 final HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse();
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setErrorMessage(exception.getMessage());
        error.setRequestedURI(request.getRequestURI() + "?" + request.getQueryString());

        return error;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionResponse handleMissingRequiredParameter(final Exception exception,
                                                     final HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse();
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setErrorMessage(exception.getMessage());
        error.setRequestedURI(request.getRequestURI() + "?" + request.getQueryString());

        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionResponse handleConstraintViolationException(final Exception exception,
                                                         final HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse();
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setErrorMessage(exception.getMessage());
        error.setRequestedURI(request.getRequestURI() + "?" + request.getQueryString());

        return error;
    }
    
    @ExceptionHandler(InvalidDateDataException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionResponse handleInvalidDateDataException(final Exception exception,
                                                         final HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse();
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setErrorMessage(exception.getMessage());
        error.setRequestedURI(request.getRequestURI() + "?" + request.getQueryString());

        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    ExceptionResponse handleException(final Exception exception,
                                      final HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse();
        error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setErrorMessage(exception.getMessage());
        error.setRequestedURI(request.getRequestURI());

        return error;
    }
}
