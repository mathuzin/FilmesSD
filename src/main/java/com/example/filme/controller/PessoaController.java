package com.example.filme.controller;

import com.example.filme.domain.pessoa.PessoaService;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.pessoa.dtos.DadosDetalhamentoPessoa;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequestMapping("pessoa")
@RestController
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping("/all")
    public ResponseEntity<Page<DadosDetalhamentoPessoa>> listarTodasAsPessoas(Pageable paginacao) {
        var dto = pessoaService.listarTodasAsPessoas(paginacao);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoPessoa> listarPessoa(@PathVariable Integer id) {
        var dto = pessoaService.listarPessoa(id);

        return ResponseEntity.ok(dto);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoPessoa> alterarPessoa(@Valid @RequestBody DadosAlterarPessoa dados) {
        var dto = pessoaService.alterarPessoa(dados);

        return ResponseEntity.ok(dto);
    }
}
