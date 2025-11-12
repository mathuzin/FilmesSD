package com.example.filme.controller;

import com.example.filme.domain.pessoa.Pessoa;
import com.example.filme.domain.pessoa.PessoaService;
import com.example.filme.domain.pessoa.Tipo;
import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.pessoa.dtos.DadosDetalhamentoPessoa;
import com.example.filme.domain.usuario.Usuario;
import com.example.filme.domain.usuario.UsuarioRepository;
import com.example.filme.domain.usuario.dtos.AthenticationDTO;
import com.example.filme.domain.usuario.dtos.LoginResponseDTO;
import com.example.filme.domain.usuario.dtos.RegisterDTO;
import com.example.filme.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        if (usuarioRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        Tipo tipoPessoa = switch (data.perfil()) {
            default -> Tipo.USUARIO;
        };

        DadosCadastroPessoa dadosCadastroPessoa = new DadosCadastroPessoa(
                data.nome(),
                data.dataNascimento(),
                data.origem(),
                tipoPessoa
        );

        Pessoa novaPessoa = pessoaService.cadastrarPessoa(dadosCadastroPessoa);

        Usuario novoUsuario = new Usuario(data.login(), encryptedPassword, data.perfil());
        novoUsuario.setPessoa(novaPessoa);

        usuarioRepository.save(novoUsuario);

        return ResponseEntity.status(201).build();
    }

}