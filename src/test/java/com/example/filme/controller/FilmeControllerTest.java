package com.example.filme.controller;

import com.example.filme.domain.filme.FilmeService;
import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
import com.example.filme.domain.filme.dtos.DadosDetalhamentoFilme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FilmeControllerTest {

    @Mock
    private FilmeService filmeService;

    @Captor
    private ArgumentCaptor<DadosCadastrarFilme> cadastrarFilme;

    @InjectMocks
    private FilmeController filmeController;

    private DadosCadastrarFilme dadosCadastrar;
    private DadosDetalhamentoFilme dadosDetalhamento;

    private DadosAlterarFilme dadosAlterar;
    private DadosDetalhamentoFilme dadosDetalhamentoAlterado;

    List<DadosDetalhamentoFilme> filmesRecomendados;
    List<DadosDetalhamentoFilme> filmesImportados;

    private List<DadosDetalhamentoFilme> listaFilmes;

    @BeforeEach
    void setUp() {
        dadosCadastrar = new DadosCadastrarFilme(
                "Matrix",
                1,
                "https://imagem.jpg",
                "Descrição do filme",
                1999
        );

        dadosDetalhamento = new DadosDetalhamentoFilme(
                10,
                "Matrix",
                9.8f,
                "Ação",
                "https://imagem.jpg",
                "Descrição do filme",
                1999
        );

        dadosAlterar = new DadosAlterarFilme(
                10,                    // id do filme a alterar
                "Matrix Reloaded",     // novo nome
                1,                     // id do gênero
                "https://novaimagem.jpg",
                "Descrição alterada",
                2003
        );

        dadosDetalhamentoAlterado = new DadosDetalhamentoFilme(
                10,
                "Matrix Reloaded",
                8.5f,
                "Ação",
                "https://novaimagem.jpg",
                "Descrição alterada",
                2003
        );

        filmesRecomendados = List.of(
                new DadosDetalhamentoFilme(1, "Filme A", 7.5f, "Ação", "urlA", "Descrição A", 2020),
                new DadosDetalhamentoFilme(2, "Filme B", 8.0f, "Comédia", "urlB", "Descrição B", 2021)
        );

        filmesImportados = List.of(
                new DadosDetalhamentoFilme(1, "Filme X", 7.0f, "Drama", "urlX", "Descrição X", 2019),
                new DadosDetalhamentoFilme(2, "Filme Y", 8.3f, "Aventura", "urlY", "Descrição Y", 2020)
        );
    }

    @Test
    @DisplayName("Deveria cadastrar um filme")
    void cadastrarFilmeComSucesso() {
        Mockito.when(filmeService.cadastrarFilme(Mockito.any())).thenReturn(dadosDetalhamento);

        var response = filmeController.adicionarFilme(dadosCadastrar);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(dadosDetalhamento, response.getBody());

        Mockito.verify(filmeService).cadastrarFilme(cadastrarFilme.capture());
        DadosCadastrarFilme capturado = cadastrarFilme.getValue();
        assertEquals("Matrix", capturado.nome());
        assertEquals(1, capturado.id_genero());
        assertEquals("https://imagem.jpg", capturado.posterUrl());
        assertEquals("Descrição do filme", capturado.descricao());
        assertEquals(1999, capturado.ano());
    }

    @Test
    @DisplayName("Deveria listar todos os filmes")
    void listarTodosOsFilmes() {
        Mockito.when(filmeService.listarTodosFilmes()).thenReturn(listaFilmes);

        var response = filmeController.listarTodosOsFilmes();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(listaFilmes, response.getBody());

        Mockito.verify(filmeService).listarTodosFilmes();
    }

    @Test
    @DisplayName("Deveria listar um filme pelo ID")
    void listarFilmePorId() {
        Integer id = 10;

        Mockito.when(filmeService.listarFilme(id)).thenReturn(dadosDetalhamento);

        var response = filmeController.listarFilme(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dadosDetalhamento, response.getBody());

        Mockito.verify(filmeService).listarFilme(id);
    }

    @Test
    @DisplayName("Deveria alterar um filme com sucesso")
    void alterarFilmeComSucesso() {

        Mockito.when(filmeService.alterarFilme(dadosAlterar)).thenReturn(dadosDetalhamentoAlterado);

        var response = filmeController.alterarFilme(dadosAlterar);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dadosDetalhamentoAlterado, response.getBody());

        Mockito.verify(filmeService).alterarFilme(dadosAlterar);
    }

    @Test
    @DisplayName("Deveria retornar filmes recomendados para um usuário")
    void deveriaRetornarFilmesRecomendados() {
        Integer idUsuario = 123;

        Mockito.when(filmeService.filmesRecomendadosParaUsuario(idUsuario))
                .thenReturn(filmesRecomendados);

        var response = filmeController.filmesRecomendados(idUsuario);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(filmesRecomendados, response.getBody());

        Mockito.verify(filmeService).filmesRecomendadosParaUsuario(idUsuario);
    }

    @Test
    @DisplayName("Deveria importar filmes da API e retornar 201 com lista")
    void deveriaImportarFilmesDaApiComSucesso() {
        int pagina = 1;

        Mockito.when(filmeService.importarFilmesDaApi(pagina))
                .thenReturn(filmesImportados);

        var response = filmeController.inserirFilmesDaApiNoBD(pagina);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(filmesImportados, response.getBody());

        Mockito.verify(filmeService).importarFilmesDaApi(pagina);
    }

    @Test
    @DisplayName("Deveria retornar 204 quando não houver filmes importados")
    void deveriaRetornarNoContentQuandoNenhumFilmeImportado() {
        int pagina = 2;

        Mockito.when(filmeService.importarFilmesDaApi(pagina))
                .thenReturn(List.of());

        var response = filmeController.inserirFilmesDaApiNoBD(pagina);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());

        Mockito.verify(filmeService).importarFilmesDaApi(pagina);
    }

}