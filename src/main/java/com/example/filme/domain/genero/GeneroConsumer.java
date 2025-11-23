package com.example.filme.domain.genero;

import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
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
public class GeneroConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final GeneroService generoService;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public GeneroConsumer(SqsClient sqsClient, ObjectMapper objectMapper, GeneroService generoService) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.generoService = generoService;
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
                        var dados = objectMapper.treeToValue(json.get("dados"), DadosCadastrarGenero.class);
                        generoService.adicionarGenero(dados);
                        System.out.println("Gênero cadastrado via SQS: " + dados.nome());
                    }

                    case "EDITAR" -> {
                        var dados = objectMapper.treeToValue(json.get("dados"), DadosEditarGenero.class);
                        generoService.editarGenero(dados);
                        System.out.println("Gênero editado via SQS: " + dados.id());
                    }

                    case "IMPORTAR_DA_API" -> {
                        generoService.importarGenerosDaApi();
                        System.out.println("Gêneros importados da API via SQS");
                    }

                    default -> System.out.println("Ação inválida recebida: " + acao);
                }

                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(msg.receiptHandle())
                        .build());

            } catch (Exception e) {
                System.err.println("Erro ao processar mensagem de gênero:");
                e.printStackTrace();
            }
        }
    }
}