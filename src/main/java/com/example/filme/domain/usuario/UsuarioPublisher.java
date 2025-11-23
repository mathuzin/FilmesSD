package com.example.filme.domain.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Service
public class UsuarioPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.usuario-topic-arn}")
    private String topicArn;

    public UsuarioPublisher(SnsClient snsClient, ObjectMapper objectMapper) {
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

    public void publicarDesativar(Integer idUsuario) {
        enviar(Map.of(
                "acao", "DESATIVAR",
                "idUsuario", idUsuario
        ));
    }

    public void publicarListar() {
        enviar(Map.of(
                "acao", "LISTAR"
        ));
    }

    public void publicarListarAtivos() {
        enviar(Map.of(
                "acao", "LISTAR_ATIVOS"
        ));
    }

    public void publicarBuscar(Integer idUsuario) {
        enviar(Map.of(
                "acao", "BUSCAR",
                "idUsuario", idUsuario
        ));
    }
}
