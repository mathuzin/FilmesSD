package com.example.filme.domain.pessoa.exceptions;

public class PessoaBadRequestException extends RuntimeException {
    public PessoaBadRequestException(String message) {
        super(message);
    }
}
