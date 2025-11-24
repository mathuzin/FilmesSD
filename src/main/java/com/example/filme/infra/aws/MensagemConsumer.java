package com.example.filme.infra.aws;

import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public abstract class MensagemConsumer {

    protected final SqsClient sqsClient;
    protected final ObjectMapper objectMapper;

    @Value("${aws.sqs.queue-url}")
    protected String queueUrl;

    protected MensagemConsumer(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    protected void consumirMensagens() {

        var request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .visibilityTimeout(30)
                .waitTimeSeconds(10)
                .build();

        for (Message msg : sqsClient.receiveMessage(request).messages()) {
            try {
                var dto = objectMapper.readValue(msg.body(), AcaoMensagemDTO.class);
                processar(dto);

                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(msg.receiptHandle())
                        .build());

            } catch (Exception e) {
                System.err.println("Erro — será reentregue automaticamente");
            }
        }
    }

    protected abstract void processar(AcaoMensagemDTO dto);
}