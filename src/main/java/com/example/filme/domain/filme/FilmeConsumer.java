package com.example.filme.domain.filme;

import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
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
public class FilmeConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final FilmeService filmeService;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public FilmeConsumer(SqsClient sqsClient, ObjectMapper objectMapper, FilmeService filmeService) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.filmeService = filmeService;
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

                    case "CADASTRAR" -> {
                        var dados = objectMapper.treeToValue(json.get("dados"), DadosCadastrarFilme.class);
                        filmeService.cadastrarFilme(dados);
                        System.out.println("Filme cadastrado via SQS: " + dados.nome());
                    }

                    case "ALTERAR" -> {
                        var dados = objectMapper.treeToValue(json.get("dados"), DadosAlterarFilme.class);
                        filmeService.alterarFilme(dados);
                        System.out.println("Filme alterado via SQS: " + dados.id());
                    }

                    case "ATUALIZAR_POPULARIDADE" -> {
                        Integer idFilme = json.get("idFilme").asInt();
                        filmeService.atualizarPopularidade(idFilme);
                        System.out.println("Popularidade atualizada via SQS: " + idFilme);
                    }

                    case "IMPORTAR_DA_API" -> {
                        Integer pagina = json.get("pagina").asInt();
                        filmeService.importarFilmesDaApi(pagina);
                        System.out.println("Importação da API via SQS — página: " + pagina);
                    }

                    default -> System.out.println("Ação inválida recebida: " + acao);
                }

                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(msg.receiptHandle())
                        .build());

            } catch (Exception e) {
                System.err.println("Erro ao processar mensagem SQS:");
                e.printStackTrace();
            }
        }
    }
}