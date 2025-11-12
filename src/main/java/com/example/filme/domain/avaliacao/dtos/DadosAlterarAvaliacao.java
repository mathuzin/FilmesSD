package com.example.filme.domain.avaliacao.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DadosAlterarAvaliacao(

        @NotNull
        Integer id,

        @DecimalMin(value = "0.0", inclusive = true, message = "A nota deve ser no mínimo 0")
        @DecimalMax(value = "5.0", inclusive = true, message = "A nota deve ser no máximo 5")
        Float nota,

        String ds_avaliacao
) {
}
