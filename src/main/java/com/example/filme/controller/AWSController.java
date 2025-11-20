package com.example.filme.controller;

import com.example.filme.infra.aws.MensagemSqsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("aws")
public class AWSController {

    @Autowired
    private MensagemSqsService mensagemSqsService;

    // Envia texto puro para a fila
    @PostMapping("/enviar")
    @Transactional
    public ResponseEntity<String> enviarMensagem(@RequestBody @Valid String mensagem) {

        mensagemSqsService.enviarMensagem(mensagem);

        return ResponseEntity.status(201)
                .body("Mensagem enviada para o SQS com sucesso!");
    }

    // Exemplo para enviar um objeto JSON qualquer
    @PostMapping("/enviar-json")
    @Transactional
    public ResponseEntity<String> enviarMensagemJson(@RequestBody Object dto) {

        mensagemSqsService.enviarMensagem(dto);

        return ResponseEntity.status(201)
                .body("JSON enviado para o SQS com sucesso!");
    }
}
