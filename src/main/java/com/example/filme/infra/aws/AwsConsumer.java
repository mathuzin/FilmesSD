package com.example.filme.infra.aws;

import com.example.filme.domain.avaliacao.AvaliacaoService;
import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.domain.filme.FilmeService;
import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
import com.example.filme.domain.filme_pessoa.FilmePessoaService;
import com.example.filme.domain.filme_pessoa.dtos.DadosAlterarFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.domain.genero.GeneroService;
import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
import com.example.filme.domain.pessoa.PessoaService;
import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import com.example.filme.domain.pessoa.dtos.DadosAlterarPessoa;
import com.example.filme.domain.usuario.UsuarioService;

import com.example.filme.domain.usuario.dtos.DadosEditarUsuario;
import com.example.filme.infra.aws.dtos.AcaoMensagemDTO;
import com.example.filme.infra.aws.dtos.SnsEnvelope;
import com.example.filme.infra.aws.enums.AcaoMensagem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class AwsConsumer {

    private final ObjectMapper objectMapper;
    private final FilmeService filmeService;
    private final AvaliacaoService avaliacaoService;
    private final PessoaService pessoaService;
    private final GeneroService generoService;
    private final FilmePessoaService filmePessoaService;
    private final UsuarioService usuarioService;

    public AwsConsumer(ObjectMapper objectMapper,
                       FilmeService filmeService,
                       AvaliacaoService avaliacaoService,
                       PessoaService pessoaService,
                       GeneroService generoService,
                       FilmePessoaService filmePessoaService,
                       UsuarioService usuarioService) {
        this.objectMapper = objectMapper;
        this.filmeService = filmeService;
        this.avaliacaoService = avaliacaoService;
        this.pessoaService = pessoaService;
        this.generoService = generoService;
        this.filmePessoaService = filmePessoaService;
        this.usuarioService = usuarioService;
    }

    @SqsListener("${aws.sqs.queue-url}")
    public void receberMensagem(String mensagemJson) {
        try {
            SnsEnvelope envelope = objectMapper.readValue(mensagemJson, SnsEnvelope.class);

            String mensagemReal = envelope.Message();

            AcaoMensagemDTO dto = objectMapper.readValue(mensagemReal, AcaoMensagemDTO.class);

            String entidade = dto.entidade();
            AcaoMensagem acao = dto.acao();
            JsonNode dados = dto.dados();

            switch (entidade) {
                case "FILME" -> processarFilme(acao, dados, dto);
                case "AVALIACAO" -> processarAvaliacao(acao, dados, dto);
                case "PESSOA" -> processarPessoa(acao, dados, dto);
                case "GENERO" -> processarGenero(acao, dados, dto);
                case "FILME_PESSOA" -> processarFilmePessoa(acao, dados, dto);
                case "USUARIO" -> processarUsuario(acao, dados, dto);
                default -> System.err.println("Entidade desconhecida na mensagem SQS: " + entidade);
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem SQS");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // ---------- FILME ----------
    private void processarFilme(AcaoMensagem acao, JsonNode dados, AcaoMensagemDTO dto) {
        switch (acao) {
            case CADASTRAR -> {
                var cadastro = objectMapper.convertValue(dados, DadosCadastrarFilme.class);
                filmeService.cadastrarFilme(cadastro);
                System.out.println("Filme cadastrado via SQS: " + cadastro.nome());
            }
            case ALTERAR -> {
                var alterar = objectMapper.convertValue(dados, DadosAlterarFilme.class);
                filmeService.alterarFilme(alterar);
                System.out.println("Filme alterado via SQS: " + alterar.id());
            }
            case ATUALIZAR_POPULARIDADE -> {
                filmeService.atualizarPopularidade(dto.idFilme());
                System.out.println("Popularidade atualizada via SQS: " + dto.idFilme());
            }
            case IMPORTAR_DA_API -> {
                // se página foi enviada no dados, pega de dados, senão pega idFilme()
                Integer pagina = dados != null && dados.isInt() ? dados.asInt() : dto.idFilme();
                filmeService.importarFilmesDaApi(pagina != null ? pagina : 1);
                System.out.println("Importação da API via SQS — página: " + pagina);
            }
            default -> System.out.println("Ação inválida recebida para FILME: " + acao);
        }
    }

    // ---------- AVALIACAO ----------
    private void processarAvaliacao(AcaoMensagem acao, JsonNode dados, AcaoMensagemDTO dto) {
        switch (acao) {
            case CADASTRAR -> {
                var cadastro = objectMapper.convertValue(dados, DadosCadastroAvaliacao.class);
                var avaliacao = avaliacaoService.avaliar(cadastro);
                System.out.println("Avaliação cadastrada via SQS: " + avaliacao.nm_usuario());
            }
            case ALTERAR -> {
                var alterar = objectMapper.convertValue(dados, DadosAlterarAvaliacao.class);
                var avaliacao = avaliacaoService.atualizarAvaliacao(alterar);
                System.out.println("Avaliação alterada via SQS: " + avaliacao.nm_usuario());
            }
            case DELETAR -> {
                avaliacaoService.deletarAvaliacao(dto.idFilme());
                System.out.println("Avaliação deletada via SQS: " + dto.idFilme());
            }
            case LISTAR_USUARIO -> {
                var lista = avaliacaoService.listarAvaliacoesDeUmUsuario(dto.idUsuario());
                System.out.println("Listadas " + lista.size() + " avaliações do usuário: " + dto.idUsuario());
            }
            case LISTAR_FILME -> {
                var lista = avaliacaoService.listarAvaliacoesFilme(dto.idFilme());
                System.out.println("Listadas " + lista.size() + " avaliações do filme: " + dto.idFilme());
            }
            case LISTAR_UNICA -> {
                var avaliacao = avaliacaoService.listarAvaliacaoDeUmFilme(dto.idFilme(), dto.idUsuario());
                System.out.println("Avaliação localizada via SQS — filme: " + dto.idFilme() + ", usuário: " + dto.idUsuario());
            }
            default -> System.out.println("Ação inválida recebida para AVALIACAO: " + acao);
        }
    }

    // ---------- PESSOA ----------
    private void processarPessoa(AcaoMensagem acao, JsonNode dados, AcaoMensagemDTO dto) {
        switch (acao) {
            case ADICIONAR, CADASTRAR -> {
                var cadastro = objectMapper.convertValue(dados, DadosCadastroPessoa.class);
                pessoaService.cadastrarPessoa(cadastro);
                System.out.println("Pessoa cadastrada via SQS: " + cadastro.nome());
            }
            case EDITAR, ALTERAR -> {
                var alterar = objectMapper.convertValue(dados, DadosAlterarPessoa.class);
                pessoaService.alterarPessoa(alterar);
                System.out.println("Pessoa alterada via SQS: " + alterar.id());
            }
            default -> System.out.println("Ação inválida recebida para PESSOA: " + acao);
        }
    }

    // ---------- GENERO ----------
    private void processarGenero(AcaoMensagem acao, JsonNode dados, AcaoMensagemDTO dto) {
        switch (acao) {
            case CADASTRAR, ADICIONAR -> {
                var cadastro = objectMapper.convertValue(dados, DadosCadastrarGenero.class);
                generoService.adicionarGenero(cadastro);
                System.out.println("Gênero cadastrado via SQS: " + cadastro.nome());
            }
            case EDITAR -> {
                var editar = objectMapper.convertValue(dados, DadosEditarGenero.class);
                generoService.editarGenero(editar);
                System.out.println("Gênero editado via SQS: " + editar.id());
            }
            case IMPORTAR_DA_API -> {
                generoService.importarGenerosDaApi();
                System.out.println("Gêneros importados da API via SQS");
            }
            default -> System.out.println("Ação inválida recebida para GENERO: " + acao);
        }
    }

    // ---------- FILME_PESSOA ----------
    private void processarFilmePessoa(AcaoMensagem acao, JsonNode dados, AcaoMensagemDTO dto) {
        switch (acao) {
            case ADICIONAR -> {
                var cadastro = objectMapper.convertValue(dados, DadosCadastroFilmePessoa.class);
                filmePessoaService.adicionarPessoaAoFilme(cadastro);
                System.out.println("FilmePessoa recebido — CADASTRAR: " + cadastro);
            }
            case ALTERAR -> {
                var alterar = objectMapper.convertValue(dados, DadosAlterarFilmePessoa.class);
                filmePessoaService.alterarPapelEmFilme(alterar);
                System.out.println("FilmePessoa recebido — ALTERAR PAPEL: " + alterar);
            }
            case DELETAR -> {
                filmePessoaService.deletarPessoaDeFilme(dto.idPessoa(), dto.idFilme());
                System.out.println("FilmePessoa recebido — DELETAR: pessoa=" + dto.idPessoa() + ", filme=" + dto.idFilme());
            }
            default -> System.out.println("Ação inválida recebida para FILME_PESSOA: " + acao);
        }
    }

    // ---------- USUARIO ----------
    private void processarUsuario(AcaoMensagem acao, JsonNode dados, AcaoMensagemDTO dto) {
        switch (acao) {

            case EDITAR -> {
                var editar = objectMapper.convertValue(dados, DadosEditarUsuario.class);
                usuarioService.editarUsuario(editar);
                System.out.println("Usuário editado via SQS: " + editar.id());
            }

            case DELETAR -> {
                usuarioService.desativarUsuario(dto.idUsuario());
                System.out.println("Usuário desativado via SQS: " + dto.idUsuario());
            }

            case LISTAR_USUARIO -> {
                var resultado = usuarioService.listarTodosUsuariosAtivos(null);
                System.out.println("Usuários ativos listados via SQS: " + resultado.getTotalElements());
            }

            default -> System.out.println("Ação inválida para USUARIO: " + acao);
        }
    }
}