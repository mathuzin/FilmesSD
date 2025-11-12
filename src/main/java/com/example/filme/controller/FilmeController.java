package com.example.filme.controller;

import com.example.filme.domain.filme.FilmeService;
import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
import com.example.filme.domain.filme.dtos.DadosDetalhamentoFilme;
import com.example.filme.domain.genero.Genero;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("filme")
public class FilmeController {

    @Autowired
    private FilmeService filmeService;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoFilme> adicionarFilme(@Valid DadosCadastrarFilme dados) {

        var dto = filmeService.cadastrarFilme(dados);

        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/filmes")
    public ResponseEntity<List<DadosDetalhamentoFilme>> listarTodosOsFilmes() {
        var filmes = filmeService.listarTodosFilmes();
        return ResponseEntity.ok(filmes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoFilme> listarFilme(@PathVariable Integer id) {
        var dto = filmeService.listarFilme(id);

        return ResponseEntity.ok(dto);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoFilme> alterarFilme(@Valid @RequestBody DadosAlterarFilme dados) {
        var dto = filmeService.alterarFilme(dados);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/recomendarFilmes/{id}")
    public ResponseEntity<List<DadosDetalhamentoFilme>> filmesRecomendados(@PathVariable Integer id) {
        var dto = filmeService.filmesRecomendadosParaUsuario(id);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/inserirDaAPI/{pagina}")
    public ResponseEntity<List<DadosDetalhamentoFilme>> inserirFilmesDaApiNoBD(@PathVariable Integer pagina) {
        var filmesAdicionados = filmeService.importarFilmesDaApi(pagina);

        if (filmesAdicionados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(201).body(filmesAdicionados);
    }

}