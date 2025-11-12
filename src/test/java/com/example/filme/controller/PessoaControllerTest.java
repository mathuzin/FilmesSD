package com.example.filme.controller;

import com.example.filme.domain.pessoa.PessoaService;
import com.example.filme.domain.pessoa.Tipo;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.pessoa.dtos.DadosDetalhamentoPessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PessoaControllerTest {

    @Mock
    private PessoaService pessoaService;

    @InjectMocks
    private PessoaController pessoaController;

    private DadosCadastroPessoa dadosCadastroPessoa;
    private DadosDetalhamentoPessoa dadosDetalhamentoPessoa;

    private DadosAlterarPessoa dadosAlterarPessoa;
    private DadosDetalhamentoPessoa dadosDetalhamentoPessoaAlterado;

    private List<DadosDetalhamentoPessoa> listaPessoas;

    @BeforeEach
    void setUp() {
        dadosCadastroPessoa = new DadosCadastroPessoa(
                "João Silva",
                LocalDate.of(1985, 5, 20),
                "BRA",
                Tipo.ATOR
        );

        dadosDetalhamentoPessoa = new DadosDetalhamentoPessoa(
                1,
                "João Silva",
                LocalDate.of(1985, 5, 20),
                "BRA",
                "ATOR"
        );

        dadosAlterarPessoa = new DadosAlterarPessoa(
                1,
                "João Pedro Silva",
                Tipo.DIRETOR
        );

        dadosDetalhamentoPessoaAlterado = new DadosDetalhamentoPessoa(
                1,
                "João Pedro Silva",
                LocalDate.of(1985, 5, 20),
                "BRA",
                "DIRETOR"
        );

        listaPessoas = List.of(
                new DadosDetalhamentoPessoa(1, "João Silva", LocalDate.of(1985, 5, 20), "BRA", "ATOR"),
                new DadosDetalhamentoPessoa(2, "Maria Costa", LocalDate.of(1990, 3, 14), "USA", "DIRETOR")
        );
    }

    @Test
    @DisplayName("Deve listar todas as pessoas")
    void listarTodasAsPessoas() {
        var paginacao = PageRequest.of(0, 10);
        Page<DadosDetalhamentoPessoa> paginaEsperada = new PageImpl<>(listaPessoas);

        Mockito.when(pessoaService.listarTodasAsPessoas(paginacao)).thenReturn(paginaEsperada);

        ResponseEntity<Page<DadosDetalhamentoPessoa>> response = pessoaController.listarTodasAsPessoas(paginacao);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(paginaEsperada, response.getBody());

        Mockito.verify(pessoaService).listarTodasAsPessoas(paginacao);
    }

    @Test
    @DisplayName("Deve listar uma pessoa pelo ID")
    void listarPessoaPorIdComSucesso() {
        Integer id = 1;

        Mockito.when(pessoaService.listarPessoa(id)).thenReturn(dadosDetalhamentoPessoa);

        var response = pessoaController.listarPessoa(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dadosDetalhamentoPessoa, response.getBody());

        Mockito.verify(pessoaService).listarPessoa(id);
    }

    @Test
    @DisplayName("Deve alterar uma pessoa com sucesso")
    void alterarPessoaComSucesso() {
        Mockito.when(pessoaService.alterarPessoa(dadosAlterarPessoa)).thenReturn(dadosDetalhamentoPessoaAlterado);

        var response = pessoaController.alterarPessoa(dadosAlterarPessoa);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dadosDetalhamentoPessoaAlterado, response.getBody());

        Mockito.verify(pessoaService).alterarPessoa(dadosAlterarPessoa);
    }


}