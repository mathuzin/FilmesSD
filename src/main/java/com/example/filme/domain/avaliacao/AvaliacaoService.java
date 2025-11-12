package com.example.filme.domain.avaliacao;

import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosDetalhamentoAvaliacao;
import com.example.filme.domain.avaliacao.exceptions.AvaliacaoBadRequestException;
import com.example.filme.domain.avaliacao.exceptions.AvaliacaoNotFoundException;
import com.example.filme.domain.filme.FilmeRepository;
import com.example.filme.domain.filme.FilmeService;
import com.example.filme.domain.filme.exceptions.FilmeBadRequestException;
import com.example.filme.domain.filme.exceptions.FilmeNotFoundException;
import com.example.filme.domain.usuario.UsuarioRepository;
import com.example.filme.domain.usuario.exceptions.UsuarioBadRequestException;
import com.example.filme.domain.usuario.exceptions.UsuarioNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvaliacaoService {

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    // Faz uma nova avaliação para um filme
    @Transactional
    public DadosDetalhamentoAvaliacao avaliar(@Valid DadosCadastroAvaliacao dados) {
        if (dados == null) {
            throw new AvaliacaoBadRequestException("Dados da avaliação não podem ser nulos.");
        }

        if (!usuarioRepository.existsById(dados.usuario_id())) {
            throw new UsuarioNotFoundException("Usuário com ID " + dados.usuario_id() + " não encontrado.");
        }

        if (!filmeRepository.existsById(dados.filme_id())) {
            throw new FilmeNotFoundException("Filme com ID " + dados.filme_id() + " não encontrado.");
        }

        boolean jaAvaliado = avaliacaoRepository.existsByFilmeIdAndUsuarioId(dados.filme_id(), dados.usuario_id());
        if (jaAvaliado) {
            throw new AvaliacaoBadRequestException("Este usuário já avaliou este filme.");
        }

        var usuario = usuarioRepository.getReferenceById(dados.usuario_id());
        var filme = filmeRepository.getReferenceById(dados.filme_id());

        var avaliacao = new Avaliacao(dados, filme, usuario);
        avaliacaoRepository.save(avaliacao);
        filmeService.atualizarPopularidade(filme.getId());

        return new DadosDetalhamentoAvaliacao(avaliacao);
    }


    // Lista todas as avaliações relacionada a um usuário
    public List<DadosDetalhamentoAvaliacao> listarAvaliacoesDeUmUsuario(Integer id_usuario) {

        if (id_usuario == null) {
            throw new UsuarioBadRequestException("Id de usuário não pode ser nulo.");
        }

        if (!usuarioRepository.existsById(id_usuario)) {
            throw new UsuarioNotFoundException("Usuário com ID " + id_usuario + " não encontrado.");
        }

        return avaliacaoRepository.findByUsuarioId(id_usuario).stream()
                .map(DadosDetalhamentoAvaliacao::new)
                .toList();
    }


    // Lista todas as avaliações relacionada a um filme
    public List<DadosDetalhamentoAvaliacao> listarAvaliacoesFilme(Integer id_filme) {

        if (id_filme == null) {
            throw new FilmeBadRequestException("Id do Filme não pode ser nulo.");
        }

        if (!filmeRepository.existsById(id_filme)) {
            throw new FilmeNotFoundException("Filme com ID " + id_filme + " não encontrado.");
        }

        return avaliacaoRepository.findByFilmeId(id_filme).stream()
                .map(DadosDetalhamentoAvaliacao::new)
                .toList();
    }


    // Lista a avaliação feita pelo usuário para um filme
    public DadosDetalhamentoAvaliacao listarAvaliacaoDeUmFilme(Integer id_filme, Integer id_usuario) {

        if (id_filme == null) {
            throw new FilmeBadRequestException("Id do Filme não pode ser nulo.");
        }

        if (!filmeRepository.existsById(id_filme)) {
            throw new FilmeNotFoundException("Filme com ID " + id_filme + " não encontrado.");
        }

        if (id_usuario == null) {
            throw new UsuarioBadRequestException("Id do Usuário não pode ser nulo.");
        }

        if (!usuarioRepository.existsById(id_usuario)) {
            throw new UsuarioNotFoundException("Usuário com ID " + id_usuario + " não encontrado.");
        }

        var avaliacao = avaliacaoRepository.findByFilmeIdAndUsuarioId(id_filme, id_usuario)
                .orElseThrow(() -> new AvaliacaoNotFoundException("Este usuário ainda não avaliou este filme."));

        return new DadosDetalhamentoAvaliacao(avaliacao);

    }

    // Atualiza uma avaliação
    @Transactional
    public DadosDetalhamentoAvaliacao atualizarAvaliacao(@Valid DadosAlterarAvaliacao dados) {

        if (dados == null) {
            throw new AvaliacaoBadRequestException("Dados não podem ser nulos");
        }

        if (!avaliacaoRepository.existsById(dados.id())) {
            throw new AvaliacaoNotFoundException("Avaliação com ID " + dados.id() + " não encontrada.");
        }

        Avaliacao avaliacao = avaliacaoRepository.getReferenceById(dados.id());

        if (dados.nota() != null) {
            avaliacao.setNota(dados.nota());
        }

        if (dados.ds_avaliacao() != null) {
            avaliacao.setDs_avaliacao(dados.ds_avaliacao());
        }

        avaliacaoRepository.save(avaliacao);

        filmeService.atualizarPopularidade(avaliacao.getFilme().getId());

        return new DadosDetalhamentoAvaliacao(avaliacao);

    }

    // Deleta uma avaliação
    @Transactional
    public void deletarAvaliacao(Integer idAvaliacao) {

        if (idAvaliacao == null) {
            throw new AvaliacaoBadRequestException("Não existe avaliação com ID nulo");
        }

        Avaliacao avaliacao = avaliacaoRepository.findById(idAvaliacao)
                .orElseThrow(() -> new AvaliacaoNotFoundException("Avaliação com ID " + idAvaliacao + " não encontrada."));

        avaliacaoRepository.delete(avaliacao);

        filmeService.atualizarPopularidade(avaliacao.getFilme().getId());
    }
}
