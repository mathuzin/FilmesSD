package com.example.filme.domain.filme_pessoa;

import com.example.filme.domain.filme_pessoa.dtos.DadosAlterarFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.infra.aws.MensagemPublisher;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.example.filme.infra.aws.enums.AcaoMensagem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class FilmePessoaPublisher {

    private final MensagemPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public FilmePessoaPublisher(MensagemPublisher publisher) {
        this.publisher = publisher;
    }

    public void publicarAdicionar(DadosCadastroFilmePessoa dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "FILME_PESSOA",
                AcaoMensagem.ADICIONAR,
                mapper.valueToTree(dados),
                dados.id_filme(),
                null,
                dados.id_pessoa()
        ));
    }

    public void publicarAlterar(DadosAlterarFilmePessoa dados) {
        publisher.enviar(new AcaoMensagemDTO(
                "FILME_PESSOA",
                AcaoMensagem.ALTERAR,
                mapper.valueToTree(dados),
                null,
                null,
                null
        ));
    }

    public void publicarDeletar(Integer idPessoa, Integer idFilme) {
        publisher.enviar(new AcaoMensagemDTO(
                "FILME_PESSOA",
                AcaoMensagem.DELETAR,
                null,
                idFilme,
                null,
                idPessoa
        ));
    }
}