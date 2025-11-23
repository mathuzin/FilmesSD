package com.example.filme.domain.filme_pessoa;

import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosAlterarFilmePessoa;
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
public class FilmePessoaConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final FilmePessoaService filmePessoaService;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public FilmePessoaConsumer(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            FilmePessoaService filmePessoaService
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.filmePessoaService = filmePessoaService;
    }

    @Scheduled(fixedDelay = 2000)
    public void consumir() {

        var request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build();

        var messages = sqsClient.receiveMessage(request).messages();

        for (Message msg : messages) {
            try {
                JsonNode json = objectMapper.readTree(msg.body());

                String acao = json.get("acao").asText();
                JsonNode dadosNode = json.get("dados");

                switch (acao) {

                    case "CADASTRAR" -> {
                        var dados = objectMapper.treeToValue(dadosNode, DadosCadastroFilmePessoa.class);
                        System.out.println("FilmePessoa recebido — CADASTRAR: " + dados);
                        filmePessoaService.adicionarPessoaAoFilme(dados);
                    }

                    case "ALTERAR" -> {
                        var dados = objectMapper.treeToValue(dadosNode, DadosAlterarFilmePessoa.class);
                        System.out.println("FilmePessoa recebido — ALTERAR PAPEL: " + dados);
                        filmePessoaService.alterarPapelEmFilme(dados);
                    }

                    case "DELETAR" -> {
                        Integer idPessoa = json.get("idPessoa").asInt();
                        Integer idFilme = json.get("idFilme").asInt();
                        System.out.println("FilmePessoa recebido — DELETAR: pessoa=" + idPessoa + ", filme=" + idFilme);
                        filmePessoaService.deletarPessoaDeFilme(idPessoa, idFilme);
                    }

                    default -> System.err.println("Ação desconhecida no FilmePessoaConsumer: " + acao);
                }

            } catch (Exception e) {
                System.err.println("Erro ao processar mensagem de FilmePessoa");
                e.printStackTrace();
            }

            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(msg.receiptHandle())
                    .build());
        }
    }
}