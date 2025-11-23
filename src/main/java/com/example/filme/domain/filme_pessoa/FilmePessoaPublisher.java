package com.example.filme.domain.filme_pessoa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Service
public class FilmePessoaPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.app-topic-arn}")
    private String topicArn;

    public FilmePessoaPublisher(SnsClient snsClient, ObjectMapper objectMapper) {
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

    public void publicarCadastrar(Object dadosCadastro) {
        enviar(Map.of(
                "acao", "CADASTRAR",
                "dados", dadosCadastro
        ));
    }

    public void publicarAlterar(Object dadosAlteracao) {
        enviar(Map.of(
                "acao", "ALTERAR",
                "dados", dadosAlteracao
        ));
    }

    public void publicarDeletar(Integer idPessoa, Integer idFilme) {
        enviar(Map.of(
                "acao", "DELETAR",
                "idPessoa", idPessoa,
                "idFilme", idFilme
        ));
    }
}
