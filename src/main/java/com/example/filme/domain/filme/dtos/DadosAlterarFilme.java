package com.example.filme.domain.filme.dtos;

import jakarta.validation.constraints.NotNull;

public record DadosAlterarFilme(

        @NotNull
        Integer id,
        String nome,
        Integer id_genero,
        String posterUrl,
        String descricao,
        Integer ano
) {
}