package com.example.filme.domain.usuario.dtos;

import com.example.filme.domain.usuario.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastrarUsuario(

        @NotNull
        Integer pessoa_id,

        @NotBlank
        @Email
        String login,

        @NotBlank
        String senha,

        @NotNull
        Perfil perfil

) {
}
