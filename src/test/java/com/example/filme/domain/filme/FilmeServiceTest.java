package com.example.filme.domain.filme;

import com.example.filme.domain.avaliacao.Avaliacao;
import com.example.filme.domain.avaliacao.AvaliacaoRepository;
import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
import com.example.filme.domain.filme.dtos.DadosDetalhamentoFilme;
import com.example.filme.domain.filme.exceptions.FilmeBadRequestException;
import com.example.filme.domain.filme.exceptions.FilmeNotFoundException;
import com.example.filme.domain.filme_pessoa.FilmePessoaService;
import com.example.filme.domain.genero.Genero;
import com.example.filme.domain.genero.GeneroRepository;
import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.exceptions.GeneroBadRequestException;
import com.example.filme.domain.genero.exceptions.GeneroNotFoundException;
import com.example.filme.domain.usuario.Usuario;
import com.example.filme.domain.usuario.UsuarioRepository;
import com.example.filme.domain.usuario.exceptions.UsuarioBadRequestException;
import com.example.filme.domain.usuario.exceptions.UsuarioNotFoundException;
import com.example.filme.infra.tmdbAPI.MovieServiceTMDB;
import com.example.filme.infra.tmdbAPI.dtos.TMDBGenre;
import com.example.filme.infra.tmdbAPI.dtos.TMDBMovieDetail;
import com.example.filme.infra.tmdbAPI.dtos.TMDBMovieResult;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class FilmeServiceTest {

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MovieServiceTMDB movieServiceTMDB;

    @Mock
    private FilmePessoaService filmePessoaService;

    @Mock
    private FilmeRepository filmeRepository;

    @Captor
    private ArgumentCaptor<Filme> filmeArgumentCaptor;

    @InjectMocks
    private FilmeService filmeService;

    private Filme filme;
    private Genero genero;
    private Usuario usuario;
    private DadosCadastrarFilme dadosCadastrarFilme;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.filme = new Filme();
        this.genero = new Genero();
        filme.setGenero(genero);
        this.usuario = new Usuario();
    }

    @Test
    @DisplayName("Tete cadastro de filme")
    void deveriaCadastrarFilmes() {
        // Arrange
        Integer id = 1;

        genero = new Genero(id, "Ação");

        dadosCadastrarFilme = new DadosCadastrarFilme(
                "Matrix",
                genero.getId(),
                "poster.jpg",
                "Filme de ficção",
                1999
        );

        Filme filmeEsperado = new Filme(dadosCadastrarFilme);
        filmeEsperado.setGenero(genero);

        Mockito.when(generoRepository.findById(id)).thenReturn(Optional.of(genero));
        Mockito.when(filmeRepository.save(any(Filme.class))).thenReturn(filmeEsperado);

        // Act
        filmeService.cadastrarFilme(dadosCadastrarFilme);

        // Assert
        Mockito.verify(filmeRepository).save(filmeArgumentCaptor.capture());

        Filme resultado = filmeArgumentCaptor.getValue();

        assertEquals("Matrix", resultado.getNome());
        assertEquals("poster.jpg", resultado.getPosterUrl());
        assertEquals("Filme de ficção", resultado.getDescricao());
        assertEquals(1999, resultado.getAno());
        assertEquals(genero, resultado.getGenero());
    }

    @Test
    @DisplayName("Teste Exception FilmeBadRequestException com dados nulos ao tentar cadastrar filme")
    void deveriaLancarExceptionPoisDadosSaoNulos() {

        Throwable exception = catchThrowable(() -> filmeService.cadastrarFilme(dadosCadastrarFilme));

        assertThat(exception)
                .isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("Dados não podem ser nulos");
    }

    // Lista todos os filmes do banco de dados
    public List<DadosDetalhamentoFilme> listarTodosFilmes() {
        return filmeRepository.findAll().stream()
                .map(DadosDetalhamentoFilme::new)
                .toList();
    }

    @Test
    @DisplayName("Teste listar todos os filmes")
    void deveriaListarTodosOsFilmes() {

        List<Filme> filmes = List.of(filme);

        Mockito.when(filmeRepository.findAll()).thenReturn(filmes);

        List<DadosDetalhamentoFilme> resultado = filmeService.listarTodosFilmes();

        Mockito.verify(filmeRepository).findAll();

        List<DadosDetalhamentoFilme> esperado = filmes.stream()
                .map(DadosDetalhamentoFilme::new)
                .toList();


        assertEquals(esperado, resultado);

    }

    @Test
    @DisplayName("Deveria retornar os dados detalhados de um filme existente pelo ID")
    void deveriaListarUmFilmePorId() {
        // Arrange
        Integer idFilme = 1;

        filme.setNome("Matrix");
        filme.setDescricao("Filme de ficção");
        filme.setPosterUrl("poster.jpg");
        filme.setAno(1999);
        filme.setGenero(genero);

        Mockito.when(filmeRepository.findById(idFilme)).thenReturn(Optional.of(filme));

        // Act
        DadosDetalhamentoFilme resultado = filmeService.listarFilme(idFilme);

        // Assert
        assertNotNull(resultado);
        assertEquals("Matrix", resultado.nome());
        assertEquals("Filme de ficção", resultado.descricao());
        assertEquals("poster.jpg", resultado.posterUrl());
        assertEquals(1999, resultado.ano());
        assertEquals(genero.getNome(), resultado.genero()); // Supondo que o DTO retorna apenas o nome do gênero
    }

    @Test
    @DisplayName("Deveria lançar FilmeBadRequestException quando o ID do filme é nulo")
    void deveriaLancarExceptionQuandoIdFilmeForNulo() {
        // Arrange
        Integer idFilme = null;

        // Act
        Throwable exception = catchThrowable(() -> filmeService.listarFilme(idFilme));

        // Assert
        assertThat(exception)
                .isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("ID do filme não pode ser nulo");
    }

    @Test
    @DisplayName("Deveria lançar FilmeNotFoundException quando o filme não for encontrado pelo ID")
    void deveriaLancarExceptionQuandoFilmeNaoForEncontrado() {
        // Arrange
        Integer idFilme = -9;

        Mockito.when(filmeRepository.findById(idFilme)).thenReturn(Optional.empty());

        // Act
        Throwable exception = catchThrowable(() -> filmeService.listarFilme(idFilme));

        // Assert
        assertThat(exception)
                .isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme não encontrado com ID: " + idFilme);
    }

    @Test
    @DisplayName("Deve lançar exceção se dados de alteração forem nulos")
    void deveLancarExceptionSeDadosForemNulos() {
        Throwable exception = catchThrowable(() -> filmeService.alterarFilme(null));

        assertThat(exception)
                .isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("Dados inválidos para alteração");
    }

    @Test
    @DisplayName("Deve lançar exceção se filme não for encontrado")
    void deveLancarExceptionSeFilmeNaoForEncontrado() {
        DadosAlterarFilme dados = Mockito.mock(DadosAlterarFilme.class);
        Mockito.when(dados.id()).thenReturn(-999);

        Mockito.when(filmeRepository.findById(-999)).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> filmeService.alterarFilme(dados));

        assertThat(exception)
                .isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme não encontrado com ID: -999");
    }

    @Test
    @DisplayName("Deve lançar exceção se gênero não for encontrado ao alterar")
    void deveLancarExceptionSeGeneroNaoForEncontrado() {
        DadosAlterarFilme dados = Mockito.mock(DadosAlterarFilme.class);
        Mockito.when(dados.id()).thenReturn(1);
        Mockito.when(dados.id_genero()).thenReturn(-2);

        Filme filmeExistente = new Filme();
        Mockito.when(filmeRepository.findById(1)).thenReturn(Optional.of(filmeExistente));
        Mockito.when(generoRepository.findById(-2)).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> filmeService.alterarFilme(dados));

        assertThat(exception)
                .isInstanceOf(GeneroNotFoundException.class)
                .hasMessage("Gênero não encontrado com ID: -2");
    }

    @Test
    @DisplayName("Deve alterar apenas campos fornecidos e salvar o filme")
    void deveAlterarCamposInformados() {
        DadosAlterarFilme dados = Mockito.mock(DadosAlterarFilme.class);
        Mockito.when(dados.id()).thenReturn(1);
        Mockito.when(dados.nome()).thenReturn("Novo Nome");
        Mockito.when(dados.id_genero()).thenReturn(3);
        Mockito.when(dados.posterUrl()).thenReturn("novo_poster.jpg");
        Mockito.when(dados.descricao()).thenReturn("Nova descrição");
        Mockito.when(dados.ano()).thenReturn(2023);

        Filme filmeExistente = new Filme();
        filmeExistente.setNome("Nome Antigo");
        filmeExistente.setDescricao("Descrição Antiga");
        filmeExistente.setPosterUrl("poster_antigo.jpg");
        filmeExistente.setAno(2000);

        Genero genero = new Genero(3, "Comédia");

        // Mocks
        Mockito.when(filmeRepository.findById(1)).thenReturn(Optional.of(filmeExistente));
        Mockito.when(generoRepository.findById(3)).thenReturn(Optional.of(genero));
        Mockito.when(filmeRepository.save(Mockito.any(Filme.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DadosDetalhamentoFilme resultado = filmeService.alterarFilme(dados);

        Mockito.verify(filmeRepository).save(filmeArgumentCaptor.capture());
        Filme salvo = filmeArgumentCaptor.getValue();

        assertEquals("Novo Nome", salvo.getNome());
        assertEquals("Nova descrição", salvo.getDescricao());
        assertEquals("novo_poster.jpg", salvo.getPosterUrl());
        assertEquals(2023, salvo.getAno());
        assertEquals(genero, salvo.getGenero());

        assertEquals("Novo Nome", resultado.nome());
        assertEquals("Nova descrição", resultado.descricao());
        assertEquals("novo_poster.jpg", resultado.posterUrl());
        assertEquals(2023, resultado.ano());
        assertEquals("Comédia", resultado.genero());
    }

    @Test
    @DisplayName("Deve alterar somente campos não nulos, ignorando os nulos")
    void deveIgnorarCamposNulosAoAlterar() {
        DadosAlterarFilme dados = Mockito.mock(DadosAlterarFilme.class);
        Mockito.when(dados.id()).thenReturn(1);
        Mockito.when(dados.nome()).thenReturn(null);
        Mockito.when(dados.id_genero()).thenReturn(null);
        Mockito.when(dados.posterUrl()).thenReturn(null);
        Mockito.when(dados.descricao()).thenReturn(null);
        Mockito.when(dados.ano()).thenReturn(null);

        Filme filmeExistente = new Filme();
        filmeExistente.setNome("Nome Existente");
        filmeExistente.setDescricao("Descrição Existente");
        filmeExistente.setPosterUrl("poster_existente.jpg");
        filmeExistente.setAno(1999);
        filmeExistente.setGenero(new Genero(5, "Drama"));

        Mockito.when(filmeRepository.findById(1)).thenReturn(Optional.of(filmeExistente));
        Mockito.when(filmeRepository.save(Mockito.any(Filme.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DadosDetalhamentoFilme resultado = filmeService.alterarFilme(dados);

        Mockito.verify(filmeRepository).save(filmeArgumentCaptor.capture());
        Filme salvo = filmeArgumentCaptor.getValue();

        assertEquals("Nome Existente", salvo.getNome());
        assertEquals("Descrição Existente", salvo.getDescricao());
        assertEquals("poster_existente.jpg", salvo.getPosterUrl());
        assertEquals(1999, salvo.getAno());
        assertEquals("Drama", salvo.getGenero().getNome());

        assertEquals("Nome Existente", resultado.nome());
        assertEquals("Descrição Existente", resultado.descricao());
        assertEquals("poster_existente.jpg", resultado.posterUrl());
        assertEquals(1999, resultado.ano());
        assertEquals("Drama", resultado.genero());
    }


    @Test
    void deveriaRecomendarFilmesComBaseNoIdDoUsuario() {
        // Arrange
        Integer id = 1;

        List<Filme> filmes = List.of(filme);

        Mockito.when(usuarioRepository.existsById(id)).thenReturn(true);
        Mockito.when(filmeRepository.recomendarFilmesParecidos(id)).thenReturn(filmes);

        List<DadosDetalhamentoFilme> resultado = filmeService.filmesRecomendadosParaUsuario(id);

        Mockito.verify(filmeRepository).recomendarFilmesParecidos(id);

        List<DadosDetalhamentoFilme> esperado = filmes.stream()
                .map(DadosDetalhamentoFilme::new)
                .toList();

        assertEquals(esperado, resultado);
    }

    @Test
    void deveriaLancarExceptionPoisIdUsuarioEstaNulo() {
        // Arrange
        Integer id = null;

        Throwable exception = catchThrowable(() -> filmeService.filmesRecomendadosParaUsuario(id));

        assertThat(exception)
                .isInstanceOf(UsuarioBadRequestException.class)
                .hasMessage("ID de usuário inválido.");
    }

    @Test
    void deveriaLancarExceptionPoisIdUsuarioNaoExiste() {
        // Arrange
        Integer id = -9;

        Mockito.when(usuarioRepository.existsById(id)).thenReturn(false);

        Throwable exception = catchThrowable(() -> filmeService.filmesRecomendadosParaUsuario(id));

        assertThat(exception)
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Usuário com ID " + id + " não encontrado.");
    }

    @Test
    @DisplayName("Deveria atualizar a popularidade do filme com base nas avaliações")
    void deveriaAtualizarPopularidadeDoFilme() {
        // Arrange
        Integer idFilme = 1;

        filme.setNome("Matrix");

        Avaliacao avaliacao1 = new Avaliacao();
        avaliacao1.setNota(4.0f);

        Avaliacao avaliacao2 = new Avaliacao();
        avaliacao2.setNota(5.0f);

        List<Avaliacao> avaliacoes = List.of(avaliacao1, avaliacao2);

        Mockito.when(filmeRepository.findById(idFilme)).thenReturn(Optional.of(filme));
        Mockito.when(avaliacaoRepository.findByFilmeId(idFilme)).thenReturn(avaliacoes);
        Mockito.when(filmeRepository.save(any(Filme.class))).thenReturn(filme);

        // Act
        filmeService.atualizarPopularidade(idFilme);

        // Assert
        Mockito.verify(filmeRepository).save(filmeArgumentCaptor.capture());
        Filme filmeSalvo = filmeArgumentCaptor.getValue();

        assertEquals(4.5f, filmeSalvo.getPopularidade());
    }

    @Test
    @DisplayName("Deveria lançar exceção pois IdFilme é nulo ao tentar atualizar popularidade")
    void deveriaLancarExcecaoSeIdFIlmeNulo() {

        Integer id = null;

        Throwable exception = catchThrowable(() -> filmeService.atualizarPopularidade(id));

        assertThat(exception)
                .isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("ID do filme não pode ser nulo");

    }

    @Test
    @DisplayName("Deve lançar FilmeBadRequestException se página for menor que 0")
    void deveriaLancarExcecaoSePaginaMenorQueZero() {
        Throwable exception = catchThrowable(() -> filmeService.importarFilmesDaApi(-1));

        assertThat(exception)
                .isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("Página não pode ser menor que 0");
    }

    @Test
    @DisplayName("Deve ignorar filmes já existentes no banco ao importar")
    void deveIgnorarFilmesJaExistentes() {
        // Arrange
        int pagina = 1;

        TMDBMovieDetail detalhe = new TMDBMovieDetail();
        detalhe.id = 100;
        detalhe.title = "Filme Existente";
        detalhe.overview = "Descricao";
        detalhe.poster_path = "/poster.jpg";
        detalhe.release_date = "2000-01-01";
        detalhe.genres = List.of();

        Mockito.when(movieServiceTMDB.buscarFilmesPorPagina(pagina)).thenReturn(List.of(detalhe));
        Mockito.when(filmeRepository.findByTmdbId(100)).thenReturn(Optional.of(new Filme()));

        // Act
        List<DadosDetalhamentoFilme> resultado = filmeService.importarFilmesDaApi(pagina);

        // Assert
        assertTrue(resultado.isEmpty());
        Mockito.verify(filmeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve importar filme e buscar detalhes corretamente")
    void deveImportarFilmeCorretamente() {
        int pagina = 1;
        int tmdbId = 123;
        String nomeGenero = "Ação";

        TMDBMovieDetail detalhe = new TMDBMovieDetail();
        detalhe.id = tmdbId;
        detalhe.title = "Matrix";
        detalhe.overview = "Um filme";
        detalhe.poster_path = "/poster.jpg";
        detalhe.release_date = "1999-03-31";
        detalhe.genres = List.of(new TMDBGenre() {{
            id = 1;
            name = nomeGenero;
        }});

        Genero genero = new Genero(null, nomeGenero);

        // Mocks
        Mockito.when(movieServiceTMDB.buscarFilmesPorPagina(pagina))
                .thenReturn(List.of(detalhe));

        Mockito.when(movieServiceTMDB.buscarDetalhesDoFilme(tmdbId))
                .thenReturn(detalhe);

        Mockito.when(filmeRepository.findByTmdbId(tmdbId)).thenReturn(Optional.empty());
        Mockito.when(generoRepository.findByNomeIgnoreCase(nomeGenero)).thenReturn(Optional.of(genero));
        Mockito.when(filmeRepository.save(any(Filme.class))).thenAnswer(inv -> inv.getArgument(0));

        // Execução
        List<DadosDetalhamentoFilme> resultadoFinal = filmeService.importarFilmesDaApi(pagina);

        // Verificações
        assertEquals(1, resultadoFinal.size());
        assertEquals("Matrix", resultadoFinal.get(0).nome());
    }


    @Test
    @DisplayName("Deve criar novo gênero se não existir no repositório")
    void deveCriarNovoGeneroSeNaoExistir() {
        int pagina = 1;
        int tmdbId = 123;
        String nomeGenero = "Comédia";

        TMDBMovieDetail detalhe = new TMDBMovieDetail();
        detalhe.id = tmdbId;
        detalhe.title = "Matrix";
        detalhe.overview = "Um filme de ficção científica";
        detalhe.poster_path = "/poster.jpg";
        detalhe.release_date = "1999-03-31";
        detalhe.genres = List.of(new TMDBGenre() {{
            id = 1;
            name = nomeGenero;
        }});

        Genero generoCriado = new Genero(null, nomeGenero);

        // Mocks
        Mockito.when(movieServiceTMDB.buscarFilmesPorPagina(pagina)).thenReturn(List.of(detalhe));
        Mockito.when(movieServiceTMDB.buscarDetalhesDoFilme(tmdbId)).thenReturn(detalhe);
        Mockito.when(filmeRepository.findByTmdbId(tmdbId)).thenReturn(Optional.empty());
        Mockito.when(generoRepository.findByNomeIgnoreCase(nomeGenero)).thenReturn(Optional.empty());
        Mockito.when(generoRepository.save(Mockito.any())).thenReturn(generoCriado);
        Mockito.when(filmeRepository.save(Mockito.any(Filme.class))).thenAnswer(inv -> inv.getArgument(0));

        // Execução
        List<DadosDetalhamentoFilme> resultadoFinal = filmeService.importarFilmesDaApi(pagina);

        // Verificações
        assertEquals(1, resultadoFinal.size());
        Mockito.verify(generoRepository).save(Mockito.any(Genero.class));
    }
}