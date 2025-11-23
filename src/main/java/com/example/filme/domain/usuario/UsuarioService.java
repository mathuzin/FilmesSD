package com.example.filme.domain.usuario;

import com.example.filme.domain.pessoa.PessoaRepository;
import com.example.filme.domain.usuario.dtos.DadosDetalhamentoUsuario;
import com.example.filme.domain.usuario.dtos.DadosEditarUsuario;
import com.example.filme.domain.usuario.exceptions.UsuarioBadRequestException;
import com.example.filme.domain.usuario.exceptions.UsuarioDuplicadoException;
import com.example.filme.domain.usuario.exceptions.UsuarioNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Page<DadosDetalhamentoUsuario> listarTodosUsuarios(Pageable paginacao) {
        return usuarioRepository.findAll(paginacao)
                .map(DadosDetalhamentoUsuario::new);
    }

    public Page<DadosDetalhamentoUsuario> listarTodosUsuariosAtivos(Pageable paginacao) {
        return usuarioRepository.findByAtivoTrue(paginacao)
                .map(DadosDetalhamentoUsuario::new);
    }

    public DadosDetalhamentoUsuario buscarUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            throw new UsuarioBadRequestException("Id do usuário não pode ser nulo.");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário com ID " + idUsuario + " não encontrado."));

        return new DadosDetalhamentoUsuario(usuario);
    }

    @Transactional
    public DadosDetalhamentoUsuario editarUsuario(DadosEditarUsuario dados) {

        if (dados == null) {
            throw new UsuarioBadRequestException("Dados para editar usuário não devem ser nulos");
        }

        Usuario usuario = usuarioRepository.findById(dados.id())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário com ID " + dados.id() + " não encontrado."));

        if (dados.login() != null) {
            Optional<Usuario> usuarioComMesmoLogin = usuarioRepository.findByLoginIgnoreCase(dados.login());

            if (usuarioComMesmoLogin.isPresent() && !usuarioComMesmoLogin.get().getId().equals(dados.id())) {
                throw new UsuarioDuplicadoException("Login já está em uso por outro registro.");
            }

            usuario.setLogin(dados.login());
        }

        if (dados.senha() != null) {
            usuario.setSenha(dados.senha());
        }

        usuarioRepository.save(usuario);

        return new DadosDetalhamentoUsuario(usuario);

    }

    @Transactional
    public void desativarUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            throw new UsuarioBadRequestException("Id do usuário não pode ser nulo.");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new com.example.filme.domain.usuario.exceptions.UsuarioNotFoundException("Usuário com ID " + idUsuario + " não encontrado."));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
}
