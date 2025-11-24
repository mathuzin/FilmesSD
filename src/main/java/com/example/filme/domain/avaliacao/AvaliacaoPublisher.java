package com.example.filme.domain.avaliacao;

import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.infra.aws.MensagemPublisher;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.example.filme.infra.aws.enums.AcaoMensagem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class AvaliacaoPublisher {

    private final MensagemPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public AvaliacaoPublisher(MensagemPublisher publisher) {
        this.publisher = publisher;
    }

    public void publicarCadastrar(DadosCadastroAvaliacao dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "AVALIACAO",
                AcaoMensagem.CADASTRAR,
                mapper.valueToTree(dados),
                null,
                null,
                null
        ));
    }

    public void publicarAlterar(DadosAlterarAvaliacao dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "AVALIACAO",
                AcaoMensagem.ALTERAR,
                mapper.valueToTree(dados),
                null,
                null,
                null
        ));
    }

    public void publicarDeletar(Integer idAvaliacao) {
        publisher.enviar(new AcaoMensagemDTO(
                "AVALIACAO",
                AcaoMensagem.DELETAR,
                null,
                idAvaliacao,
                null,
                null
        ));
    }

    public void publicarListarUsuario(Integer idUsuario) {
        publisher.enviar(new AcaoMensagemDTO(
                "AVALIACAO",
                AcaoMensagem.LISTAR_USUARIO,
                null,
                null,
                idUsuario,
                null
        ));
    }

    public void publicarListarFilme(Integer idFilme) {
        publisher.enviar(new AcaoMensagemDTO(
                "AVALIACAO",
                AcaoMensagem.LISTAR_FILME,
                null,
                idFilme,
                null,
                null
        ));
    }

    public void publicarListarUnica(Integer idFilme, Integer idUsuario) {
        publisher.enviar(new AcaoMensagemDTO(
                "AVALIACAO",
                AcaoMensagem.LISTAR_UNICA,
                null,
                idFilme,
                idUsuario,
                null
        ));
    }
}