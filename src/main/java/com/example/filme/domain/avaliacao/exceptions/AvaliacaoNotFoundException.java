package com.example.filme.domain.avaliacao.exceptions;

public class AvaliacaoNotFoundException extends RuntimeException {
    public AvaliacaoNotFoundException(String message) {
        super(message);
    }
}