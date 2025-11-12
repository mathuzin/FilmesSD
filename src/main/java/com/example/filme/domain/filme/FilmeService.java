package com.example.filme.domain.filme;

import com.example.filme.domain.avaliacao.Avaliacao;
import com.example.filme.domain.avaliacao.AvaliacaoRepository;
import com.example.filme.domain.filme.dtos.*;
import com.example.filme.domain.filme.exceptions.FilmeBadRequestException;
import com.example.filme.domain.filme.exceptions.FilmeNotFoundException;
import com.example.filme.domain.filme_pessoa.FilmePessoaService;
import com.example.filme.domain.genero.Genero;
import com.example.filme.domain.genero.GeneroRepository;
import com.example.filme.domain.genero.exceptions.GeneroNotFoundException;
import com.example.filme.domain.usuario.UsuarioRepository;
import com.example.filme.domain.usuario.exceptions.UsuarioBadRequestException;
import com.example.filme.domain.usuario.exceptions.UsuarioNotFoundException;
import com.example.filme.infra.tmdbAPI.MovieServiceTMDB;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmeService {

    @Autowired
    private FilmePessoaService filmePessoaService;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private MovieServiceTMDB movieServiceTMDB;

    @Autowired
    private FilmeRepository filmeRepository;

    // Cria um filme manualmente
    @Transactional
    public DadosDetalhamentoFilme cadastrarFilme(@Valid DadosCadastrarFilme dados) {
        if (dados == null) {
            throw new FilmeBadRequestException("Dados não podem ser nulos");
        }

        Genero genero = generoRepository.findById(dados.id_genero())
                .orElseThrow(() -> new GeneroNotFoundException("Gênero com ID " + dados.id_genero() + " não encontrado."));


        Filme filme = new Filme(dados);
        filme.setGenero(genero);

        filmeRepository.save(filme);
        return new DadosDetalhamentoFilme(filme);
    }

    // Lista todos os filmes do banco de dados
    public List<DadosDetalhamentoFilme> listarTodosFilmes() {
        return filmeRepository.findAll().stream()
                .map(DadosDetalhamentoFilme::new)
                .toList();
    }

    // Lista um filme específico
    public DadosDetalhamentoFilme listarFilme(Integer idFilme) {
        if (idFilme == null) {
            throw new FilmeBadRequestException("ID do filme não pode ser nulo");
        }

        Filme filme = filmeRepository.findById(idFilme)
                .orElseThrow(() -> new FilmeNotFoundException("Filme não encontrado com ID: " + idFilme));

        return new DadosDetalhamentoFilme(filme);
    }

    // Altera informações de um filme
    @Transactional
    public DadosDetalhamentoFilme alterarFilme(@Valid DadosAlterarFilme dados) {
        if (dados == null) {
            throw new FilmeBadRequestException("Dados inválidos para alteração");
        }

        Filme filmeAlterado = filmeRepository.findById(dados.id())
                .orElseThrow(() -> new FilmeNotFoundException("Filme não encontrado com ID: " + dados.id()));

        if (dados.nome() != null) {
            filmeAlterado.setNome(dados.nome());
        }

        if (dados.id_genero() != null) {
            Genero genero = generoRepository.findById(dados.id_genero())
                    .orElseThrow(() -> new GeneroNotFoundException("Gênero não encontrado com ID: " + dados.id_genero()));
            filmeAlterado.setGenero(genero);
        }

        if (dados.posterUrl() != null) {
            filmeAlterado.setPosterUrl(dados.posterUrl());
        }

        if (dados.descricao() != null) {
            filmeAlterado.setDescricao(dados.descricao());
        }

        if (dados.ano() != null) {
            filmeAlterado.setAno(dados.ano());
        }


        filmeRepository.save(filmeAlterado);
        return new DadosDetalhamentoFilme(filmeAlterado);
    }

    // Recomenda filmes para um usuário de acordo com as avaliações feitas
    public List<DadosDetalhamentoFilme> filmesRecomendadosParaUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            throw new UsuarioBadRequestException("ID de usuário inválido.");
        }

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioNotFoundException("Usuário com ID " + idUsuario + " não encontrado.");
        }

        return filmeRepository.recomendarFilmesParecidos(idUsuario).stream()
                .map(DadosDetalhamentoFilme::new)
                .toList();
    }


    // Atualiza a média de popularidade do filme
    @Transactional
    public void atualizarPopularidade(Integer idFilme) {
        if (idFilme == null) {
            throw new FilmeBadRequestException("ID do filme não pode ser nulo");
        }

        Filme filme = filmeRepository.findById(idFilme)
                .orElseThrow(() -> new FilmeNotFoundException("Filme não encontrado com ID: " + idFilme));

        List<Avaliacao> avaliacoes = avaliacaoRepository.findByFilmeId(idFilme);
        Float media = (float) avaliacoes.stream().mapToDouble(Avaliacao::getNota).average().orElse(0.0);

        filme.setPopularidade(media);
        filmeRepository.save(filme);
    }

    // Importa filmes da API para o banco de dados
    @Transactional
    public List<DadosDetalhamentoFilme> importarFilmesDaApi(int pagina) {
        if (pagina < 0) {
            throw new FilmeBadRequestException("Página não pode ser menor que 0");
        }

        List<DadosDetalhamentoFilme> filmesAdicionados = new ArrayList<>();

        var filmesDaApi = movieServiceTMDB.buscarFilmesPorPagina(pagina);

        for (var resultado : filmesDaApi) {

            if (filmeRepository.findByTmdbId(resultado.getId()).isPresent()) {
                continue;
            }

            var detalhes = movieServiceTMDB.buscarDetalhesDoFilme(resultado.getId());

            Filme filme = new Filme();
            filme.setNome(detalhes.getTitle());
            filme.setDescricao(detalhes.getOverview());
            filme.setPosterUrl(detalhes.getPoster_path());
            filme.setAno(Integer.parseInt(detalhes.getRelease_date().split("-")[0]));
            filme.setTmdbId(detalhes.getId());
            filme.setPopularidade(0f);

            String nomeGenero = detalhes.getGenres().get(0).getName();
            Genero genero = generoRepository.findByNomeIgnoreCase(nomeGenero)
                    .orElseGet(() -> generoRepository.save(new Genero(null, nomeGenero)));
            filme.setGenero(genero);


            filmeRepository.save(filme);
            filmePessoaService.importarAtoresEDiretoresDoFilme(filme.getId());

            DadosDetalhamentoFilme filmeDetalhado = new DadosDetalhamentoFilme(filme);
            filmesAdicionados.add(filmeDetalhado);
        }

        return filmesAdicionados;
    }
}
