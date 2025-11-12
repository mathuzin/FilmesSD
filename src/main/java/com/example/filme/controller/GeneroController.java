package com.example.filme.controller;

import com.example.filme.domain.genero.GeneroService;
import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosDetalhamentoGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("genero")
public class GeneroController {

    @Autowired
    private GeneroService generoService;

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<DadosDetalhamentoGenero> cadastrarGenero(@Valid @RequestBody DadosCadastrarGenero dados) {
        var dto = generoService.adicionarGenero(dados);
        return ResponseEntity.status(201).body(dto);
    }

    @Transactional
    @PostMapping("/add/api")
    public ResponseEntity<List<DadosDetalhamentoGenero>> cadastrarGenerosDaApi() {
        var generosAdicionados = generoService.importarGenerosDaApi();

        if (generosAdicionados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(201).body(generosAdicionados);
    }

    @GetMapping
    public ResponseEntity<List<DadosDetalhamentoGenero>> listarGeneros() {
        var lista = generoService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @Transactional
    @PutMapping
    public ResponseEntity<DadosDetalhamentoGenero> editarGenero(@Valid @RequestBody DadosEditarGenero dados) {
        var dto = generoService.editarGenero(dados);
        return ResponseEntity.ok(dto);
    }
}
