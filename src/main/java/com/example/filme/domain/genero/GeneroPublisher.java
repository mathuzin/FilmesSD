package com.example.filme.domain.genero;

import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
import com.example.filme.infra.aws.MensagemPublisher;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.example.filme.infra.aws.enums.AcaoMensagem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class GeneroPublisher {

    private final MensagemPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public GeneroPublisher(MensagemPublisher publisher) {
        this.publisher = publisher;
    }

    public void publicarAdicionar(DadosCadastrarGenero dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "GENERO",
                AcaoMensagem.CADASTRAR,
                mapper.valueToTree(dados),
                null,
                null,
                null
        ));
    }

    public void publicarEditar(DadosEditarGenero dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "GENERO",
                AcaoMensagem.EDITAR,
                mapper.valueToTree(dados),
                null,
                null,
                null
        ));
    }

    public void publicarImportarDaApi() {
        publisher.enviar(new AcaoMensagemDTO(
                "GENERO",
                AcaoMensagem.IMPORTAR_DA_API,
                null,
                null,
                null,
                null
        ));
    }
}