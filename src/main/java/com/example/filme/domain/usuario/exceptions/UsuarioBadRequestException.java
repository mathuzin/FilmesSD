package com.example.filme.domain.usuario.exceptions;

public class UsuarioBadRequestException extends RuntimeException {
    public UsuarioBadRequestException(String message) {
        super(message);
    }
}
