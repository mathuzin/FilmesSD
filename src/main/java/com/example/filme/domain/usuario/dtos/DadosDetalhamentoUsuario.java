package com.example.filme.domain.usuario.dtos;

import com.example.filme.domain.usuario.Usuario;

public record DadosDetalhamentoUsuario(String login, String nome) {

    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(usuario.getLogin(), usuario.getPessoa().getNome());
    }
}
