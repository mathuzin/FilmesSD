package com.example.filme.domain.genero;

import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Service
public class GeneroPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.app-topic-arn}")
    private String topicArn;

    public GeneroPublisher(SnsClient snsClient, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }

    private void enviar(Object payload) {
        try {
            String message = objectMapper.writeValueAsString(payload);

            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar mensagem para SNS", e);
        }
    }

    public void publicarAdicionar(DadosCadastrarGenero dados) {
        enviar(Map.of(
                "acao", "ADICIONAR",
                "dados", dados
        ));
    }

    public void publicarEditar(DadosEditarGenero dados) {
        enviar(Map.of(
                "acao", "EDITAR",
                "dados", dados
        ));
    }

    public void publicarImportarDaApi() {
        enviar(Map.of(
                "acao", "IMPORTAR_DA_API"
        ));
    }
}
