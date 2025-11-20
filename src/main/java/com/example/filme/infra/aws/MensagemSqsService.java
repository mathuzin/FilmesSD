package com.example.filme.infra.aws;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MensagemSqsService {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public MensagemSqsService(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void enviarMensagem(Object mensagem) {
        sqsTemplate.send(queueUrl, mensagem);
    }
}