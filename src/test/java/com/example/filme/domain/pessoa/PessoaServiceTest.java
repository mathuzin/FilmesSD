package com.example.filme.domain.pessoa;

import com.example.filme.domain.filme.exceptions.FilmeBadRequestException;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.pessoa.dtos.DadosDetalhamentoPessoa;
import com.example.filme.domain.pessoa.exceptions.PessoaBadRequestException;
import com.example.filme.domain.pessoa.exceptions.PessoaNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Captor
    private ArgumentCaptor<Pessoa> pessoaCaptor;

    @InjectMocks
    private PessoaService pessoaService;

    private Pessoa pessoa;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.pessoa = new Pessoa();
        this.pessoa.setNome("Nome Antigo");
        this.pessoa.setTipo(Tipo.ATOR);
    }

    @Test
    @DisplayName("Deve retornar erro ao cadastrar com dados nulos")
    void deveriaLancarExceptionPoisDadosNulos() {

        Throwable exception = catchThrowable(() -> pessoaService.cadastrarPessoa(null));

        assertThat(exception)
                .isInstanceOf(PessoaBadRequestException.class)
                .hasMessage("Dados não podem ser nulos.");

    }

    @Test
    @DisplayName("Deve cadastrar uma pessoa")
    void deveriaCadastrarPessoaComSucesso() {
        // Arrange
        DadosCadastroPessoa dados = new DadosCadastroPessoa(
                "Keanu Reeves",
                LocalDate.of(1964, 9, 2),
                "CAN",
                Tipo.ATOR
        );

        Pessoa pessoa = new Pessoa(dados);

        // Simula que o repositório retorna a mesma pessoa que foi salva
        Mockito.when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        // Act
        Pessoa resultado = pessoaService.cadastrarPessoa(dados);

        // Assert
        assertNotNull(resultado);

        assertEquals("Keanu Reeves", resultado.getNome());
        assertEquals(LocalDate.of(1964, 9, 2), resultado.getDataNascimento());
        assertEquals("CAN", resultado.getOrigem());
        assertEquals(Tipo.ATOR, resultado.getTipo());

        Mockito.verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    @DisplayName("Deve listar todas as pessoas")
    void deveListarTodasAsPessoasComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);

        Pessoa p1 = new Pessoa(new DadosCadastroPessoa(
                "Keanu Reeves",
                LocalDate.of(1964, 9, 2),
                "Can",
                Tipo.ATOR
        ));

        Page<Pessoa> page = new PageImpl<>(List.of(p1));
        Mockito.when(pessoaRepository.findAll(pageable)).thenReturn(page);

        Page<DadosDetalhamentoPessoa> resultado = pessoaService.listarTodasAsPessoas(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Keanu Reeves", resultado.getContent().get(0).nome());
    }

    @Test
    @DisplayName("Deve lançar exceção se o ID da pessoa for nulo ao listar uma pessoa")
    void deveLancarExcecaoSeIdForNulo() {
        Throwable exception = catchThrowable(() -> pessoaService.listarPessoa(null));

        assertThat(exception)
                .isInstanceOf(PessoaBadRequestException.class)
                .hasMessage("ID não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção se a pessoa não for encontrada pelo ID")
    void deveLancarExcecaoSePessoaNaoForEncontrada() {
        Integer id = -999;
        Mockito.when(pessoaRepository.findById(id)).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> pessoaService.listarPessoa(id));

        assertThat(exception)
                .isInstanceOf(PessoaNotFoundException.class)
                .hasMessage("Pessoa não encontrada com ID: -999");
    }

    @Test
    @DisplayName("Deve retornar os dados da pessoa com sucesso")
    void deveRetornarPessoaComSucesso() {
        Integer id = 1;

        Pessoa pessoa = new Pessoa(new DadosCadastroPessoa(
                "Carrie-Anne Moss",
                LocalDate.of(1967, 8, 21),
                "Can",
                Tipo.ATOR
        ));

        Mockito.when(pessoaRepository.findById(id)).thenReturn(Optional.of(pessoa));

        DadosDetalhamentoPessoa resultado = pessoaService.listarPessoa(id);

        assertNotNull(resultado);
        assertEquals("Carrie-Anne Moss", resultado.nome());
        assertEquals(LocalDate.of(1967, 8, 21), resultado.dt_nascimento());
        assertEquals("Can", resultado.origem());
        assertEquals(Tipo.ATOR.toString(), resultado.tipo());
    }

    @Test
    @DisplayName("Deve lançar exceção se os dados forem nulos ao alterar")
    void deveLancarExcecaoSeDadosForemNulos() {
        Throwable exception = catchThrowable(() -> pessoaService.alterarPessoa(null));

        assertThat(exception)
                .isInstanceOf(PessoaBadRequestException.class)
                .hasMessage("Dados não podem ser nulos.");
    }

    @Test
    @DisplayName("Deve lançar exceção se a pessoa não for encontrada ao alterar")
    void deveLancarExcecaoSePessoaNaoForEncontradaAOEditar() {
        DadosAlterarPessoa dados = mock(DadosAlterarPessoa.class);
        Mockito.when(dados.id()).thenReturn(-99);

        Mockito.when(pessoaRepository.findById(-99)).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> pessoaService.alterarPessoa(dados));

        assertThat(exception)
                .isInstanceOf(PessoaNotFoundException.class)
                .hasMessage("Pessoa não encontrada com ID: -99");
    }

    @Test
    @DisplayName("Deve alterar somente o nome quando tipo for nulo")
    void deveAlterarSomenteONome() {
        DadosAlterarPessoa dados = mock(DadosAlterarPessoa.class);
        Mockito.when(dados.id()).thenReturn(pessoa.getId());
        Mockito.when(dados.nome()).thenReturn("Nome Novo");
        Mockito.when(dados.tipo()).thenReturn(null);

        Mockito.when(pessoaRepository.findById(pessoa.getId())).thenReturn(Optional.of(pessoa));
        Mockito.when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(i -> i.getArgument(0));

        DadosDetalhamentoPessoa resultado = pessoaService.alterarPessoa(dados);

        Mockito.verify(pessoaRepository).save(pessoaCaptor.capture());
        Pessoa pessoaSalva = pessoaCaptor.getValue();

        assertEquals("Nome Novo", pessoaSalva.getNome());
        assertEquals(Tipo.ATOR, pessoaSalva.getTipo());

        assertEquals("Nome Novo", resultado.nome());
        assertEquals(Tipo.ATOR.toString(), resultado.tipo());
    }

    @Test
    @DisplayName("Deve alterar somente o tipo quando nome for nulo")
    void deveAlterarSomenteOTipo() {
        DadosAlterarPessoa dados = mock(DadosAlterarPessoa.class);
        Mockito.when(dados.id()).thenReturn(pessoa.getId());
        Mockito.when(dados.nome()).thenReturn(null);
        Mockito.when(dados.tipo()).thenReturn(Tipo.DIRETOR);

        Mockito.when(pessoaRepository.findById(pessoa.getId())).thenReturn(Optional.of(pessoa));
        Mockito.when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(i -> i.getArgument(0));

        DadosDetalhamentoPessoa resultado = pessoaService.alterarPessoa(dados);

        Mockito.verify(pessoaRepository).save(pessoaCaptor.capture());
        Pessoa pessoaSalva = pessoaCaptor.getValue();

        assertEquals("Nome Antigo", pessoaSalva.getNome());
        assertEquals(Tipo.DIRETOR, pessoaSalva.getTipo());

        assertEquals("Nome Antigo", resultado.nome());
        assertEquals(Tipo.DIRETOR.toString(), resultado.tipo());
    }

    @Test
    @DisplayName("Deve alterar nome e tipo de uma pessoa existente")
    void deveAlterarPessoaComSucesso() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Nome Antigo");
        pessoa.setTipo(Tipo.ATOR);

        DadosAlterarPessoa dados = mock(DadosAlterarPessoa.class);
        Mockito.when(dados.id()).thenReturn(1);
        Mockito.when(dados.nome()).thenReturn("Nome Novo");
        Mockito.when(dados.tipo()).thenReturn(Tipo.DIRETOR);

        Mockito.when(pessoaRepository.findById(1)).thenReturn(Optional.of(pessoa));
        Mockito.when(pessoaRepository.save(pessoa)).thenReturn(pessoa);

        DadosDetalhamentoPessoa resultado = pessoaService.alterarPessoa(dados);

        assertNotNull(resultado);
        assertEquals("Nome Novo", resultado.nome());
        assertEquals("DIRETOR", resultado.tipo());
    }


}