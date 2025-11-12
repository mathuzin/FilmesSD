package com.example.filme.domain.usuario;

import com.example.filme.domain.pessoa.Pessoa;
import com.example.filme.domain.pessoa.PessoaRepository;
import com.example.filme.domain.pessoa.PessoaService;
import com.example.filme.domain.pessoa.Tipo;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.example.filme.domain.pessoa.exceptions.PessoaNotFoundException;
import com.example.filme.domain.usuario.dtos.DadosDetalhamentoUsuario;
import com.example.filme.domain.usuario.dtos.DadosEditarUsuario;
import com.example.filme.domain.usuario.exceptions.UsuarioBadRequestException;
import com.example.filme.domain.usuario.exceptions.UsuarioDuplicadoException;
import com.example.filme.domain.usuario.exceptions.UsuarioNotFoundException;
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

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Pessoa pessoa;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.pessoa = new Pessoa();
        this.pessoa.setTipo(Tipo.USUARIO);
        this.pessoa.setNome("Fulano");
        this.pessoa.setOrigem("BRA");
        this.pessoa.setDataNascimento(LocalDate.of(2000, 5, 15));

        this.usuario = new Usuario();
        this.usuario.setSenha("12345");
        this.usuario.setLogin("email@email.com.br");
        this.usuario.setAtivo(true);
        this.usuario.setPessoa(pessoa);
    }

    @Test
    @DisplayName("Deve listar todos os usuários com paginação")
    void deveriaListarTodosUsuarios() {
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Usuario> page = new PageImpl<>(List.of(usuario));

        Mockito.when(usuarioRepository.findAll(paginacao)).thenReturn(page);

        var resultado = usuarioService.listarTodosUsuarios(paginacao);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("email@email.com.br", resultado.getContent().get(0).login());
    }

    @Test
    @DisplayName("Deve listar todos os usuários ativos com paginação")
    void deveriaListarTodosUsuariosAtivos() {
        // ARRANGE
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Usuario> page = new PageImpl<>(List.of(usuario));

        Mockito.when(usuarioRepository.findByAtivoTrue(paginacao)).thenReturn(page);

        // ACT
        Page<DadosDetalhamentoUsuario> resultado = usuarioService.listarTodosUsuariosAtivos(paginacao);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertTrue(resultado.getContent().get(0).login().contains("email"));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver usuários ativos")
    void deveriaRetornarPaginaVaziaSeNaoHouverUsuariosAtivos() {
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Usuario> paginaVazia = Page.empty(paginacao);

        Mockito.when(usuarioRepository.findByAtivoTrue(paginacao)).thenReturn(paginaVazia);

        Page<DadosDetalhamentoUsuario> resultado = usuarioService.listarTodosUsuariosAtivos(paginacao);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar os dados do usuário quando o ID for válido")
    void deveriaBuscarUsuarioPorId() {
        // ARRANGE
        Integer idUsuario = 1;
        Usuario usuarioMock = usuario;

        Mockito.when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioMock));

        // ACT
        DadosDetalhamentoUsuario resultado = usuarioService.buscarUsuario(idUsuario);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("email@email.com.br", resultado.login());
    }

    @Test
    @DisplayName("Deve lançar exceção se o ID for nulo")
    void deveriaLancarExcecaoSeIdForNulo() {
        // ACT + ASSERT
        Throwable exception = catchThrowable(() -> usuarioService.buscarUsuario(null));

        assertThat(exception)
                .isInstanceOf(UsuarioBadRequestException.class)
                .hasMessage("Id do usuário não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção se o ID não existir")
    void deveriaLancarExcecaoSeIdNaoExistir() {
        // ACT + ASSERT
        Throwable exception = catchThrowable(() -> usuarioService.buscarUsuario(-9));

        assertThat(exception)
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Usuário com ID -9 não encontrado.");
    }

    @Test
    @DisplayName("Deve lançar exceção se login já estiver em uso por outro usuário")
    void deveLancarExcecaoSeLoginDuplicado() {
        DadosEditarUsuario dados = Mockito.mock(DadosEditarUsuario.class);
        Mockito.when(dados.id()).thenReturn(1);
        Mockito.when(dados.login()).thenReturn("loginexistente@email.com");

        Usuario usuarioExistente = new Usuario("loginexistente@email.com", "senha", Perfil.COMUM);

        Usuario outroUsuario = Mockito.mock(Usuario.class);
        Mockito.when(outroUsuario.getId()).thenReturn(2);

        Mockito.when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioExistente));
        Mockito.when(usuarioRepository.findByLoginIgnoreCase("loginexistente@email.com"))
                .thenReturn(Optional.of(outroUsuario));

        UsuarioDuplicadoException ex = assertThrows(UsuarioDuplicadoException.class, () -> {
            usuarioService.editarUsuario(dados);
        });

        assertEquals("Login já está em uso por outro registro.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve editar login e senha do usuário com sucesso")
    void deveEditarLoginESenhaComSucesso() {
        DadosEditarUsuario dados = Mockito.mock(DadosEditarUsuario.class);
        Mockito.when(dados.id()).thenReturn(usuario.getId());
        Mockito.when(dados.login()).thenReturn("novologin@email.com");
        Mockito.when(dados.senha()).thenReturn("novasenha");

        Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioRepository.findByLoginIgnoreCase("novologin@email.com")).thenReturn(Optional.empty());
        Mockito.when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoUsuario resultado = usuarioService.editarUsuario(dados);

        assertNotNull(resultado);
        assertEquals("novologin@email.com", resultado.login());
        assertEquals("novasenha", usuario.getSenha());
    }

    @Test
    @DisplayName("Deve editar apenas senha se login for null")
    void deveEditarApenasSenha() {
        DadosEditarUsuario dados = Mockito.mock(DadosEditarUsuario.class);
        Mockito.when(dados.id()).thenReturn(usuario.getId());
        Mockito.when(dados.login()).thenReturn(null);
        Mockito.when(dados.senha()).thenReturn("novasenha");

        Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoUsuario resultado = usuarioService.editarUsuario(dados);

        assertNotNull(resultado);
        assertEquals("novasenha", usuario.getSenha());
        assertEquals("email@email.com.br", usuario.getLogin());
    }

    @Test
    @DisplayName("Deve editar apenas login se senha for null")
    void deveEditarApenasLogin() {
        DadosEditarUsuario dados = Mockito.mock(DadosEditarUsuario.class);
        Mockito.when(dados.id()).thenReturn(usuario.getId());
        Mockito.when(dados.login()).thenReturn("novologin@email.com");
        Mockito.when(dados.senha()).thenReturn(null);

        Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioRepository.findByLoginIgnoreCase("novologin@email.com")).thenReturn(Optional.empty());
        Mockito.when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoUsuario resultado = usuarioService.editarUsuario(dados);

        assertNotNull(resultado);
        assertEquals("novologin@email.com", resultado.login());
        assertEquals("12345", usuario.getSenha());
    }

    @Test
    @DisplayName("Deve lançar exceção se dados forem nulos")
    void deveLancarExcecaoSeDadosForemNulos() {

        DadosEditarUsuario dados = null;

        Throwable exception = catchThrowable(() -> usuarioService.editarUsuario(dados));

        assertThat(exception)
                .isInstanceOf(UsuarioBadRequestException.class)
                .hasMessage("Dados para editar usuário não devem ser nulos");
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não for encontrado")
    void deveLancarExcecaoSeUsuarioNaoForEncontrado() {

        DadosEditarUsuario dados = mock(DadosEditarUsuario.class);
        Mockito.when(dados.id()).thenReturn(-9);

        Throwable exception = catchThrowable(() -> usuarioService.editarUsuario(dados));

        assertThat(exception)
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Usuário com ID -9 não encontrado.");
    }

    @Test
    @DisplayName("Não deve lançar exceção se login já estiver em uso pelo próprio usuário")
    void naoDeveLancarExcecaoSeLoginPertencerAoMesmoUsuario() {
        DadosEditarUsuario dados = Mockito.mock(DadosEditarUsuario.class);
        Mockito.when(dados.id()).thenReturn(1);
        Mockito.when(dados.login()).thenReturn(usuario.getLogin());
        Mockito.when(dados.senha()).thenReturn("novasenha");

        Usuario spyUsuario = Mockito.spy(usuario);
        Mockito.when(spyUsuario.getId()).thenReturn(1);

        Mockito.when(usuarioRepository.findById(1)).thenReturn(Optional.of(spyUsuario));
        Mockito.when(usuarioRepository.findByLoginIgnoreCase(spyUsuario.getLogin())).thenReturn(Optional.of(spyUsuario));
        Mockito.when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoUsuario resultado = usuarioService.editarUsuario(dados);

        assertNotNull(resultado);
        assertEquals("novasenha", spyUsuario.getSenha());
        assertEquals(spyUsuario.getLogin(), resultado.login());
    }

    @Test
    @DisplayName("Deve desativar o usuário com sucesso")
    void deveDesativarUsuarioComSucesso() {
        // Arrange
        Usuario spyUsuario = Mockito.spy(usuario);
        Mockito.when(usuarioRepository.findById(1)).thenReturn(Optional.of(spyUsuario));

        // Act
        usuarioService.desativarUsuario(1);

        // Assert
        Mockito.verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario salvo = usuarioCaptor.getValue();
        assertFalse(salvo.getAtivo());
    }

    @Test
    @DisplayName("Deve lançar exceção se ID for nulo ao desativar usuário")
    void deveLancarExcecaoSeIdForNuloAoDesativarUsuario() {
        // Act + Assert
        Throwable ex = catchThrowable(() -> usuarioService.desativarUsuario(null));

        assertThat(ex)
                .isInstanceOf(UsuarioBadRequestException.class)
                .hasMessage("Id do usuário não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não for encontrado ao desativar")
    void deveLancarExcecaoSeUsuarioNaoForEncontradoAoDesativar() {
        // Arrange
        Mockito.when(usuarioRepository.findById(-10)).thenReturn(Optional.empty());

        // Act + Assert
        Throwable ex = catchThrowable(() -> usuarioService.desativarUsuario(-10));

        assertThat(ex)
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Usuário com ID -10 não encontrado.");
    }

}