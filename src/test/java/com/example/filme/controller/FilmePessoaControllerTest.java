package com.example.filme.controller;

import com.example.filme.domain.filme_pessoa.FilmePessoaService;
import com.example.filme.domain.filme_pessoa.Papel;
import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosDetalhamentoFilmePessoa;
import com.example.filme.domain.pessoa.dtos.DadosDetalhamentoPessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FilmePessoaControllerTest {

    @Mock
    private FilmePessoaService filmePessoaService;

    @Captor
    private ArgumentCaptor<DadosCadastroFilmePessoa> cadastroFilmePessoaCaptor;

    @InjectMocks
    private FilmePessoaController filmePessoaController;

    private DadosCadastroFilmePessoa dadosCadastroFilmePessoa;
    private DadosDetalhamentoFilmePessoa dadosDetalhamentoFilmePessoa;

    private Page<DadosDetalhamentoPessoa> paginaAtores;
    private Page<DadosDetalhamentoPessoa> paginaDiretores;

    @BeforeEach
    void setUp() {
        dadosCadastroFilmePessoa = new DadosCadastroFilmePessoa(
                200,
                100,
                Papel.ATOR
        );

        dadosDetalhamentoFilmePessoa = new DadosDetalhamentoFilmePessoa(
                200,
                "Pessoa Exemplo",
                100,
                "Filme Exemplo",
                Papel.ATOR.toString()
        );

        DadosDetalhamentoPessoa ator1 = new DadosDetalhamentoPessoa(
                200,
                "Ator Exemplo 1",
                LocalDate.of(1980, 1, 1),
                "Brasil",
                "ATOR"
        );

        DadosDetalhamentoPessoa ator2 = new DadosDetalhamentoPessoa(
                201,
                "Ator Exemplo 2",
                LocalDate.of(1990, 5, 10),
                "Brasil",
                "ATOR"
        );

        paginaAtores = new PageImpl<>(List.of(ator1, ator2));

        DadosDetalhamentoPessoa diretor1 = new DadosDetalhamentoPessoa(
                300,
                "Diretor Exemplo 1",
                LocalDate.of(1975, 3, 15),
                "Estados Unidos",
                "DIRETOR"
        );

        DadosDetalhamentoPessoa diretor2 = new DadosDetalhamentoPessoa(
                301,
                "Diretor Exemplo 2",
                LocalDate.of(1968, 7, 22),
                "Estados Unidos",
                "DIRETOR"
        );

        paginaDiretores = new PageImpl<>(List.of(diretor1, diretor2));
    }

    @Test
    @DisplayName("Deveria cadastrar Pessoa a um Filme")
    void deveriaCadastrarPessoaAFilme() {

        Mockito.when(filmePessoaService.adicionarPessoaAoFilme(Mockito.any(DadosCadastroFilmePessoa.class)))
                .thenReturn(dadosDetalhamentoFilmePessoa);

        var response = filmePessoaController.cadastrarPessoaAFilme(dadosCadastroFilmePessoa);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(dadosDetalhamentoFilmePessoa, response.getBody());

        Mockito.verify(filmePessoaService).adicionarPessoaAoFilme(cadastroFilmePessoaCaptor.capture());
        DadosCadastroFilmePessoa capturado = cadastroFilmePessoaCaptor.getValue();

        assertNotNull(capturado);
        assertEquals(dadosCadastroFilmePessoa.id_pessoa(), capturado.id_pessoa());
        assertEquals(dadosCadastroFilmePessoa.id_filme(), capturado.id_filme());
        assertEquals(dadosCadastroFilmePessoa.papel(), capturado.papel());
    }

    @Test
    void deveriaListarAtoresEmUmFilme() {
        // Arrange
        Integer idFilme = 100;
        Pageable paginacao = Pageable.unpaged();

        Mockito.when(filmePessoaService.buscarAtoresEmUmFilme(idFilme, paginacao))
                .thenReturn(paginaAtores);

        // Act
        var response = filmePessoaController.listarAtoresEmUmFilme(idFilme, paginacao);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(paginaAtores, response.getBody());

        Mockito.verify(filmePessoaService).buscarAtoresEmUmFilme(idFilme, paginacao);
    }

    @Test
    void deveriaListarDiretoresEmUmFilme() {
        // Arrange
        Integer idFilme = 100;
        Pageable paginacao = Pageable.unpaged();

        Mockito.when(filmePessoaService.buscarDiretoresEmUmFilme(idFilme, paginacao))
                .thenReturn(paginaDiretores);

        // Act
        var response = filmePessoaController.listarDiretoresEmUmFilme(idFilme, paginacao);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(paginaDiretores, response.getBody());

        Mockito.verify(filmePessoaService).buscarDiretoresEmUmFilme(idFilme, paginacao);
    }

}