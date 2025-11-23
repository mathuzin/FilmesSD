package com.example.filme.infra.aws;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class FilmeQueueListener {

    @SqsListener("${aws.sqs.queue-url}")
    public void receberMensagem(String mensagem) {
        System.out.println("Mensagem recebida da fila: " + mensagem);
    }
}