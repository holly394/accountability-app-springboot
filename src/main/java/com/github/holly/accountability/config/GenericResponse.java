package com.github.holly.accountability.config;

import org.springframework.http.HttpStatus;

public class GenericResponse {
    private String message;
    private HttpStatus error;

    public GenericResponse(String message) {
        super();
        this.message = message;
    }

    public GenericResponse(String message, HttpStatus error) {
        super();
        this.message = message;
        this.error = error;
    }

    public HttpStatus getError() {
        return this.error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setError(HttpStatus error) {
        this.error = error;
    }

}
