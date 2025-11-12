package com.example.filme.domain.filme.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastrarFilme(

        @NotBlank
        String nome,

        @NotNull
        Integer id_genero,

        String posterUrl,
        String descricao,

        @Min(value = 1888, message = "O ano deve ser maior ou igual a 1888")
        Integer ano

) {
}