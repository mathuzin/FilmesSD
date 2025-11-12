package com.example.filme.infra.exception;

import com.example.filme.domain.avaliacao.exceptions.AvaliacaoBadRequestException;
import com.example.filme.domain.avaliacao.exceptions.AvaliacaoNotFoundException;
import com.example.filme.domain.filme.exceptions.FilmeBadRequestException;
import com.example.filme.domain.filme.exceptions.FilmeNotFoundException;
import com.example.filme.domain.genero.exceptions.GeneroDuplicadoException;
import com.example.filme.domain.genero.exceptions.GeneroNotFoundException;
import com.example.filme.domain.pessoa.exceptions.PessoaBadRequestException;
import com.example.filme.domain.pessoa.exceptions.PessoaNotFoundException;
import com.example.filme.domain.usuario.exceptions.UsuarioBadRequestException;
import com.example.filme.domain.usuario.exceptions.UsuarioDuplicadoException;
import com.example.filme.domain.usuario.exceptions.UsuarioNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneroNotFoundException.class)
    public ResponseEntity<String> handleGeneroNotFound(GeneroNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(GeneroDuplicadoException.class)
    public ResponseEntity<String> handleGeneroDuplicado(GeneroDuplicadoException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(PessoaNotFoundException.class)
    public ResponseEntity<String> handlePessoaNotFound(PessoaNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(PessoaBadRequestException.class)
    public ResponseEntity<String> handlePessoaBadRequest(PessoaBadRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(FilmeNotFoundException.class)
    public ResponseEntity<String> handleFilmeNotFound(FilmeNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }


    @ExceptionHandler(FilmeBadRequestException.class)
    public ResponseEntity<String> handleFilmeBadRequest(FilmeBadRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<String> handleUsuarioNotFound(UsuarioNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(UsuarioBadRequestException.class)
    public ResponseEntity<String> handleUsuarioBadRequest(UsuarioBadRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(AvaliacaoNotFoundException.class)
    public ResponseEntity<String> handleAvaliacaoNotFound(AvaliacaoNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(AvaliacaoBadRequestException.class)
    public ResponseEntity<String> handleAvaliacaoBadRequest(AvaliacaoBadRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UsuarioDuplicadoException.class)
    public ResponseEntity<String> handleUsuarioDuplicado(UsuarioDuplicadoException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception e) {
        return ResponseEntity.status(500).body("Erro interno no servidor. Por favor, tente novamente mais tarde.");
    }

}
