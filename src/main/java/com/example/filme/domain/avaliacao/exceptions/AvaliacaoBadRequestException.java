package com.example.filme.domain.avaliacao.exceptions;

public class AvaliacaoBadRequestException extends RuntimeException {
    public AvaliacaoBadRequestException(String message) {
        super(message);
    }
}
