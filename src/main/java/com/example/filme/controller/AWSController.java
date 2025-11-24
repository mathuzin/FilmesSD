package com.example.filme.controller;

import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
import com.example.filme.domain.filme_pessoa.dtos.DadosAlterarFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.usuario.dtos.DadosCadastrarUsuario;
import com.example.filme.domain.usuario.dtos.DadosEditarUsuario;
import com.example.filme.infra.aws.MensagemSqsService;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.example.filme.infra.aws.enums.AcaoMensagem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("aws")
public class AWSController {

    private final MensagemSqsService mensagemSqsService;
    private final ObjectMapper mapper;

    @Autowired
    public AWSController(MensagemSqsService mensagemSqsService, ObjectMapper mapper) {
        this.mensagemSqsService = mensagemSqsService;
        this.mapper = mapper;
    }

    // ---------------- FILME ----------------

    @PostMapping("/filme/cadastrar")
    @Transactional
    public ResponseEntity<String> enviarCadastrarFilme(@RequestBody @Valid DadosCadastrarFilme dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("FILME", AcaoMensagem.CADASTRAR, node, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.status(201).body("Mensagem CADASTRAR FILME enviada ao SQS");
    }

    @PutMapping("/filme/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarFilme(@RequestBody @Valid DadosAlterarFilme dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("FILME", AcaoMensagem.ALTERAR, node, dados.id(), null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem ALTERAR FILME enviada ao SQS");
    }

    @PostMapping("/filme/atualizar-popularidade/{id}")
    public ResponseEntity<String> enviarAtualizarPopularidade(@PathVariable Integer id) {
        var dto = new AcaoMensagemDTO("FILME", AcaoMensagem.ATUALIZAR_POPULARIDADE, null, id, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem ATUALIZAR_POPULARIDADE enviada ao SQS");
    }

    @PostMapping("/filme/importar/{pagina}")
    public ResponseEntity<String> enviarImportarFilme(@PathVariable Integer pagina) {
        JsonNode node = mapper.valueToTree(pagina);
        var dto = new AcaoMensagemDTO("FILME", AcaoMensagem.IMPORTAR_DA_API, node, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.status(201).body("Mensagem IMPORTAR_DA_API enviada ao SQS — página: " + pagina);
    }

    // ---------------- PESSOA ----------------

    @PostMapping("/pessoa/cadastrar")
    @Transactional
    public ResponseEntity<String> enviarCadastrarPessoa(@RequestBody @Valid DadosCadastroPessoa dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("PESSOA", AcaoMensagem.CADASTRAR, node, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.status(201).body("Mensagem CADASTRAR PESSOA enviada ao SQS");
    }

    @PutMapping("/pessoa/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarPessoa(@RequestBody @Valid DadosAlterarPessoa dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("PESSOA", AcaoMensagem.EDITAR, node, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem EDITAR PESSOA enviada ao SQS");
    }

    // ---------------- GÊNERO ----------------

    @PostMapping("/genero/adicionar")
    @Transactional
    public ResponseEntity<String> enviarAdicionarGenero(@RequestBody @Valid DadosCadastrarGenero dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("GENERO", AcaoMensagem.CADASTRAR, node, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.status(201).body("Mensagem CADASTRAR GÊNERO enviada ao SQS");
    }

    @PutMapping("/genero/editar")
    @Transactional
    public ResponseEntity<String> enviarEditarGenero(@RequestBody @Valid DadosEditarGenero dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("GENERO", AcaoMensagem.EDITAR, node, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem EDITAR GÊNERO enviada ao SQS");
    }

    @PostMapping("/genero/importar")
    public ResponseEntity<String> enviarImportarGeneros() {
        var dto = new AcaoMensagemDTO("GENERO", AcaoMensagem.IMPORTAR_DA_API, null, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.status(201).body("Mensagem IMPORTAR GÊNEROS enviada ao SQS");
    }

    // ---------------- AVALIAÇÃO ----------------

    @PostMapping("/avaliacao/cadastrar")
    @Transactional
    public ResponseEntity<String> enviarCadastrarAvaliacao(@RequestBody @Valid DadosCadastroAvaliacao dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("AVALIACAO", AcaoMensagem.CADASTRAR, node, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.status(201).body("Mensagem CADASTRAR AVALIAÇÃO enviada ao SQS");
    }

    @PutMapping("/avaliacao/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarAvaliacao(@RequestBody @Valid DadosAlterarAvaliacao dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("AVALIACAO", AcaoMensagem.ALTERAR, node, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem ALTERAR AVALIAÇÃO enviada ao SQS");
    }

    @DeleteMapping("/avaliacao/deletar/{id}")
    public ResponseEntity<String> enviarDeletarAvaliacao(@PathVariable Integer id) {
        var dto = new AcaoMensagemDTO("AVALIACAO", AcaoMensagem.DELETAR, null, id, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem DELETAR AVALIAÇÃO enviada ao SQS");
    }

    @GetMapping("/avaliacao/listar-usuario/{idUsuario}")
    public ResponseEntity<String> enviarListarAvaliacoesUsuario(@PathVariable Integer idUsuario) {
        var dto = new AcaoMensagemDTO("AVALIACAO", AcaoMensagem.LISTAR_USUARIO, null, null, idUsuario, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem LISTAR_USUARIO enviada ao SQS");
    }

    @GetMapping("/avaliacao/listar-filme/{idFilme}")
    public ResponseEntity<String> enviarListarAvaliacoesFilme(@PathVariable Integer idFilme) {
        var dto = new AcaoMensagemDTO("AVALIACAO", AcaoMensagem.LISTAR_FILME, null, idFilme, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem LISTAR_FILME enviada ao SQS");
    }

    @GetMapping("/avaliacao/listar-unica")
    public ResponseEntity<String> enviarListarAvaliacaoUnica(@RequestParam Integer idFilme,
                                                             @RequestParam Integer idUsuario) {
        var dto = new AcaoMensagemDTO("AVALIACAO", AcaoMensagem.LISTAR_UNICA, null, idFilme, idUsuario, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem LISTAR_UNICA enviada ao SQS");
    }

    // ---------------- FILME_PESSOA ----------------

    @PostMapping("/filme-pessoa/adicionar")
    @Transactional
    public ResponseEntity<String> enviarAdicionarFilmePessoa(@RequestBody @Valid DadosCadastroFilmePessoa dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("FILME_PESSOA", AcaoMensagem.ADICIONAR, node, dados.id_filme(), null, dados.id_pessoa());
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.status(201).body("Mensagem ADICIONAR FILME_PESSOA enviada ao SQS");
    }

    @PutMapping("/filme-pessoa/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarFilmePessoa(@RequestBody @Valid DadosAlterarFilmePessoa dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("FILME_PESSOA", AcaoMensagem.ALTERAR, node, dados.id().getFilmeId(), null, dados.id().getPessoaId());
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem ALTERAR FILME_PESSOA enviada ao SQS");
    }

    @DeleteMapping("/filme-pessoa/deletar")
    public ResponseEntity<String> enviarDeletarFilmePessoa(@RequestParam Integer idFilme,
                                                           @RequestParam Integer idPessoa) {
        var dto = new AcaoMensagemDTO("FILME_PESSOA", AcaoMensagem.DELETAR, null, idFilme, null, idPessoa);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem DELETAR FILME_PESSOA enviada ao SQS");
    }

    // ---------------- USUÁRIO ----------------

    @PostMapping("/usuario/cadastrar")
    @Transactional
    public ResponseEntity<String> enviarCadastrarUsuario(@RequestBody @Valid DadosCadastrarUsuario dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("USUARIO", AcaoMensagem.CADASTRAR, node, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.status(201).body("Mensagem CADASTRAR USUÁRIO enviada ao SQS");
    }

    @PutMapping("/usuario/alterar")
    @Transactional
    public ResponseEntity<String> enviarAlterarUsuario(@RequestBody @Valid DadosEditarUsuario dados) {
        JsonNode node = mapper.valueToTree(dados);
        var dto = new AcaoMensagemDTO("USUARIO", AcaoMensagem.EDITAR, node, null, dados.id(), null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem EDITAR USUÁRIO enviada ao SQS");
    }

    @PostMapping("/usuario/desativar/{id}")
    public ResponseEntity<String> enviarDesativarUsuario(@PathVariable Integer id) {
        var dto = new AcaoMensagemDTO("USUARIO", AcaoMensagem.DELETAR, null, null, id, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem DESATIVAR USUÁRIO enviada ao SQS");
    }

    @GetMapping("/usuario/listar")
    public ResponseEntity<String> enviarListarUsuarios() {
        var dto = new AcaoMensagemDTO("USUARIO", AcaoMensagem.LISTAR_USUARIO, null, null, null, null);
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.ok("Mensagem LISTAR_USUARIO enviada ao SQS");
    }

    // ---------------- UTILS (texto/JSON genérico) ----------------

    // Envia texto puro para a fila
    @PostMapping("/enviar")
    @Transactional
    public ResponseEntity<String> enviarMensagemSimples(@RequestBody @Valid String mensagem) {
        mensagemSqsService.enviarMensagem(mensagem);
        return ResponseEntity.status(201).body("Mensagem enviada para o SQS com sucesso!");
    }

    // Envia um objeto JSON qualquer (p.ex.: para testes)
    @PostMapping("/enviar-json")
    @Transactional
    public ResponseEntity<String> enviarMensagemJson(@RequestBody Object dto) {
        mensagemSqsService.enviarMensagem(dto);
        return ResponseEntity.status(201).body("JSON enviado para o SQS com sucesso!");
    }
}