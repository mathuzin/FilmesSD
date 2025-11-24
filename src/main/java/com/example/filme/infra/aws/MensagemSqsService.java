package com.example.filme.infra.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MensagemSqsService {

    @Autowired
    private ObjectMapper objectMapper;

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public MensagemSqsService(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void enviarMensagem(Object dto) {
        try {
            var json = objectMapper.writeValueAsString(dto);
            sqsTemplate.send(queueUrl, json);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar mensagem para JSON", e);
        }
    }
}