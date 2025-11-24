package com.example.filme.domain.filme;

import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
import com.example.filme.infra.aws.MensagemPublisher;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.example.filme.infra.aws.enums.AcaoMensagem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class FilmePublisher {

    private final MensagemPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public FilmePublisher(MensagemPublisher publisher) {
        this.publisher = publisher;
    }

    public void publicarCadastrar(DadosCadastrarFilme dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "FILME",
                AcaoMensagem.CADASTRAR,
                mapper.valueToTree(dados),
                null,
                null,
                null
        ));
    }

    public void publicarAlterar(DadosAlterarFilme dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "FILME",
                AcaoMensagem.ALTERAR,
                mapper.valueToTree(dados),
                dados.id(),
                null,
                null
        ));
    }

    public void publicarAtualizarPopularidade(Integer idFilme) {
        publisher.enviar(new AcaoMensagemDTO(
                "FILME",
                AcaoMensagem.ATUALIZAR_POPULARIDADE,
                null,
                idFilme,
                null,
                null
        ));
    }

    public void publicarImportarDaApi(Integer pagina) {
        publisher.enviar(new AcaoMensagemDTO(
                "FILME",
                AcaoMensagem.IMPORTAR_DA_API,
                mapper.valueToTree(pagina),
                null,
                null,
                null
        ));
    }
}