package com.example.filme.controller;

import com.example.filme.domain.filme_pessoa.FilmePessoaService;
import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosDetalhamentoFilmePessoa;
import com.example.filme.domain.pessoa.dtos.DadosDetalhamentoPessoa;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("filmePessoa")
public class FilmePessoaController {

    @Autowired
    private FilmePessoaService filmePessoaService;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoFilmePessoa> cadastrarPessoaAFilme(@Valid @RequestBody DadosCadastroFilmePessoa dados) {
        var dto = filmePessoaService.adicionarPessoaAoFilme(dados);

        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/atores/{idFilme}")
    public ResponseEntity<Page<DadosDetalhamentoPessoa>> listarAtoresEmUmFilme(@PathVariable Integer idFilme, Pageable paginacao) {
        var dto = filmePessoaService.buscarAtoresEmUmFilme(idFilme, paginacao);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/diretores/{idFilme}")
    public ResponseEntity<Page<DadosDetalhamentoPessoa>> listarDiretoresEmUmFilme(@PathVariable Integer idFilme, Pageable paginacao) {
        var dto = filmePessoaService.buscarDiretoresEmUmFilme(idFilme, paginacao);

        return ResponseEntity.ok(dto);
    }

}