package com.example.filme.domain.filme_pessoa;

import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.infra.aws.MensagemConsumer;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
public class FilmePessoaConsumer extends MensagemConsumer {

    private final FilmePessoaService service;

    public FilmePessoaConsumer(SqsClient sqsClient,
                               ObjectMapper objectMapper,
                               FilmePessoaService service) {
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

            case ADICIONAR -> {
                var dados = objectMapper.convertValue(msg.dados(), DadosCadastroFilmePessoa.class);
                service.adicionarPessoaAoFilme(dados);
            }

            default -> System.out.println("Ação ignorada para FILME_PESSOA: " + msg.acao());
        }
    }
}