package com.example.filme.domain.usuario.dtos;

import jakarta.validation.constraints.NotNull;

public record DadosEditarUsuario(

        @NotNull
        Integer id,
        String login,
        String senha

) {
}
