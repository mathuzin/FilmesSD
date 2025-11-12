package com.example.filme.domain.genero.dtos;

import jakarta.validation.constraints.NotNull;

public record DadosEditarGenero(

        @NotNull
        Integer id,
        String nome
) {
}
