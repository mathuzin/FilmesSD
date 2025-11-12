package com.example.filme.domain.filme_pessoa.dtos;

import com.example.filme.domain.filme_pessoa.Papel;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroFilmePessoa(

        @NotNull
        Integer id_pessoa,

        @NotNull
        Integer id_filme,

        @NotNull
        Papel papel
) {
}
