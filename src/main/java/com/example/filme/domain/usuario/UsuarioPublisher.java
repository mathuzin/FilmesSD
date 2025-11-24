package com.example.filme.domain.usuario;

import com.example.filme.domain.usuario.dtos.DadosCadastrarUsuario;
import com.example.filme.domain.usuario.dtos.DadosEditarUsuario;
import com.example.filme.infra.aws.MensagemPublisher;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.example.filme.infra.aws.enums.AcaoMensagem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class UsuarioPublisher {

    private final MensagemPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public UsuarioPublisher(MensagemPublisher publisher) {
        this.publisher = publisher;
    }

    public void publicarCadastrar(DadosCadastrarUsuario dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "USUARIO",
                AcaoMensagem.CADASTRAR,
                mapper.valueToTree(dados),
                null,
                null,
                null
        ));
    }

    public void publicarAlterar(DadosEditarUsuario dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "USUARIO",
                AcaoMensagem.ALTERAR,
                mapper.valueToTree(dados),
                null,
                dados.id(),
                null
        ));
    }

    public void publicarDesativar(Integer idUsuario) {
        publisher.enviar(new AcaoMensagemDTO(
                "USUARIO",
                AcaoMensagem.DELETAR,
                null,
                null,
                idUsuario,
                null
        ));
    }

    public void publicarListar() {
        publisher.enviar(new AcaoMensagemDTO(
                "USUARIO",
                AcaoMensagem.LISTAR_USUARIO,
                null,
                null,
                null,
                null
        ));
    }
}