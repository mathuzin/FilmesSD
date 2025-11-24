package com.example.filme.domain.pessoa;

import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.example.filme.infra.aws.MensagemPublisher;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.example.filme.infra.aws.enums.AcaoMensagem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class PessoaPublisher {

    private final MensagemPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public PessoaPublisher(MensagemPublisher publisher) {
        this.publisher = publisher;
    }

    public void publicarAdicionar(DadosCadastroPessoa dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "PESSOA",
                AcaoMensagem.CADASTRAR,
                mapper.valueToTree(dados),
                null,
                null,
                null
        ));
    }

    public void publicarEditar(DadosAlterarPessoa dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "PESSOA",
                AcaoMensagem.EDITAR,
                mapper.valueToTree(dados),
                null,
                null,
                null
        ));
    }
}