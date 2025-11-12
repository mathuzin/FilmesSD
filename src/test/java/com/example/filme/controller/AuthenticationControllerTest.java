package com.example.filme.controller;

import com.example.filme.domain.pessoa.Pessoa;
import com.example.filme.domain.pessoa.PessoaService;
import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.usuario.Perfil;
import com.example.filme.domain.usuario.Usuario;
import com.example.filme.domain.usuario.UsuarioRepository;
import com.example.filme.domain.usuario.dtos.AthenticationDTO;
import com.example.filme.domain.usuario.dtos.RegisterDTO;
import com.example.filme.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private PessoaService pessoaService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void deveFazerLoginComSucesso() {
        AthenticationDTO dados = new AthenticationDTO("usuario@email.com", "senha123");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());

        Usuario usuarioMock = Mockito.mock(Usuario.class);

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(usuarioMock, null, List.of()));

        Mockito.when(tokenService.generateToken(usuarioMock)).thenReturn("tokenGerado");

        var response = authenticationController.login(dados);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("tokenGerado", response.getBody().token());
    }

    @Test
    void deveRegistrarUsuarioComPerfilComum() {
        RegisterDTO dados = new RegisterDTO(
                "comum@email.com",
                "senha123",
                Perfil.COMUM,
                "Fulano Comum",
                LocalDate.of(1990, 1, 1),
                "BRA"
        );

        Mockito.when(usuarioRepository.findByLogin(dados.login())).thenReturn(null);

        Pessoa pessoaCadastrada = new Pessoa();
        pessoaCadastrada.setNome(dados.nome());
        Mockito.when(pessoaService.cadastrarPessoa(Mockito.any(DadosCadastroPessoa.class))).thenReturn(pessoaCadastrada);

        Mockito.when(usuarioRepository.save(usuarioCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Void> response = authenticationController.register(dados);

        assertEquals(201, response.getStatusCodeValue());

        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertEquals(dados.login(), usuarioSalvo.getLogin());
        assertEquals(Perfil.COMUM, usuarioSalvo.getPerfil());
        assertEquals(pessoaCadastrada, usuarioSalvo.getPessoa());
        assertNotNull(usuarioSalvo.getSenha());
    }

    @Test
    void deveRegistrarUsuarioComPerfilADM() {
        RegisterDTO dados = new RegisterDTO(
                "adm@email.com",
                "senha123",
                Perfil.ADM,
                "Fulano ADM",
                LocalDate.of(1990, 1, 1),
                "BRA"
        );

        Mockito.when(usuarioRepository.findByLogin(dados.login())).thenReturn(null);

        Pessoa pessoaCadastrada = new Pessoa();
        pessoaCadastrada.setNome(dados.nome());
        Mockito.when(pessoaService.cadastrarPessoa(Mockito.any(DadosCadastroPessoa.class))).thenReturn(pessoaCadastrada);

        Mockito.when(usuarioRepository.save(usuarioCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Void> response = authenticationController.register(dados);

        assertEquals(201, response.getStatusCodeValue());

        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertEquals(dados.login(), usuarioSalvo.getLogin());
        assertEquals(Perfil.ADM, usuarioSalvo.getPerfil());
        assertEquals(pessoaCadastrada, usuarioSalvo.getPessoa());
        assertNotNull(usuarioSalvo.getSenha());
    }

    @Test
    void deveRetornarBadRequestSeLoginExistir() {
        RegisterDTO dados = new RegisterDTO(
                "existente@email.com",
                "senha123",
                Perfil.COMUM,
                "Fulano Existente",
                LocalDate.of(1990, 1, 1),
                "BRA"
        );

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setLogin(dados.login());

        Mockito.when(usuarioRepository.findByLogin(dados.login())).thenReturn(usuarioExistente);

        ResponseEntity<Void> response = authenticationController.register(dados);

        assertEquals(400, response.getStatusCodeValue());

        // Verifica que não salvou usuário
        Mockito.verify(usuarioRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(pessoaService, Mockito.never()).cadastrarPessoa(Mockito.any());
    }
}