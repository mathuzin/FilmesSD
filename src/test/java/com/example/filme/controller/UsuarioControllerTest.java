package com.example.filme.controller;

import com.example.filme.domain.usuario.Perfil;
import com.example.filme.domain.usuario.UsuarioService;
import com.example.filme.domain.usuario.dtos.DadosCadastrarUsuario;
import com.example.filme.domain.usuario.dtos.DadosDetalhamentoUsuario;
import com.example.filme.domain.usuario.dtos.DadosEditarUsuario;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private DadosCadastrarUsuario dadosCadastrarUsuario;
    private DadosDetalhamentoUsuario dadosDetalhamentoUsuario;

    private DadosEditarUsuario dadosEditarUsuario;
    private DadosDetalhamentoUsuario dadosDetalhamentoUsuarioEditado;

    private List<DadosDetalhamentoUsuario> listaUsuarios;
    private Page<DadosDetalhamentoUsuario> paginaUsuarios;

    @BeforeEach
    void setUp() {
        dadosCadastrarUsuario = new DadosCadastrarUsuario(
                1, // pessoa_id
                "usuario@example.com",
                "senha123",
                Perfil.ADM
        );

        dadosDetalhamentoUsuario = new DadosDetalhamentoUsuario(
                "usuario@example.com",
                "João Silva"
        );

        dadosEditarUsuario = new DadosEditarUsuario(
                1,
                "usuario_editado@example.com",
                "novaSenha456"
        );

        dadosDetalhamentoUsuarioEditado = new DadosDetalhamentoUsuario(
                "usuario_editado@example.com",
                "João Silva"
        );

        listaUsuarios = List.of(
                new DadosDetalhamentoUsuario("usuario1@example.com", "João Silva"),
                new DadosDetalhamentoUsuario("usuario2@example.com", "Maria Costa")
        );

        paginaUsuarios = new PageImpl<>(listaUsuarios);
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveriaListarTodosOsUsuarios() {

        Mockito.when(usuarioService.listarTodosUsuarios(Mockito.any(Pageable.class)))
                .thenReturn(paginaUsuarios);

        ResponseEntity<Page<DadosDetalhamentoUsuario>> response = usuarioController.listarTodosUsuarios(Pageable.unpaged());

        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertEquals(listaUsuarios.size(), response.getBody().getContent().size());
        assertEquals(listaUsuarios.get(0).login(), response.getBody().getContent().get(0).login());

        Mockito.verify(usuarioService, Mockito.times(1)).listarTodosUsuarios(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar todos os usuários ativos")
    void deveriaListarTodosOsUsuariosAtivos() {

        Mockito.when(usuarioService.listarTodosUsuariosAtivos(Mockito.any(Pageable.class)))
                .thenReturn(paginaUsuarios);

        ResponseEntity<Page<DadosDetalhamentoUsuario>> response = usuarioController.listarTodosUsuariosAtivos(Pageable.unpaged());

        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertEquals(listaUsuarios.size(), response.getBody().getContent().size());
        assertEquals(listaUsuarios.get(0).login(), response.getBody().getContent().get(0).login());

        Mockito.verify(usuarioService, Mockito.times(1)).listarTodosUsuariosAtivos(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar usuário pelo ID")
    void deveriaBuscarUsuarioPorId() {
        Integer usuarioId = 1;

        Mockito.when(usuarioService.buscarUsuario(usuarioId)).thenReturn(dadosDetalhamentoUsuario);

        ResponseEntity<DadosDetalhamentoUsuario> response = usuarioController.buscarUsuario(usuarioId);

        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertEquals(dadosDetalhamentoUsuario.login(), response.getBody().login());
        assertEquals(dadosDetalhamentoUsuario.nome(), response.getBody().nome());

        Mockito.verify(usuarioService, Mockito.times(1)).buscarUsuario(usuarioId);
    }

    @Test
    @DisplayName("Deve editar usuário com sucesso")
    void deveriaEditarUsuarioComSucesso() {

        Mockito.when(usuarioService.editarUsuario(dadosEditarUsuario)).thenReturn(dadosDetalhamentoUsuarioEditado);

        ResponseEntity<DadosDetalhamentoUsuario> response = usuarioController.editarUsuario(dadosEditarUsuario);

        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertEquals(dadosDetalhamentoUsuarioEditado.login(), response.getBody().login());
        assertEquals(dadosDetalhamentoUsuarioEditado.nome(), response.getBody().nome());

        Mockito.verify(usuarioService, Mockito.times(1)).editarUsuario(dadosEditarUsuario);
    }

    @Test
    @DisplayName("Deve desativar usuário com sucesso")
    void deveriaDesativarUsuarioComSucesso() {
        Integer usuarioId = 1;

        Mockito.doNothing().when(usuarioService).desativarUsuario(usuarioId);

        var response = usuarioController.desativarUsuario(usuarioId);

        assertEquals(204, response.getStatusCodeValue());

        Mockito.verify(usuarioService, Mockito.times(1)).desativarUsuario(usuarioId);
    }


}