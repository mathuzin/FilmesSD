package com.example.filme.domain.genero.dtos;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastrarGenero(

        @NotBlank
        String nome
) {
}
