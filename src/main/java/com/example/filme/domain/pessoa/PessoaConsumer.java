package com.example.filme.domain.pessoa;

import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
public class PessoaConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final PessoaService pessoaService;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public PessoaConsumer(SqsClient sqsClient, ObjectMapper objectMapper, PessoaService pessoaService) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.pessoaService = pessoaService;
    }

    @Scheduled(fixedDelay = 2000)
    public void consumir() {

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .visibilityTimeout(30)
                .build();

        var messages = sqsClient.receiveMessage(request).messages();

        for (Message msg : messages) {
            try {
                JsonNode json = objectMapper.readTree(msg.body());
                String acao = json.get("acao").asText();

                switch (acao) {

                    case "ADICIONAR" -> {
                        var dados = objectMapper.treeToValue(json.get("dados"), DadosCadastroPessoa.class);
                        pessoaService.cadastrarPessoa(dados);
                        System.out.println("Pessoa cadastrada via SQS: " + dados.nome());
                    }

                    case "EDITAR" -> {
                        var dados = objectMapper.treeToValue(json.get("dados"), DadosAlterarPessoa.class);
                        pessoaService.alterarPessoa(dados);
                        System.out.println("Pessoa alterada via SQS: " + dados.id());
                    }

                    default -> System.out.println("Ação inválida recebida: " + acao);
                }

                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(msg.receiptHandle())
                        .build());

            } catch (Exception e) {
                System.err.println("Erro ao processar mensagem de Pessoa:");
                e.printStackTrace();
            }
        }
    }
}