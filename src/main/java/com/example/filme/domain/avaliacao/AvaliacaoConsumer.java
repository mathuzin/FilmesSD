package com.example.filme.domain.avaliacao;

import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.infra.aws.MensagemConsumer;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
public class AvaliacaoConsumer extends MensagemConsumer {

    private final AvaliacaoService service;

    public AvaliacaoConsumer(SqsClient sqsClient,
                             ObjectMapper objectMapper,
                             AvaliacaoService service) {
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
                var dados = objectMapper.convertValue(msg.dados(), DadosCadastroAvaliacao.class);
                service.avaliar(dados);
            }

            case ALTERAR -> {
                var dados = objectMapper.convertValue(msg.dados(), DadosAlterarAvaliacao.class);
                service.atualizarAvaliacao(dados);
            }

            case DELETAR -> service.deletarAvaliacao(msg.idFilme());

            default -> System.out.println("Ação ignorada: " + msg.acao());
        }
    }
}
