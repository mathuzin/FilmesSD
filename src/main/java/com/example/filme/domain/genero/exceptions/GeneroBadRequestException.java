package com.example.filme.domain.genero.exceptions;

public class GeneroBadRequestException extends RuntimeException {

    public GeneroBadRequestException(String message) {
        super(message);
    }

}