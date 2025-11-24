package com.example.filme.infra.aws;

import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
public class MensagemPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.app-topic-arn}")
    private String topicArn;

    public MensagemPublisher(SnsClient snsClient, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }

    public void enviar(AcaoMensagemDTO msg) {
        try {
            String json = objectMapper.writeValueAsString(msg);

            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(json)
                    .build());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao publicar SNS", e);
        }
    }
}
