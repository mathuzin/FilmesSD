package com.example.filme.domain.avaliacao;

import com.example.filme.domain.avaliacao.dtos.DadosAlterarAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.domain.avaliacao.dtos.DadosDetalhamentoAvaliacao;
import com.example.filme.domain.avaliacao.exceptions.AvaliacaoBadRequestException;
import com.example.filme.domain.avaliacao.exceptions.AvaliacaoNotFoundException;
import com.example.filme.domain.filme.Filme;
import com.example.filme.domain.filme.FilmeRepository;
import com.example.filme.domain.filme.FilmeService;
import com.example.filme.domain.filme.exceptions.FilmeBadRequestException;
import com.example.filme.domain.filme.exceptions.FilmeNotFoundException;
import com.example.filme.domain.usuario.Usuario;
import com.example.filme.domain.usuario.UsuarioRepository;
import com.example.filme.domain.usuario.exceptions.UsuarioBadRequestException;
import com.example.filme.domain.usuario.exceptions.UsuarioNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FilmeRepository filmeRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private FilmeService filmeService;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Captor
    private ArgumentCaptor<Avaliacao> avaliacaoArgumentCaptor;

    private Usuario usuario;
    private Filme filme;
    private DadosCadastroAvaliacao dadosCadastroAvaliacao;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        filme = new Filme();

        dadosCadastroAvaliacao = mock(DadosCadastroAvaliacao.class);
    }

    @Test
    @DisplayName("Deve lançar exceção se dados forem nulos")
    void deveLancarExceptionSeDadosForemNulos() {
        Throwable exception = catchThrowable(() -> avaliacaoService.avaliar(null));

        assertThat(exception)
                .isInstanceOf(AvaliacaoBadRequestException.class)
                .hasMessage("Dados da avaliação não podem ser nulos.");
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não existir")
    void deveLancarExceptionSeUsuarioNaoExistir() {
        DadosCadastroAvaliacao dados = mock(DadosCadastroAvaliacao.class);
        Mockito.when(dados.usuario_id()).thenReturn(-99);

        Mockito.when(usuarioRepository.existsById(-99)).thenReturn(false);

        Throwable exception = catchThrowable(() -> avaliacaoService.avaliar(dados));

        assertThat(exception)
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Usuário com ID -99 não encontrado.");
    }

    @Test
    @DisplayName("Deve lançar exceção se filme não existir")
    void deveLancarExceptionSeFilmeNaoExistir() {
        DadosCadastroAvaliacao dados = mock(DadosCadastroAvaliacao.class);
        Mockito.when(dados.usuario_id()).thenReturn(1);
        Mockito.when(dados.filme_id()).thenReturn(-99);

        Mockito.when(usuarioRepository.existsById(1)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(-99)).thenReturn(false);

        Throwable exception = catchThrowable(() -> avaliacaoService.avaliar(dados));

        assertThat(exception)
                .isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme com ID -99 não encontrado.");
    }

    @Test
    @DisplayName("Deve lançar exceção se avaliação já existir para usuário e filme")
    void deveLancarExceptionSeAvaliacaoJaExistir() {
        DadosCadastroAvaliacao dados = mock(DadosCadastroAvaliacao.class);
        Mockito.when(dados.usuario_id()).thenReturn(1);
        Mockito.when(dados.filme_id()).thenReturn(1);

        Mockito.when(usuarioRepository.existsById(1)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(1)).thenReturn(true);
        Mockito.when(avaliacaoRepository.existsByFilmeIdAndUsuarioId(1, 1)).thenReturn(true);

        Throwable exception = catchThrowable(() -> avaliacaoService.avaliar(dados));

        assertThat(exception)
                .isInstanceOf(AvaliacaoBadRequestException.class)
                .hasMessage("Este usuário já avaliou este filme.");
    }

    @Test
    @DisplayName("Deve criar nova avaliação e atualizar popularidade do filme")
    void deveCriarNovaAvaliacao() {
        DadosCadastroAvaliacao dados = mock(DadosCadastroAvaliacao.class);

        Mockito.when(dados.usuario_id()).thenReturn(usuario.getId());
        Mockito.when(dados.filme_id()).thenReturn(filme.getId());

        Mockito.when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        Mockito.when(filmeRepository.existsById(filme.getId())).thenReturn(true);
        Mockito.when(avaliacaoRepository.existsByFilmeIdAndUsuarioId(filme.getId(), usuario.getId())).thenReturn(false);

        Mockito.when(usuarioRepository.getReferenceById(usuario.getId())).thenReturn(usuario);
        Mockito.when(filmeRepository.getReferenceById(filme.getId())).thenReturn(filme);

        Mockito.when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(filmeService).atualizarPopularidade(filme.getId());

        DadosDetalhamentoAvaliacao resultado = avaliacaoService.avaliar(dados);

        Mockito.verify(avaliacaoRepository).save(avaliacaoArgumentCaptor.capture());
        Mockito.verify(filmeService).atualizarPopularidade(filme.getId());

        Avaliacao salvo = avaliacaoArgumentCaptor.getValue();

        assertEquals(usuario, salvo.getUsuario());
        assertEquals(filme, salvo.getFilme());

        // Verifica se o DTO retornado contém a avaliação criada
        assertNotNull(resultado);
        assertEquals(salvo.getUsuario().getId(), resultado.idFIlme());
        assertEquals(salvo.getFilme().getId(), resultado.idUsuario());
        assertEquals(salvo.getFilme().getNome(), resultado.nm_filme());
        assertEquals(salvo.getUsuario().getLogin(), resultado.nm_usuario());
        assertEquals(salvo.getDs_avaliacao(), resultado.ds_avaliacao());
        assertEquals(salvo.getNota(), resultado.nota());
    }

    @Test
    @DisplayName("Deve lançar exceção se ID do usuário for nulo")
    void deveLancarExcecaoSeIdUsuarioForNulo() {
        Throwable exception = catchThrowable(() -> avaliacaoService.listarAvaliacoesDeUmUsuario(null));

        assertThat(exception)
                .isInstanceOf(UsuarioBadRequestException.class)
                .hasMessage("Id de usuário não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não existir")
    void deveLancarExcecaoSeUsuarioNaoExistirNaListagem() {
        Integer idUsuario = 999;

        Mockito.when(usuarioRepository.existsById(idUsuario)).thenReturn(false);

        Throwable exception = catchThrowable(() -> avaliacaoService.listarAvaliacoesDeUmUsuario(idUsuario));

        assertThat(exception)
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Usuário com ID 999 não encontrado.");
    }

    @Test
    @DisplayName("Deve listar todas as avaliações de um usuário existente")
    void deveListarAvaliacoesDeUsuarioExistente() {
        Integer idUsuario = 1;
        Integer idFilme = 10;

        Usuario usuario = mock(Usuario.class);
        Filme filme = mock(Filme.class);

        Avaliacao avaliacao1 = mock(Avaliacao.class);
        Avaliacao avaliacao2 = mock(Avaliacao.class);

        Mockito.when(avaliacao1.getUsuario()).thenReturn(usuario);
        Mockito.when(avaliacao1.getFilme()).thenReturn(filme);
        Mockito.when(avaliacao2.getUsuario()).thenReturn(usuario);
        Mockito.when(avaliacao2.getFilme()).thenReturn(filme);

        Mockito.when(usuario.getId()).thenReturn(idUsuario);
        Mockito.when(usuario.getLogin()).thenReturn("usuario1");
        Mockito.when(filme.getId()).thenReturn(idFilme);
        Mockito.when(filme.getNome()).thenReturn("Filme X");

        Mockito.when(avaliacao1.getDs_avaliacao()).thenReturn("Muito bom");
        Mockito.when(avaliacao2.getDs_avaliacao()).thenReturn("Excelente");
        Mockito.when(avaliacao1.getNota()).thenReturn(4.5f);
        Mockito.when(avaliacao2.getNota()).thenReturn(5.0f);

        Mockito.when(usuarioRepository.existsById(idUsuario)).thenReturn(true);
        Mockito.when(avaliacaoRepository.findByUsuarioId(idUsuario)).thenReturn(List.of(avaliacao1, avaliacao2));

        List<DadosDetalhamentoAvaliacao> resultado = avaliacaoService.listarAvaliacoesDeUmUsuario(idUsuario);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        Mockito.verify(usuarioRepository).existsById(idUsuario);
        Mockito.verify(avaliacaoRepository).findByUsuarioId(idUsuario);
    }

    @Test
    @DisplayName("Deve disparar exception ao procurar avaliações de um filme pois idFilme é nulo")
    void deveriaLancarExcecaoPoisIdFilmeNulo() {

        Integer id = null;

        Throwable exception = catchThrowable(() -> avaliacaoService.listarAvaliacoesFilme(id));

        assertThat(exception)
                .isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("Id do Filme não pode ser nulo.");

    }

    @Test
    @DisplayName("Deve disparar exception ao procurar avaliações de um filme pois idFilme não existe")
    void deveriaLancarExcecaoPoisIdFilmeNaoExiste() {

        Integer id = -9;

        Mockito.when(filmeRepository.existsById(id)).thenReturn(false);

        Throwable exception = catchThrowable(() -> avaliacaoService.listarAvaliacoesFilme(id));

        assertThat(exception)
                .isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme com ID -9 não encontrado.");

    }

    @Test
    @DisplayName("Deve listar todas as avaliações de um filme existente")
    void deveListarAvaliacoesDeFilmeExistente() {
        Integer idFilme = 10;
        Integer idUsuario = 1;

        Filme filme = mock(Filme.class);
        Usuario usuario = mock(Usuario.class);

        Avaliacao avaliacao1 = mock(Avaliacao.class);
        Avaliacao avaliacao2 = mock(Avaliacao.class);

        Mockito.when(filme.getId()).thenReturn(idFilme);
        Mockito.when(filme.getNome()).thenReturn("Filme Teste");

        Mockito.when(usuario.getId()).thenReturn(idUsuario);
        Mockito.when(usuario.getLogin()).thenReturn("usuario_teste");

        Mockito.when(avaliacao1.getFilme()).thenReturn(filme);
        Mockito.when(avaliacao1.getUsuario()).thenReturn(usuario);
        Mockito.when(avaliacao1.getNota()).thenReturn(4.0f);
        Mockito.when(avaliacao1.getDs_avaliacao()).thenReturn("Muito bom");

        Mockito.when(avaliacao2.getFilme()).thenReturn(filme);
        Mockito.when(avaliacao2.getUsuario()).thenReturn(usuario);
        Mockito.when(avaliacao2.getNota()).thenReturn(5.0f);
        Mockito.when(avaliacao2.getDs_avaliacao()).thenReturn("Obra-prima");

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(true);
        Mockito.when(avaliacaoRepository.findByFilmeId(idFilme)).thenReturn(List.of(avaliacao1, avaliacao2));

        List<DadosDetalhamentoAvaliacao> resultado = avaliacaoService.listarAvaliacoesFilme(idFilme);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        Mockito.verify(filmeRepository).existsById(idFilme);
        Mockito.verify(avaliacaoRepository).findByFilmeId(idFilme);
    }

    @Test
    @DisplayName("Deve lançar exceção se id do filme for nulo ao buscar avaliação de um usuário a um filme")
    void deveLancarExcecaoSeIdFilmeForNuloBuscarAvaliacaoFilmeUsuario() {
        Integer idUsuario = 1;

        Throwable exception = catchThrowable(() -> avaliacaoService.listarAvaliacaoDeUmFilme(null, idUsuario));

        assertThat(exception)
                .isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("Id do Filme não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção se filme não existir ao buscar avaliação de um usuário a um filme")
    void deveLancarExcecaoSeFilmeNaoExistirBuscarAvaliacaoFilmeUsuario() {
        Integer idFilme = -1;
        Integer idUsuario = 1;

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(false);

        Throwable exception = catchThrowable(() -> avaliacaoService.listarAvaliacaoDeUmFilme(idFilme, idUsuario));

        assertThat(exception)
                .isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme com ID -1 não encontrado.");
    }

    @Test
    @DisplayName("Deve lançar exceção se id do usuário for nulo ao buscar avaliação de um usuário a um filme")
    void deveLancarExcecaoSeIdUsuarioForNuloBuscarAvaliacaoFilmeUsuario() {
        Integer idFilme = 1;

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(true);

        Throwable exception = catchThrowable(() -> avaliacaoService.listarAvaliacaoDeUmFilme(idFilme, null));

        assertThat(exception)
                .isInstanceOf(UsuarioBadRequestException.class)
                .hasMessage("Id do Usuário não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não existir ao buscar avaliação de um usuário a um filme")
    void deveLancarExcecaoSeUsuarioNaoExistirBuscarAvaliacaoFilmeUsuario() {
        Integer idFilme = 1;
        Integer idUsuario = -1;

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(true);
        Mockito.when(usuarioRepository.existsById(idUsuario)).thenReturn(false);

        Throwable exception = catchThrowable(() -> avaliacaoService.listarAvaliacaoDeUmFilme(idFilme, idUsuario));

        assertThat(exception)
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Usuário com ID -1 não encontrado.");
    }

    @Test
    @DisplayName("Deve lançar exceção se avaliação não existir ao buscar avaliação de um usuário a um filme")
    void deveLancarExcecaoSeAvaliacaoNaoExistirBuscarAvaliacaoFilmeUsuario() {
        Integer idFilme = 1;
        Integer idUsuario = 1;

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(true);
        Mockito.when(usuarioRepository.existsById(idUsuario)).thenReturn(true);
        Mockito.when(avaliacaoRepository.findByFilmeIdAndUsuarioId(idFilme, idUsuario)).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> avaliacaoService.listarAvaliacaoDeUmFilme(idFilme, idUsuario));

        assertThat(exception)
                .isInstanceOf(AvaliacaoNotFoundException.class)
                .hasMessage("Este usuário ainda não avaliou este filme.");
    }

    @Test
    @DisplayName("Deve retornar a avaliação de um usuário para um filme")
    void deveRetornarAvaliacaoDeUsuarioParaFilme() {
        Integer idFilme = 1;
        Integer idUsuario = 2;

        Filme filme = mock(Filme.class);
        Usuario usuario = mock(Usuario.class);

        Mockito.when(filme.getId()).thenReturn(idFilme);
        Mockito.when(filme.getNome()).thenReturn("Filme Teste");

        Mockito.when(usuario.getId()).thenReturn(idUsuario);
        Mockito.when(usuario.getLogin()).thenReturn("usuario_teste");

        Avaliacao avaliacao = mock(Avaliacao.class);

        Mockito.when(avaliacao.getFilme()).thenReturn(filme);
        Mockito.when(avaliacao.getUsuario()).thenReturn(usuario);
        Mockito.when(avaliacao.getNota()).thenReturn(4.5f);
        Mockito.when(avaliacao.getDs_avaliacao()).thenReturn("Gostei bastante");

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(true);
        Mockito.when(usuarioRepository.existsById(idUsuario)).thenReturn(true);
        Mockito.when(avaliacaoRepository.findByFilmeIdAndUsuarioId(idFilme, idUsuario))
                .thenReturn(Optional.of(avaliacao));

        DadosDetalhamentoAvaliacao resultado = avaliacaoService.listarAvaliacaoDeUmFilme(idFilme, idUsuario);

        assertNotNull(resultado);
        assertEquals(idFilme, resultado.idFIlme());
        assertEquals(idUsuario, resultado.idUsuario());
        assertEquals("Filme Teste", resultado.nm_filme());
        assertEquals("usuario_teste", resultado.nm_usuario());
        assertEquals("Gostei bastante", resultado.ds_avaliacao());
        assertEquals(4.5f, resultado.nota());
    }

    @Test
    @DisplayName("Deve lançar exceção se os dados forem nulos ao editar avaliação")
    void deveLancarExcecaoSeDadosForemNulos() {
        Throwable exception = catchThrowable(() -> avaliacaoService.atualizarAvaliacao(null));

        assertThat(exception)
                .isInstanceOf(AvaliacaoBadRequestException.class)
                .hasMessage("Dados não podem ser nulos");
    }

    @Test
    @DisplayName("Deve lançar exceção se avaliação não for encontrada pelo ID")
    void deveLancarExcecaoSeAvaliacaoNaoForEncontrada() {
        DadosAlterarAvaliacao dados = mock(DadosAlterarAvaliacao.class);
        Mockito.when(dados.id()).thenReturn(-1);

        Mockito.when(avaliacaoRepository.existsById(-1)).thenReturn(false);

        Throwable exception = catchThrowable(() -> avaliacaoService.atualizarAvaliacao(dados));

        assertThat(exception)
                .isInstanceOf(AvaliacaoNotFoundException.class)
                .hasMessage("Avaliação com ID -1 não encontrada.");
    }

    @Test
    @DisplayName("Deve atualizar somente a nota se a descrição for nula")
    void deveAtualizarSomenteNota() {
        Avaliacao avaliacao = mock(Avaliacao.class);
        Filme filme = mock(Filme.class);
        Usuario usuario = mock(Usuario.class);

        Integer id = 1;
        Float novaNota = 4.8f;

        DadosAlterarAvaliacao dados = mock(DadosAlterarAvaliacao.class);
        Mockito.when(dados.id()).thenReturn(id);
        Mockito.when(dados.nota()).thenReturn(novaNota);
        Mockito.when(dados.ds_avaliacao()).thenReturn(null);

        Mockito.when(avaliacaoRepository.existsById(id)).thenReturn(true);
        Mockito.when(avaliacaoRepository.getReferenceById(id)).thenReturn(avaliacao);

        Mockito.when(avaliacao.getFilme()).thenReturn(filme);
        Mockito.when(filme.getId()).thenReturn(1);
        Mockito.when(filme.getNome()).thenReturn("Filme Teste");

        Mockito.when(avaliacao.getUsuario()).thenReturn(usuario);
        Mockito.when(usuario.getId()).thenReturn(10);
        Mockito.when(usuario.getLogin()).thenReturn("joao");

        Mockito.when(avaliacao.getNota()).thenReturn(novaNota);
        Mockito.when(avaliacao.getDs_avaliacao()).thenReturn(null);

        Mockito.when(avaliacaoRepository.save(avaliacao)).thenReturn(avaliacao);

        DadosDetalhamentoAvaliacao resultado = avaliacaoService.atualizarAvaliacao(dados);

        Mockito.verify(avaliacao).setNota(novaNota);
        Mockito.verify(avaliacao, Mockito.never()).setDs_avaliacao(any());
        Mockito.verify(filmeService).atualizarPopularidade(1);

        assertNotNull(resultado);
        assertEquals("joao", resultado.nm_usuario());
        assertEquals("Filme Teste", resultado.nm_filme());
        assertEquals(novaNota, resultado.nota());
        assertNull(resultado.ds_avaliacao());
    }


    @Test
    @DisplayName("Deve atualizar somente a descrição se a nota for nula")
    void deveAtualizarSomenteDescricao() {
        Avaliacao avaliacao = mock(Avaliacao.class);
        Filme filme = mock(Filme.class);
        Usuario usuario = mock(Usuario.class);

        Integer id = 2;
        String novaDescricao = "Nova descrição da avaliação";

        DadosAlterarAvaliacao dados = mock(DadosAlterarAvaliacao.class);
        Mockito.when(dados.id()).thenReturn(id);
        Mockito.when(dados.nota()).thenReturn(null);
        Mockito.when(dados.ds_avaliacao()).thenReturn(novaDescricao);

        Mockito.when(avaliacaoRepository.existsById(id)).thenReturn(true);
        Mockito.when(avaliacaoRepository.getReferenceById(id)).thenReturn(avaliacao);

        Mockito.when(avaliacao.getFilme()).thenReturn(filme);
        Mockito.when(filme.getId()).thenReturn(2);
        Mockito.when(filme.getNome()).thenReturn("Filme Teste 2");

        Mockito.when(avaliacao.getUsuario()).thenReturn(usuario);
        Mockito.when(usuario.getId()).thenReturn(20);
        Mockito.when(usuario.getLogin()).thenReturn("maria");

        Mockito.when(avaliacao.getNota()).thenReturn(null);
        Mockito.when(avaliacao.getDs_avaliacao()).thenReturn(novaDescricao);

        Mockito.when(avaliacaoRepository.save(avaliacao)).thenReturn(avaliacao);

        DadosDetalhamentoAvaliacao resultado = avaliacaoService.atualizarAvaliacao(dados);

        Mockito.verify(avaliacao).setDs_avaliacao(novaDescricao);
        Mockito.verify(avaliacao, Mockito.never()).setNota(any());
        Mockito.verify(filmeService).atualizarPopularidade(2);

        assertNotNull(resultado);
        assertEquals("maria", resultado.nm_usuario());
        assertEquals("Filme Teste 2", resultado.nm_filme());
        assertNull(resultado.nota());
        assertEquals(novaDescricao, resultado.ds_avaliacao());
    }


    @Test
    @DisplayName("Deve atualizar nota e descrição")
    void deveAtualizarNotaEDescricao() {
        Avaliacao avaliacao = mock(Avaliacao.class);
        Filme filme = mock(Filme.class);
        Usuario usuario = mock(Usuario.class);

        Integer id = 3;
        Float novaNota = 3.0f;
        String novaDescricao = "Atualização completa";

        DadosAlterarAvaliacao dados = mock(DadosAlterarAvaliacao.class);
        Mockito.when(dados.id()).thenReturn(id);
        Mockito.when(dados.nota()).thenReturn(novaNota);
        Mockito.when(dados.ds_avaliacao()).thenReturn(novaDescricao);

        Mockito.when(avaliacaoRepository.existsById(id)).thenReturn(true);
        Mockito.when(avaliacaoRepository.getReferenceById(id)).thenReturn(avaliacao);

        Mockito.when(avaliacao.getFilme()).thenReturn(filme);
        Mockito.when(filme.getId()).thenReturn(3);
        Mockito.when(filme.getNome()).thenReturn("Filme X");

        Mockito.when(avaliacao.getUsuario()).thenReturn(usuario);
        Mockito.when(usuario.getId()).thenReturn(10);
        Mockito.when(usuario.getLogin()).thenReturn("joao");

        Mockito.when(avaliacao.getNota()).thenReturn(novaNota);
        Mockito.when(avaliacao.getDs_avaliacao()).thenReturn(novaDescricao);

        Mockito.when(avaliacaoRepository.save(avaliacao)).thenReturn(avaliacao);

        DadosDetalhamentoAvaliacao resultado = avaliacaoService.atualizarAvaliacao(dados);

        Mockito.verify(avaliacao).setNota(novaNota);
        Mockito.verify(avaliacao).setDs_avaliacao(novaDescricao);
        Mockito.verify(filmeService).atualizarPopularidade(3);

        assertNotNull(resultado);
        assertEquals("Filme X", resultado.nm_filme());
        assertEquals("joao", resultado.nm_usuario());
        assertEquals(novaNota, resultado.nota());
        assertEquals(novaDescricao, resultado.ds_avaliacao());
    }

    @Test
    @DisplayName("Deve lançar exceção se o ID da avaliação for nulo")
    void deveLancarExcecaoSeIdForNulo() {
        Integer id = null;

        Throwable exception = catchThrowable(() -> avaliacaoService.deletarAvaliacao(id));

        assertThat(exception)
                .isInstanceOf(AvaliacaoBadRequestException.class)
                .hasMessage("Não existe avaliação com ID nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção se a avaliação não existir")
    void deveLancarExcecaoSeAvaliacaoNaoExistir() {
        Integer id = -1;

        Mockito.when(avaliacaoRepository.findById(id)).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> avaliacaoService.deletarAvaliacao(id));

        assertThat(exception)
                .isInstanceOf(AvaliacaoNotFoundException.class)
                .hasMessage("Avaliação com ID -1 não encontrada.");
    }

    @Test
    @DisplayName("Deve deletar a avaliação corretamente e atualizar popularidade")
    void deveDeletarAvaliacaoComSucesso() {
        Integer id = 10;

        Avaliacao avaliacao = mock(Avaliacao.class);
        Filme filme = mock(Filme.class);

        Mockito.when(avaliacaoRepository.findById(id)).thenReturn(Optional.of(avaliacao));
        Mockito.when(avaliacao.getFilme()).thenReturn(filme);
        Mockito.when(filme.getId()).thenReturn(100);

        avaliacaoService.deletarAvaliacao(id);

        Mockito.verify(avaliacaoRepository).delete(avaliacaoArgumentCaptor.capture());
        Avaliacao capturado = avaliacaoArgumentCaptor.getValue();

        Mockito.verify(filmeService).atualizarPopularidade(100);

        assertEquals(avaliacao, capturado);
    }
}