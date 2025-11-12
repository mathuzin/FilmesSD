package com.example.filme.domain.pessoa.dtos;

import com.example.filme.domain.pessoa.Tipo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DadosCadastroPessoa(

        @NotBlank
        String nome,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,

        @Size(min = 3, max = 3)
        @Pattern(regexp = "^[A-Za-z]{3}$")
        String origem,

        @NotNull
        Tipo tipo
) {
}
