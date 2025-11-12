package com.example.filme.controller;

import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.genero.GeneroService;
import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosDetalhamentoGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GeneroControllerTest {

    @Mock
    private GeneroService generoService;

    @InjectMocks
    private GeneroController generoController;

    private DadosCadastrarGenero dadosCadastrar;
    private DadosDetalhamentoGenero dadosDetalhamento;

    private DadosEditarGenero dadosEditar;
    private DadosDetalhamentoGenero dadosDetalhamentoEditado;

    private List<DadosDetalhamentoGenero> listaGeneros;
    private List<DadosDetalhamentoGenero> generosImportados;

    @BeforeEach
    void setUp() {
        dadosCadastrar = new DadosCadastrarGenero("Ação");

        dadosDetalhamento = new DadosDetalhamentoGenero(1, "Ação");

        dadosEditar = new DadosEditarGenero(1, "Ação e Aventura");

        dadosDetalhamentoEditado = new DadosDetalhamentoGenero(1, "Ação e Aventura");

        listaGeneros = List.of(
                new DadosDetalhamentoGenero(1, "Ação"),
                new DadosDetalhamentoGenero(2, "Comédia"),
                new DadosDetalhamentoGenero(3, "Drama")
        );

        generosImportados = List.of(
                new DadosDetalhamentoGenero(4, "Terror"),
                new DadosDetalhamentoGenero(5, "Ficção Científica")
        );
    }

    @Test
    @DisplayName("Deveria cadastrar novo gênero")
    void deveriaCadastrarGeneroComSucesso() {
        // Arrange
        Mockito.when(generoService.adicionarGenero(dadosCadastrar)).thenReturn(dadosDetalhamento);

        // Act
        var response = generoController.cadastrarGenero(dadosCadastrar);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(dadosDetalhamento, response.getBody());

        Mockito.verify(generoService).adicionarGenero(dadosCadastrar);
    }

    @Test
    @DisplayName("Deveria importar e cadastrar gêneros da API com sucesso")
    void deveriaImportarGenerosDaApiComSucesso() {
        // Arrange
        Mockito.when(generoService.importarGenerosDaApi()).thenReturn(generosImportados);

        // Act
        var response = generoController.cadastrarGenerosDaApi();

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(generosImportados, response.getBody());

        Mockito.verify(generoService).importarGenerosDaApi();
    }

    @Test
    @DisplayName("Não deveria retornar conteúdo quando nenhum gênero for importado da API")
    void naoDeveriaRetornarConteudoQuandoImportacaoVazia() {
        // Arrange
        Mockito.when(generoService.importarGenerosDaApi()).thenReturn(List.of());

        // Act
        var response = generoController.cadastrarGenerosDaApi();

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());

        Mockito.verify(generoService).importarGenerosDaApi();
    }

    @Test
    @DisplayName("Deveria listar todos os gêneros com sucesso")
    void deveriaListarTodosOsGeneros() {
        // Arrange
        Mockito.when(generoService.listarTodos()).thenReturn(listaGeneros);

        // Act
        var response = generoController.listarGeneros();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(listaGeneros, response.getBody());

        Mockito.verify(generoService).listarTodos();
    }

    @Test
    @DisplayName("Deveria editar um gênero com sucesso")
    void deveriaEditarGeneroComSucesso() {
        // Arrange
        Mockito.when(generoService.editarGenero(dadosEditar)).thenReturn(dadosDetalhamentoEditado);

        // Act
        var response = generoController.editarGenero(dadosEditar);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(dadosDetalhamentoEditado, response.getBody());

        Mockito.verify(generoService).editarGenero(dadosEditar);
    }

}