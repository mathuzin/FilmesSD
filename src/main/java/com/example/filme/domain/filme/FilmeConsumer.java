package com.example.filme.domain.filme;

import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
import com.example.filme.infra.aws.MensagemConsumer;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
public class FilmeConsumer extends MensagemConsumer {

    private final FilmeService service;

    public FilmeConsumer(SqsClient sqsClient,
                         ObjectMapper objectMapper,
                         FilmeService service) {
        super(sqsClient, objectMapper);
        this.service = service;
    }

    @Scheduled(fixedDelay = 2000)
    public void consumir() {
        consumirMensagens();
    }

    @Override
    protected void processar(AcaoMensagemDTO msg) {
        switch (msg.acao()) {

            case CADASTRAR -> {
                var dados = objectMapper.convertValue(msg.dados(), DadosCadastrarFilme.class);
                service.cadastrarFilme(dados);
            }

            case ALTERAR -> {
                var dados = objectMapper.convertValue(msg.dados(), DadosAlterarFilme.class);
                service.alterarFilme(dados);
            }

            case ATUALIZAR_POPULARIDADE -> service.atualizarPopularidade(msg.idFilme());

            default -> System.out.println("Ação ignorada para FILME: " + msg.acao());
        }
    }
}