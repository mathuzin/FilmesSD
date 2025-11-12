package com.example.filme.domain.filme.exceptions;


public class FilmeNotFoundException extends RuntimeException {
    public FilmeNotFoundException(String message) {
        super(message);
    }
}
