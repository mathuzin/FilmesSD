package com.example.filme.domain.pessoa.dtos;

import com.example.filme.domain.pessoa.Tipo;
import jakarta.validation.constraints.NotNull;

public record DadosAlterarPessoa(

        @NotNull
        Integer id,
        String nome,
        Tipo tipo
) {
}
