package com.example.filme.domain.filme.exceptions;

public class FilmeBadRequestException extends RuntimeException {
    public FilmeBadRequestException(String message) {
        super(message);
    }
}
