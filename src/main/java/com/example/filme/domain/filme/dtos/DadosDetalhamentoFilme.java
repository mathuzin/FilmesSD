package com.example.filme.domain.filme.dtos;

import com.example.filme.domain.filme.Filme;

public record DadosDetalhamentoFilme(Integer id, String nome, Float popularidade, String genero, String posterUrl,
                                     String descricao,
                                     Integer ano) {
    public DadosDetalhamentoFilme(Filme filme) {
        this(filme.getId(), filme.getNome(), filme.getPopularidade(), filme.getGenero().getNome(), filme.getPosterUrl(), filme.getDescricao(), filme.getAno());
    }
}