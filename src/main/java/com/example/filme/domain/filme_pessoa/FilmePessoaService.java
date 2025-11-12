package com.example.filme.domain.filme_pessoa;

import com.example.filme.domain.filme.Filme;
import com.example.filme.domain.filme.FilmeRepository;
import com.example.filme.domain.filme.exceptions.FilmeBadRequestException;
import com.example.filme.domain.filme.exceptions.FilmeNotFoundException;
import com.example.filme.domain.filme_pessoa.dtos.DadosAlterarFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosDetalhamentoFilmePessoa;
import com.example.filme.domain.pessoa.Pessoa;
import com.example.filme.domain.pessoa.PessoaRepository;
import com.example.filme.domain.pessoa.Tipo;
import com.example.filme.domain.pessoa.dtos.DadosDetalhamentoPessoa;
import com.example.filme.domain.pessoa.exceptions.PessoaNotFoundException;
import com.example.filme.infra.tmdbAPI.MovieServiceTMDB;
import com.example.filme.infra.tmdbAPI.dtos.TMDBCredits;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FilmePessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private MovieServiceTMDB movieServiceTMDB;

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private FilmePessoaRepository filmePessoaRepository;

    // Adiciona um(a) ator/atriz ou diretor(a) ao filme
    @Transactional
    public DadosDetalhamentoFilmePessoa adicionarPessoaAoFilme(@Valid DadosCadastroFilmePessoa dados) {

        if (!pessoaRepository.existsById(dados.id_pessoa())) {
            throw new PessoaNotFoundException("Pessoa com ID " + dados.id_pessoa() + " não encontrada.");
        }

        if (!filmeRepository.existsById(dados.id_filme())) {
            throw new FilmeNotFoundException("Filme com ID " + dados.id_filme() + " não encontrado.");
        }

        validarPessoaJaAssociadaAoFilme(dados.id_filme(), dados.id_pessoa(), dados.papel());

        Filme filme = filmeRepository.getReferenceById(dados.id_filme());
        Pessoa pessoa = pessoaRepository.getReferenceById(dados.id_pessoa());

        FilmePessoa novoFilmePessoa = new FilmePessoa(dados, pessoa, filme);

        filmePessoaRepository.save(novoFilmePessoa);

        return new DadosDetalhamentoFilmePessoa(novoFilmePessoa);
    }

    // Lista os atores relacionados a um filme
    public Page<DadosDetalhamentoPessoa> buscarAtoresEmUmFilme(Integer idFilme, Pageable paginacao) {
        if (!filmeRepository.existsById(idFilme)) {
            throw new FilmeNotFoundException("Filme com ID " + idFilme + " não encontrado.");
        }

        return filmePessoaRepository.findByFilmeIdAndPapel(idFilme, Papel.ATOR, paginacao)
                .map(fp -> new DadosDetalhamentoPessoa(fp.getPessoa()));
    }

    // Lista os diretores relacionados a um filme
    public Page<DadosDetalhamentoPessoa> buscarDiretoresEmUmFilme(Integer idFilme, Pageable paginacao) {
        if (!filmeRepository.existsById(idFilme)) {
            throw new FilmeNotFoundException("Filme com ID " + idFilme + " não encontrado.");
        }

        return filmePessoaRepository.findByFilmeIdAndPapel(idFilme, Papel.DIRETOR, paginacao)
                .map(fp -> new DadosDetalhamentoPessoa(fp.getPessoa()));
    }

    // Altera o papel de uma pessoa em um filme
    @Transactional
    public DadosDetalhamentoFilmePessoa alterarPapelEmFilme(DadosAlterarFilmePessoa dados) {

        if (dados == null) {
            throw new FilmeBadRequestException("Dados para alteração de papel são inválidos ou nulos.");
        }

        if (!pessoaRepository.existsById(dados.id().getPessoaId())) {
            throw new PessoaNotFoundException("Pessoa com ID " + dados.id().getPessoaId() + " não encontrada.");
        }

        if (!filmeRepository.existsById(dados.id().getFilmeId())) {
            throw new FilmeNotFoundException("Filme com ID " + dados.id().getFilmeId() + " não encontrado.");
        }

        validarPessoaJaAssociadaAoFilme(dados.id().getFilmeId(), dados.id().getPessoaId(), dados.papel());

        FilmePessoa filmePessoaAlterado = filmePessoaRepository.getReferenceById(dados.id());

        if (dados.papel() != null) {
            filmePessoaAlterado.setPapel(dados.papel());
        }

        return new DadosDetalhamentoFilmePessoa(filmePessoaAlterado);

    }

    // Retira um(a) ator/atriz ou diretor(a) de um filme
    @Transactional
    public void deletarPessoaDeFilme(Integer idPessoa, Integer idFilme) {

        if (!pessoaRepository.existsById(idPessoa)) {
            throw new PessoaNotFoundException("Pessoa com ID " + idPessoa + " não encontrada.");
        }

        if (!filmeRepository.existsById(idFilme)) {
            throw new FilmeNotFoundException("Filme com ID " + idFilme + " não encontrado.");
        }

        List<FilmePessoa> relacoes = filmePessoaRepository.findByFilmeIdAndPessoaId(idFilme, idPessoa);

        if (relacoes.isEmpty()) {
            throw new IllegalArgumentException("Essa pessoa não está associada a este filme.");
        }

        filmePessoaRepository.deleteAll(relacoes);
    }


    // Verifica se a pessoa adicionada ou alterada já existe nesse filme com o mesmo papel
    protected void validarPessoaJaAssociadaAoFilme(Integer filmeId, Integer pessoaId, Papel papel) {
        if (filmePessoaRepository.existsByFilmeIdAndPessoaIdAndPapel(filmeId, pessoaId, papel)) {
            throw new FilmeBadRequestException("Pessoa já está associada como " + papel + " neste filme.");
        }
    }

    // Importa atores e diretores para o banco de daodos, relacionando com seu respectivo filme
    @Transactional
    public void importarAtoresEDiretoresDoFilme(Integer filmeId) {

        if (filmeId == null) {
            throw new FilmeBadRequestException("ID do filme não pode ser nulo.");
        }

        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new FilmeNotFoundException("Filme com ID " + filmeId + " não encontrado."));

        TMDBCredits credits = movieServiceTMDB.buscarCreditosDoFilme(filme.getTmdbId());

        // Importar Atores (cast)
        for (var cast : credits.getCast()) {
            Pessoa pessoa = pessoaRepository.findByNomeIgnoreCase(cast.getName())
                    .orElseGet(() -> pessoaRepository.save(new Pessoa(null, cast.getName(), Tipo.ATOR)));

            FilmePessoa filmePessoa = new FilmePessoa(
                    new DadosCadastroFilmePessoa(pessoa.getId(), filme.getId(), Papel.ATOR),
                    pessoa,
                    filme
            );

            filmePessoaRepository.save(filmePessoa);
        }

        // Importar Diretores (crew com job == "Director")
        for (var crew : credits.getCrew()) {
            if ("Director".equals(crew.getJob())) {
                Pessoa pessoa = pessoaRepository.findByNomeIgnoreCase(crew.getName())
                        .orElseGet(() -> pessoaRepository.save(new Pessoa(null, crew.getName(), Tipo.DIRETOR)));

                FilmePessoa filmePessoa = new FilmePessoa(
                        new DadosCadastroFilmePessoa(pessoa.getId(), filme.getId(), Papel.DIRETOR),
                        pessoa,
                        filme
                );

                filmePessoaRepository.save(filmePessoa);
            }
        }
    }
}