package com.example.filme.domain.avaliacao;

import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
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
public class AvaliacaoConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final AvaliacaoService avaliacaoService;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public AvaliacaoConsumer(SqsClient sqsClient, ObjectMapper objectMapper, AvaliacaoService avaliacaoService) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.avaliacaoService = avaliacaoService;
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
                        var dados = objectMapper.treeToValue(json.get("dados"), DadosCadastroAvaliacao.class);
                        var avaliacao = avaliacaoService.avaliar(dados);
                        System.out.println("Avaliação cadastrada via SQS: " + avaliacao.nm_usuario());
                    }

                    case "ALTERAR" -> {
                        var dados = objectMapper.treeToValue(json.get("dados"), DadosAlterarAvaliacao.class);
                        var avaliacao = avaliacaoService.atualizarAvaliacao(dados);
                        System.out.println("Avaliação alterada via SQS: " + avaliacao.nm_usuario());
                    }

                    case "DELETAR" -> {
                        Integer idAvaliacao = json.get("idAvaliacao").asInt();
                        avaliacaoService.deletarAvaliacao(idAvaliacao);
                        System.out.println("Avaliação deletada via SQS: " + idAvaliacao);
                    }

                    case "LISTAR_USUARIO" -> {
                        Integer idUsuario = json.get("idUsuario").asInt();
                        var lista = avaliacaoService.listarAvaliacoesDeUmUsuario(idUsuario);
                        System.out.println("Listadas " + lista.size() + " avaliações do usuário: " + idUsuario);
                    }

                    case "LISTAR_FILME" -> {
                        Integer idFilme = json.get("idFilme").asInt();
                        var lista = avaliacaoService.listarAvaliacoesFilme(idFilme);
                        System.out.println("Listadas " + lista.size() + " avaliações do filme: " + idFilme);
                    }

                    case "LISTAR_UNICA" -> {
                        Integer idFilme = json.get("idFilme").asInt();
                        Integer idUsuario = json.get("idUsuario").asInt();
                        var avaliacao = avaliacaoService.listarAvaliacaoDeUmFilme(idFilme, idUsuario);
                        System.out.println("Avaliação localizada via SQS — filme: " + idFilme + ", usuário: " + idUsuario);
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
