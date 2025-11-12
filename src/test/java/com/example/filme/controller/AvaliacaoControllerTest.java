package com.example.filme.controller;

import com.example.filme.domain.avaliacao.AvaliacaoService;
import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosDetalhamentoAvaliacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoControllerTest {

    @Mock
    private AvaliacaoService avaliacaoService;

    @Captor
    private ArgumentCaptor<DadosCadastroAvaliacao> cadastroAvaliacaoCaptor;

    @Captor
    private ArgumentCaptor<DadosAlterarAvaliacao> alterarAvaliacaoCaptor;

    @InjectMocks
    private AvaliacaoController avaliacaoController;

    @Test
    @DisplayName("Deveria avaliar um filme")
    void deveriaAvaliar() {

        DadosCadastroAvaliacao dadosCadastro = new DadosCadastroAvaliacao(
                4.5f,
                100,
                200,
                "Muito bom!"
        );

        DadosDetalhamentoAvaliacao dadosDetalhamento = new DadosDetalhamentoAvaliacao(
                100,
                "Filme Teste",
                200,
                "usuario@email.com",
                4.5f,
                "Muito bom!"
        );

        Mockito.when(avaliacaoService.avaliar(Mockito.any(DadosCadastroAvaliacao.class)))
                .thenReturn(dadosDetalhamento);

        var response = avaliacaoController.avaliar(dadosCadastro);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(dadosDetalhamento, response.getBody());

        Mockito.verify(avaliacaoService).avaliar(cadastroAvaliacaoCaptor.capture());
        DadosCadastroAvaliacao capturado = cadastroAvaliacaoCaptor.getValue();

        assertNotNull(capturado);
        assertEquals(4.5f, capturado.nota());
        assertEquals(100, capturado.filme_id());
        assertEquals(200, capturado.usuario_id());
        assertEquals("Muito bom!", capturado.ds_avaliacao());
    }

    @Test
    @DisplayName("Deveria listar avaliações de um usuário pelo ID")
    void deveriaListarAvaliacoesDeUmUsuario() {
        // Arrange
        Integer idUsuario = 42;

        DadosDetalhamentoAvaliacao avaliacao1 = new DadosDetalhamentoAvaliacao(
                1, "Filme A", idUsuario, "usuario@email.com", 4.0f, "Muito bom"
        );
        DadosDetalhamentoAvaliacao avaliacao2 = new DadosDetalhamentoAvaliacao(
                2, "Filme B", idUsuario, "usuario@email.com", 3.5f, "Gostei"
        );
        List<DadosDetalhamentoAvaliacao> listaAvaliacoes = List.of(avaliacao1, avaliacao2);

        Mockito.when(avaliacaoService.listarAvaliacoesDeUmUsuario(idUsuario)).thenReturn(listaAvaliacoes);

        // Act
        var response = avaliacaoController.listarAvaliacoesDeUmUsuario(idUsuario);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(listaAvaliacoes, response.getBody());

        Mockito.verify(avaliacaoService).listarAvaliacoesDeUmUsuario(idUsuario);
    }

    @Test
    @DisplayName("Deveria listar avaliações de um filme pelo ID")
    void deveriaListarAvaliacoesDeUmFilme() {
        // Arrange
        Integer idFilme = 7;

        DadosDetalhamentoAvaliacao avaliacao1 = new DadosDetalhamentoAvaliacao(
                idFilme, "O Senhor dos Anéis", 101, "usuario1@email.com", 4.5f, "Obra-prima"
        );
        DadosDetalhamentoAvaliacao avaliacao2 = new DadosDetalhamentoAvaliacao(
                idFilme, "O Senhor dos Anéis", 102, "usuario2@email.com", 4.0f, "Muito bom"
        );
        List<DadosDetalhamentoAvaliacao> listaAvaliacoes = List.of(avaliacao1, avaliacao2);

        Mockito.when(avaliacaoService.listarAvaliacoesFilme(idFilme)).thenReturn(listaAvaliacoes);

        // Act
        var response = avaliacaoController.listarAvaliacoesDeUmFilme(idFilme);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(listaAvaliacoes, response.getBody());

        Mockito.verify(avaliacaoService).listarAvaliacoesFilme(idFilme);
    }

    @Test
    @DisplayName("Deveria listar a avaliação de um usuário para um filme")
    void deveriaListarAvaliacaoDoUsuarioParaFilme() {
        // Arrange
        Integer idFilme = 10;
        Integer idUsuario = 20;

        DadosDetalhamentoAvaliacao avaliacao = new DadosDetalhamentoAvaliacao(
                idFilme, "Interestelar", idUsuario, "usuario20@email.com", 5.0f, "Perfeito"
        );

        Mockito.when(avaliacaoService.listarAvaliacaoDeUmFilme(idFilme, idUsuario)).thenReturn(avaliacao);

        // Act
        var response = avaliacaoController.listarAvaliacaoDoUsuarioAoFilme(idFilme, idUsuario);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(avaliacao, response.getBody());

        Mockito.verify(avaliacaoService).listarAvaliacaoDeUmFilme(idFilme, idUsuario);
    }

    @Test
    @DisplayName("Deveria alterar uma avaliação existente")
    void deveriaAlterarAvaliacao() {
        // Arrange
        DadosAlterarAvaliacao dados = new DadosAlterarAvaliacao(
                1,
                4.5f,
                "Gostei mais depois da segunda vez"
        );

        DadosDetalhamentoAvaliacao retorno = new DadosDetalhamentoAvaliacao(
                1, "Matrix", 2, "usuario@email.com", 4.5f, "Gostei mais depois da segunda vez"
        );

        Mockito.when(avaliacaoService.atualizarAvaliacao(dados)).thenReturn(retorno);

        // Act
        var response = avaliacaoController.alterarAvaliacao(dados);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(retorno, response.getBody());

        Mockito.verify(avaliacaoService).atualizarAvaliacao(dados);
    }

    @Test
    @DisplayName("Deveria deletar uma avaliação com sucesso")
    void deveriaDeletarAvaliacao() {
        // Arrange
        Integer idAvaliacao = 10;

        // Act
        var response = avaliacaoController.deletarAvaliacao(idAvaliacao);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());

        Mockito.verify(avaliacaoService).deletarAvaliacao(idAvaliacao);
    }

}