package com.example.filme.domain.filme_pessoa.dtos;

import com.example.filme.domain.filme_pessoa.FilmePessoa;

public record DadosDetalhamentoFilmePessoa(Integer idPessoa, String nomePessoa, Integer idFilme, String nomeFilme,
                                           String papel) {

    public DadosDetalhamentoFilmePessoa(FilmePessoa dados) {
        this(dados.getPessoa().getId(), dados.getPessoa().getNome(), dados.getFilme().getId(), dados.getFilme().getNome(), dados.getPapel().toString());
    }
}
