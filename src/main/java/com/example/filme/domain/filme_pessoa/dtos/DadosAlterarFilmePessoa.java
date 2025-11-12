package com.example.filme.domain.filme_pessoa.dtos;

import com.example.filme.domain.filme_pessoa.FilmePessoaId;
import com.example.filme.domain.filme_pessoa.Papel;
import jakarta.validation.constraints.NotNull;

public record DadosAlterarFilmePessoa(

        @NotNull
        FilmePessoaId id,
        Papel papel
) {
}
