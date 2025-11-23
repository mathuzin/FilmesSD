package com.example.filme.domain.pessoa;

import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Service
public class PessoaPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.app-topic-arn}")
    private String topicArn;

    public PessoaPublisher(SnsClient snsClient, ObjectMapper objectMapper) {
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

    public void publicarAdicionar(DadosCadastroPessoa dados) {
        enviar(Map.of(
                "acao", "ADICIONAR",
                "dados", dados
        ));
    }

    public void publicarEditar(DadosAlterarPessoa dados) {
        enviar(Map.of(
                "acao", "EDITAR",
                "dados", dados
        ));
    }
}
