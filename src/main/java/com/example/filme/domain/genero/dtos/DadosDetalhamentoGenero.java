package com.example.filme.domain.genero.dtos;

import com.example.filme.domain.genero.Genero;

public record DadosDetalhamentoGenero(Integer id, String nome) {

    public DadosDetalhamentoGenero(Genero genero) {
        this(genero.getId(), genero.getNome());
    }
}
