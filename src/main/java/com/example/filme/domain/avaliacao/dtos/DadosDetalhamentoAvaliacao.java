package com.example.filme.domain.avaliacao.dtos;

import com.example.filme.domain.avaliacao.Avaliacao;

public record DadosDetalhamentoAvaliacao(Integer idFIlme, String nm_filme, Integer idUsuario, String nm_usuario,
                                         Float nota, String ds_avaliacao) {

    public DadosDetalhamentoAvaliacao(Avaliacao avaliacao) {
        this(
                avaliacao.getFilme().getId(),
                avaliacao.getFilme().getNome(),
                avaliacao.getUsuario().getId(),
                avaliacao.getUsuario().getLogin(),
                avaliacao.getNota(),
                avaliacao.getDs_avaliacao()
        );
    }
}
