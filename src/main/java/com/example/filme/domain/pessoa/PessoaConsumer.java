package com.example.filme.domain.pessoa;

import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.example.filme.infra.aws.MensagemConsumer;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
public class PessoaConsumer extends MensagemConsumer {

    private final PessoaService service;

    public PessoaConsumer(SqsClient sqsClient,
                          ObjectMapper objectMapper,
                          PessoaService service) {
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
                var dados = objectMapper.convertValue(msg.dados(), DadosCadastroPessoa.class);
                service.cadastrarPessoa(dados);
            }

            case ALTERAR -> {
                var dados = objectMapper.convertValue(msg.dados(), DadosAlterarPessoa.class);
                service.alterarPessoa(dados);
            }

            default -> System.out.println("Ação ignorada para PESSOA: " + msg.acao());
        }
    }
}