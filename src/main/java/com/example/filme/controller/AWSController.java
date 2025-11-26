package com.example.filme.controller;

import com.example.filme.domain.avaliacao.AvaliacaoPublisher;
import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.domain.filme.FilmePublisher;
import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
import com.example.filme.domain.filme_pessoa.FilmePessoaPublisher;
import com.example.filme.domain.filme_pessoa.dtos.DadosAlterarFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.domain.genero.GeneroPublisher;
import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
import com.example.filme.domain.pessoa.PessoaPublisher;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.usuario.UsuarioPublisher;
import com.example.filme.domain.usuario.dtos.DadosCadastrarUsuario;
import com.example.filme.domain.usuario.dtos.DadosEditarUsuario;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("aws")
public class AWSController {

    private final FilmePublisher filmePublisher;
    private final PessoaPublisher pessoaPublisher;
    private final GeneroPublisher generoPublisher;
    private final AvaliacaoPublisher avaliacaoPublisher;
    private final FilmePessoaPublisher filmePessoaPublisher;
    private final UsuarioPublisher usuarioPublisher;

    @Autowired
    public AWSController(
            FilmePublisher filmePublisher,
            PessoaPublisher pessoaPublisher,
            GeneroPublisher generoPublisher,
            AvaliacaoPublisher avaliacaoPublisher,
            FilmePessoaPublisher filmePessoaPublisher,
            UsuarioPublisher usuarioPublisher
    ) {
        this.filmePublisher = filmePublisher;
        this.pessoaPublisher = pessoaPublisher;
        this.generoPublisher = generoPublisher;
        this.avaliacaoPublisher = avaliacaoPublisher;
        this.filmePessoaPublisher = filmePessoaPublisher;
        this.usuarioPublisher = usuarioPublisher;
    }

    // ---------------- FILME ----------------

    @PostMapping("/filme/cadastrar")
    @Transactional
    public ResponseEntity<String> enviarCadastrarFilme(@RequestBody @Valid DadosCadastrarFilme dados) {
        filmePublisher.publicarCadastrar(dados);
        return ResponseEntity.status(201).body("Solicitação de cadastro de filme enviada.");
    }

    @PutMapping("/filme/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarFilme(@RequestBody @Valid DadosAlterarFilme dados) {
        filmePublisher.publicarAlterar(dados);
        return ResponseEntity.ok("Solicitação de alteração de filme enviada.");
    }

    @PostMapping("/filme/atualizar-popularidade/{id}")
    public ResponseEntity<String> enviarAtualizarPopularidade(@PathVariable Integer id) {
        filmePublisher.publicarAtualizarPopularidade(id);
        return ResponseEntity.ok("Solicitação de atualização de popularidade enviada.");
    }

    @PostMapping("/filme/importar/{pagina}")
    public ResponseEntity<String> enviarImportarFilme(@PathVariable Integer pagina) {
        filmePublisher.publicarImportarDaApi(pagina);
        return ResponseEntity.status(201).body("Solicitação de importação de filmes enviada.");
    }

    // ---------------- PESSOA ----------------

    @PostMapping("/pessoa/cadastrar")
    @Transactional
    public ResponseEntity<String> enviarCadastrarPessoa(@RequestBody @Valid DadosCadastroPessoa dados) {
        pessoaPublisher.publicarAdicionar(dados);
        return ResponseEntity.status(201).body("Solicitação de cadastro de pessoa enviada.");
    }

    @PutMapping("/pessoa/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarPessoa(@RequestBody @Valid DadosAlterarPessoa dados) {
        pessoaPublisher.publicarEditar(dados);
        return ResponseEntity.ok("Solicitação de alteração de pessoa enviada.");
    }

    // ---------------- GÊNERO ----------------

    @PostMapping("/genero/adicionar")
    @Transactional
    public ResponseEntity<String> enviarAdicionarGenero(@RequestBody @Valid DadosCadastrarGenero dados) {
        generoPublisher.publicarAdicionar(dados);
        return ResponseEntity.status(201).body("Solicitação de cadastro de gênero enviada.");
    }

    @PutMapping("/genero/editar")
    @Transactional
    public ResponseEntity<String> enviarEditarGenero(@RequestBody @Valid DadosEditarGenero dados) {
        generoPublisher.publicarEditar(dados);
        return ResponseEntity.ok("Solicitação de alteração de gênero enviada.");
    }

    @PostMapping("/genero/importar")
    public ResponseEntity<String> enviarImportarGeneros() {
        generoPublisher.publicarImportarDaApi();
        return ResponseEntity.status(201).body("Solicitação de importação de gêneros enviada.");
    }

    // ---------------- AVALIAÇÃO ----------------

    @PostMapping("/avaliacao/cadastrar")
    @Transactional
    public ResponseEntity<String> enviarCadastrarAvaliacao(@RequestBody @Valid DadosCadastroAvaliacao dados) {
        avaliacaoPublisher.publicarCadastrar(dados);
        return ResponseEntity.status(201).body("Solicitação de cadastro de avaliação enviada.");
    }

    @PutMapping("/avaliacao/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarAvaliacao(@RequestBody @Valid DadosAlterarAvaliacao dados) {
        avaliacaoPublisher.publicarAlterar(dados);
        return ResponseEntity.ok("Solicitação de alteração de avaliação enviada.");
    }

    @DeleteMapping("/avaliacao/deletar/{id}")
    public ResponseEntity<String> enviarDeletarAvaliacao(@PathVariable Integer id) {
        avaliacaoPublisher.publicarDeletar(id);
        return ResponseEntity.ok("Solicitação de exclusão de avaliação enviada.");
    }

    @GetMapping("/avaliacao/listar-usuario/{idUsuario}")
    public ResponseEntity<String> enviarListarAvaliacoesUsuario(@PathVariable Integer idUsuario) {
        avaliacaoPublisher.publicarListarUsuario(idUsuario);
        return ResponseEntity.ok("Solicitação de listagem de avaliações do usuário enviada.");
    }

    @GetMapping("/avaliacao/listar-filme/{idFilme}")
    public ResponseEntity<String> enviarListarAvaliacoesFilme(@PathVariable Integer idFilme) {
        avaliacaoPublisher.publicarListarFilme(idFilme);
        return ResponseEntity.ok("Solicitação de listagem de avaliações do filme enviada.");
    }

    @GetMapping("/avaliacao/listar-unica")
    public ResponseEntity<String> enviarListarAvaliacaoUnica(@RequestParam Integer idFilme,
                                                             @RequestParam Integer idUsuario) {
        avaliacaoPublisher.publicarListarUnica(idFilme, idUsuario);
        return ResponseEntity.ok("Solicitação de listagem de avaliação específica enviada.");
    }

    // ---------------- FILME_PESSOA ----------------

    @PostMapping("/filme-pessoa/adicionar")
    @Transactional
    public ResponseEntity<String> enviarAdicionarFilmePessoa(@RequestBody @Valid DadosCadastroFilmePessoa dados) {
        filmePessoaPublisher.publicarAdicionar(dados);
        return ResponseEntity.status(201).body("Solicitação de vínculo filme-pessoa enviada.");
    }

    @PutMapping("/filme-pessoa/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarFilmePessoa(@RequestBody @Valid DadosAlterarFilmePessoa dados) {
        filmePessoaPublisher.publicarAlterar(dados);
        return ResponseEntity.ok("Solicitação de alteração de vínculo filme-pessoa enviada.");
    }

    @DeleteMapping("/filme-pessoa/deletar")
    public ResponseEntity<String> enviarDeletarFilmePessoa(@RequestParam Integer idFilme,
                                                           @RequestParam Integer idPessoa) {
        filmePessoaPublisher.publicarDeletar(idFilme, idPessoa);
        return ResponseEntity.ok("Solicitação de exclusão de vínculo filme-pessoa enviada.");
    }

    // ---------------- USUÁRIO ----------------

    @PostMapping("/usuario/cadastrar")
    @Transactional
    public ResponseEntity<String> enviarCadastrarUsuario(@RequestBody @Valid DadosCadastrarUsuario dados) {
        usuarioPublisher.publicarCadastrar(dados);
        return ResponseEntity.status(201).body("Solicitação de cadastro de usuário enviada.");
    }

    @PutMapping("/usuario/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarUsuario(@RequestBody @Valid DadosEditarUsuario dados) {
        usuarioPublisher.publicarAlterar(dados);
        return ResponseEntity.ok("Solicitação de alteração de usuário enviada.");
    }

    @PostMapping("/usuario/desativar/{id}")
    public ResponseEntity<String> enviarDesativarUsuario(@PathVariable Integer id) {
        usuarioPublisher.publicarDesativar(id);
        return ResponseEntity.ok("Solicitação de desativação de usuário enviada.");
    }

    @GetMapping("/usuario/listar")
    public ResponseEntity<String> enviarListarUsuarios() {
        usuarioPublisher.publicarListar();
        return ResponseEntity.ok("Solicitação de listagem de usuários enviada.");
    }
}