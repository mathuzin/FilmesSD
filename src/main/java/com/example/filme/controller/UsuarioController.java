package com.example.filme.controller;

import com.example.filme.domain.usuario.UsuarioService;
import com.example.filme.domain.usuario.dtos.DadosDetalhamentoUsuario;
import com.example.filme.domain.usuario.dtos.DadosEditarUsuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/all")
    public ResponseEntity<Page<DadosDetalhamentoUsuario>> listarTodosUsuarios(Pageable paginacao) {
        var dto = usuarioService.listarTodosUsuarios(paginacao);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all/ativos")
    public ResponseEntity<Page<DadosDetalhamentoUsuario>> listarTodosUsuariosAtivos(Pageable paginacao) {
        var dto = usuarioService.listarTodosUsuariosAtivos(paginacao);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoUsuario> buscarUsuario(@PathVariable Integer id) {
        var dto = usuarioService.buscarUsuario(id);

        return ResponseEntity.ok(dto);
    }

    @Transactional
    @PutMapping
    public ResponseEntity<DadosDetalhamentoUsuario> editarUsuario(@RequestBody @Valid DadosEditarUsuario dados) {
        var dto = usuarioService.editarUsuario(dados);

        return ResponseEntity.ok(dto);
    }

    @Transactional
    @PostMapping("/desativar/{id}")
    public ResponseEntity<Void> desativarUsuario(@PathVariable Integer id) {
        usuarioService.desativarUsuario(id);

        return ResponseEntity.status(204).build();
    }

}
