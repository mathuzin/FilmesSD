package com.example.filme.domain.filme_pessoa;

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

    @Value("${aws.sqs.filmePessoa-queue}")
    private String queueUrl;

    public FilmePessoaConsumer(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 2000) // a cada 2 segundos
    public void consumir() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build();

        var messages = sqsClient.receiveMessage(request).messages();

        for (Message msg : messages) {

            try {
                FilmePessoa FilmePessoa = objectMapper.readValue(msg.body(), FilmePessoa.class);
                System.out.println("FilmePessoa recebido: " + FilmePessoa.getPessoa());

                // PROCESSA O FilmePessoa (sua lógica)
                processarFilmePessoa(FilmePessoa);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // apaga da fila depois de processar
            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(msg.receiptHandle())
                    .build());
        }
    }

    private void processarFilmePessoa(FilmePessoa FilmePessoa) {
        // Sua regra de negócio aqui
    }
}
