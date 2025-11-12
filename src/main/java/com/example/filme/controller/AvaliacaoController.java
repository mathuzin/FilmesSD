package com.example.filme.controller;

import com.example.filme.domain.avaliacao.AvaliacaoService;
import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosDetalhamentoAvaliacao;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoAvaliacao> avaliar(@RequestBody @Valid DadosCadastroAvaliacao dados) {

        var dto = avaliacaoService.avaliar(dados);

        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<DadosDetalhamentoAvaliacao>> listarAvaliacoesDeUmUsuario(@PathVariable Integer id) {
        var dto = avaliacaoService.listarAvaliacoesDeUmUsuario(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/filme/{id}")
    public ResponseEntity<List<DadosDetalhamentoAvaliacao>> listarAvaliacoesDeUmFilme(@PathVariable Integer id) {
        var dto = avaliacaoService.listarAvaliacoesFilme(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{idFilme}/{idUsuario}")
    public ResponseEntity<DadosDetalhamentoAvaliacao> listarAvaliacaoDoUsuarioAoFilme(
            @PathVariable Integer idFilme,
            @PathVariable Integer idUsuario) {

        var dto = avaliacaoService.listarAvaliacaoDeUmFilme(idFilme, idUsuario);
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoAvaliacao> alterarAvaliacao(@Valid @RequestBody DadosAlterarAvaliacao dados) {
        var dto = avaliacaoService.atualizarAvaliacao(dados);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable Integer id) {
        avaliacaoService.deletarAvaliacao(id);
        return ResponseEntity.noContent().build();
    }


}