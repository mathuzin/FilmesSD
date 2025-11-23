package com.example.filme.domain.filme;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Service
public class FilmePublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.app-topic-arn}")
    private String topicArn;

    public FilmePublisher(SnsClient snsClient, ObjectMapper objectMapper) {
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

    public void publicarCadastrar(Object dados) {
        enviar(Map.of(
                "acao", "CADASTRAR",
                "dados", dados
        ));
    }

    public void publicarAlterar(Object dados) {
        enviar(Map.of(
                "acao", "ALTERAR",
                "dados", dados
        ));
    }

    public void publicarAtualizarPopularidade(Integer idFilme) {
        enviar(Map.of(
                "acao", "ATUALIZAR_POPULARIDADE",
                "idFilme", idFilme
        ));
    }

    public void publicarImportarDaApi(Integer pagina) {
        enviar(Map.of(
                "acao", "IMPORTAR_DA_API",
                "pagina", pagina
        ));
    }
}
