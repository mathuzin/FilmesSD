package com.example.filme.infra.aws.dtos;

import com.example.filme.infra.aws.enums.AcaoMensagem;
import com.fasterxml.jackson.databind.JsonNode;

public record AcaoMensagemDTO(
        String entidade,
        AcaoMensagem acao,
        JsonNode dados,
        Integer idFilme,
        Integer idUsuario,
        Integer idPessoa
) {
}