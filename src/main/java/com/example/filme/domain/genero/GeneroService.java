package com.example.filme.domain.genero;

import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosDetalhamentoGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
import com.example.filme.domain.genero.exceptions.GeneroBadRequestException;
import com.example.filme.domain.genero.exceptions.GeneroDuplicadoException;
import com.example.filme.domain.genero.exceptions.GeneroNotFoundException;
import com.example.filme.infra.tmdbAPI.MovieServiceTMDB;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GeneroService {

    @Autowired
    private MovieServiceTMDB movieServiceTMDB;

    @Autowired
    private GeneroRepository generoRepository;

    // Adiciona um gênero ao banco de dados
    @Transactional
    public DadosDetalhamentoGenero adicionarGenero(@Valid DadosCadastrarGenero dados) {

        if (dados == null) {
            throw new GeneroBadRequestException("Dados para cadastrar não devem ser nulos.");
        }

        validarNomeDuplicado(dados.nome());

        var genero = new Genero(dados);

        generoRepository.save(genero);

        return new DadosDetalhamentoGenero(genero);

    }

    // Importa os gêneros da API para o banco de dados
    @Transactional
    public List<DadosDetalhamentoGenero> importarGenerosDaApi() {
        List<DadosDetalhamentoGenero> generosAdicionados = new ArrayList<>();

        var generosDaApi = movieServiceTMDB.buscarGeneros();

        List<Genero> novosGeneros = generosDaApi.stream()
                .filter(g -> !generoRepository.existsByNomeIgnoreCase(g.getName()))
                .map(g -> new Genero(null, g.getName()))
                .toList();

        generoRepository.saveAll(novosGeneros);

        generosAdicionados = novosGeneros.stream()
                .map(DadosDetalhamentoGenero::new)
                .toList();

        return generosAdicionados;
    }


    public List<DadosDetalhamentoGenero> listarTodos() {
        return generoRepository.findAll().stream()
                .map(DadosDetalhamentoGenero::new)
                .toList();
    }

    // Edita um gênero
    @Transactional
    public DadosDetalhamentoGenero editarGenero(@Valid DadosEditarGenero dados) {

        if (dados == null) {
            throw new GeneroBadRequestException("Dados para alterar não devem ser nulos.");
        }

        Genero genero = generoRepository.findById(dados.id())
                .orElseThrow(() -> new GeneroNotFoundException("Id de gênero não existe."));

        validarNomeDuplicado(dados.nome(), dados.id());

        genero.setNome(dados.nome());
        generoRepository.save(genero);

        return new DadosDetalhamentoGenero(genero);
    }

    private void validarNomeDuplicado(String nome) {
        if (generoRepository.existsByNomeIgnoreCase(nome)) {
            throw new GeneroDuplicadoException("Nome de gênero já existe.");
        }
    }

    private void validarNomeDuplicado(String nome, Integer idAtual) {
        Optional<Genero> generoExistente = generoRepository.findByNomeIgnoreCase(nome);
        if (generoExistente.isPresent() && !generoExistente.get().getId().equals(idAtual)) {
            throw new GeneroDuplicadoException("Nome de gênero já está em uso por outro registro.");
        }
    }


}