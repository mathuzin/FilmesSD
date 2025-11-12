package com.example.filme.domain.pessoa.dtos;

import com.example.filme.domain.pessoa.Pessoa;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record DadosDetalhamentoPessoa(Integer id,
                                      String nome,
                                      @JsonFormat(pattern = "dd/MM/yyyy")
                                      LocalDate dt_nascimento,
                                      String origem,
                                      String tipo) {

    public DadosDetalhamentoPessoa(Pessoa pessoa) {
        this(pessoa.getId(), pessoa.getNome(), pessoa.getDataNascimento(), pessoa.getOrigem(), pessoa.getTipo().toString());
    }
}
