package com.example.filme.domain.genero;

import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.infra.aws.MensagemConsumer;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
public class GeneroConsumer extends MensagemConsumer {

    private final GeneroService service;

    public GeneroConsumer(SqsClient sqsClient,
                          ObjectMapper objectMapper,
                          GeneroService service) {
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
                var dados = objectMapper.convertValue(msg.dados(), DadosCadastrarGenero.class);
                service.adicionarGenero(dados);
            }

            default -> System.out.println("Ação ignorada para GENERO: " + msg.acao());
        }
    }
}