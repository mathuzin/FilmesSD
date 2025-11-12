package com.example.filme.domain.filme_pessoa;

import com.example.filme.domain.filme.Filme;
import com.example.filme.domain.filme.FilmeRepository;
import com.example.filme.domain.filme.exceptions.FilmeBadRequestException;
import com.example.filme.domain.filme.exceptions.FilmeNotFoundException;
import com.example.filme.domain.filme_pessoa.dtos.DadosAlterarFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.domain.filme_pessoa.dtos.DadosDetalhamentoFilmePessoa;
import com.example.filme.domain.pessoa.Pessoa;
import com.example.filme.domain.pessoa.PessoaRepository;
import com.example.filme.domain.pessoa.Tipo;
import com.example.filme.domain.pessoa.dtos.DadosDetalhamentoPessoa;
import com.example.filme.domain.pessoa.exceptions.PessoaNotFoundException;
import com.example.filme.infra.tmdbAPI.MovieServiceTMDB;
import com.example.filme.infra.tmdbAPI.dtos.TMDBCast;
import com.example.filme.infra.tmdbAPI.dtos.TMDBCredits;
import com.example.filme.infra.tmdbAPI.dtos.TMDBCrew;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class FilmePessoaServiceTest {

    @Mock
    private FilmePessoaRepository filmePessoaRepository;

    @Mock
    private MovieServiceTMDB movieServiceTMDB;

    @Mock
    private FilmeRepository filmeRepository;

    @Mock
    private PessoaRepository pessoaRepository;

    @Captor
    private ArgumentCaptor<FilmePessoa> filmePessoaCaptor;

    @Spy
    @InjectMocks
    private FilmePessoaService filmePessoaService;


    private FilmePessoa filmePessoa;
    private Pessoa pessoa;
    private Filme filme;

    @BeforeEach
    public void setUp() {
        filme = Mockito.mock(Filme.class);
        pessoa = Mockito.mock(Pessoa.class);
        filmePessoa = Mockito.mock(FilmePessoa.class);
    }


    @Test
    @DisplayName("Deve lançar exceção quando pessoa não existir")
    void deveLancarExcecaoSePessoaNaoExistir() {
        DadosCadastroFilmePessoa dados = Mockito.mock(DadosCadastroFilmePessoa.class);

        Mockito.when(dados.id_pessoa()).thenReturn(-2);

        Mockito.when(pessoaRepository.existsById(-2)).thenReturn(false);

        Throwable ex = catchThrowable(() -> filmePessoaService.adicionarPessoaAoFilme(dados));

        assertThat(ex).isInstanceOf(PessoaNotFoundException.class)
                .hasMessage("Pessoa com ID -2 não encontrada.");

        Mockito.verify(pessoaRepository).existsById(-2);
        verifyNoMoreInteractions(filmeRepository, filmePessoaRepository);
    }


    @Test
    @DisplayName("Deve lançar exceção quando filme não existir")
    void deveLancarExcecaoSeFilmeNaoExistir() {
        DadosCadastroFilmePessoa dados = Mockito.mock(DadosCadastroFilmePessoa.class);

        Mockito.when(dados.id_pessoa()).thenReturn(1);
        Mockito.when(dados.id_filme()).thenReturn(-2);

        Mockito.when(pessoaRepository.existsById(1)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(-2)).thenReturn(false);

        Throwable ex = catchThrowable(() -> filmePessoaService.adicionarPessoaAoFilme(dados));

        assertThat(ex).isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme com ID -2 não encontrado.");

        Mockito.verify(filmeRepository).existsById(-2);
        verifyNoMoreInteractions(filmeRepository, filmePessoaRepository);
    }

    @Test
    @DisplayName("Deve adicionar pessoa ao filme corretamente e salvar")
    void deveAdicionarPessoaAoFilmeComSucesso() {
        DadosCadastroFilmePessoa dados = Mockito.mock(DadosCadastroFilmePessoa.class);

        Mockito.when(dados.id_pessoa()).thenReturn(2);
        Mockito.when(dados.id_filme()).thenReturn(1);
        Mockito.when(dados.papel()).thenReturn(Papel.ATOR);

        Mockito.when(pessoaRepository.existsById(2)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(1)).thenReturn(true);

        Mockito.when(filmeRepository.getReferenceById(1)).thenReturn(filme);
        Mockito.when(pessoaRepository.getReferenceById(2)).thenReturn(pessoa);

        Mockito.when(filmePessoaRepository.save(Mockito.any(FilmePessoa.class))).thenAnswer(i -> i.getArgument(0));

        DadosDetalhamentoFilmePessoa resultado = filmePessoaService.adicionarPessoaAoFilme(dados);

        // Verificações
        Mockito.verify(pessoaRepository).existsById(2);
        Mockito.verify(filmeRepository).existsById(1);
        Mockito.verify(filmeRepository).getReferenceById(1);
        Mockito.verify(pessoaRepository).getReferenceById(2);
        Mockito.verify(filmePessoaRepository).save(filmePessoaCaptor.capture());

        FilmePessoa salvo = filmePessoaCaptor.getValue();

        assertNotNull(resultado);
        assertEquals(filme, salvo.getFilme());
        assertEquals(pessoa, salvo.getPessoa());
        assertEquals(Papel.ATOR, dados.papel());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar atores se o filme não existir")
    void deveLancarExcecaoAoBuscarAtoresQuandoFilmeNaoExistir() {
        Integer idFilme = 10;
        Pageable pageable = PageRequest.of(0, 5);

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(false);

        Throwable ex = catchThrowable(() -> filmePessoaService.buscarAtoresEmUmFilme(idFilme, pageable));

        assertThat(ex).isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme com ID 10 não encontrado.");

        Mockito.verify(filmeRepository).existsById(idFilme);
        verifyNoMoreInteractions(filmePessoaRepository);
    }

    @Test
    @DisplayName("Deve retornar atores corretamente ao buscar por filme existente")
    void deveRetornarAtoresDoFilme() {
        Integer idFilme = 1;
        Pageable pageable = PageRequest.of(0, 10);

        FilmePessoa filmePessoa = Mockito.mock(FilmePessoa.class);
        Pessoa pessoa = Mockito.mock(Pessoa.class);

        Integer idPessoa = 99;
        String nome = "Keanu Reeves";
        String origem = "CAN";
        Tipo tipo = Tipo.ATOR;
        LocalDate dataNascimento = LocalDate.of(1964, 9, 2);

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(true);
        Mockito.when(filmePessoa.getPessoa()).thenReturn(pessoa);

        Mockito.when(pessoa.getId()).thenReturn(idPessoa);
        Mockito.when(pessoa.getNome()).thenReturn(nome);
        Mockito.when(pessoa.getOrigem()).thenReturn(origem);
        Mockito.when(pessoa.getTipo()).thenReturn(tipo);
        Mockito.when(pessoa.getDataNascimento()).thenReturn(dataNascimento);

        Page<FilmePessoa> page = new PageImpl<>(List.of(filmePessoa));
        Mockito.when(filmePessoaRepository.findByFilmeIdAndPapel(idFilme, Papel.ATOR, pageable)).thenReturn(page);

        // Execução
        Page<DadosDetalhamentoPessoa> resultado = filmePessoaService.buscarAtoresEmUmFilme(idFilme, pageable);

        // Verificações
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

        DadosDetalhamentoPessoa dto = resultado.getContent().get(0);
        assertEquals(idPessoa, dto.id());
        assertEquals(nome, dto.nome());
        assertEquals(origem, dto.origem());
        assertEquals(tipo.toString(), dto.tipo());
        assertEquals(dataNascimento, dto.dt_nascimento());
    }

    @Test
    @DisplayName("Deve lançar exceção se o filme não existir ao buscar diretores")
    void deveLancarExcecaoSeFilmeNaoExistirAoBuscarDiretores() {
        Integer idFilme = -999;
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(false);

        Throwable ex = catchThrowable(() -> filmePessoaService.buscarDiretoresEmUmFilme(idFilme, pageable));

        assertThat(ex).isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme com ID -999 não encontrado.");

        Mockito.verify(filmeRepository).existsById(idFilme);
        verifyNoMoreInteractions(filmePessoaRepository);
    }

    @Test
    @DisplayName("Deve retornar diretores corretamente ao buscar por filme existente")
    void deveRetornarDiretoresDoFilme() {
        Integer idFilme = 1;
        Pageable pageable = PageRequest.of(0, 10);

        FilmePessoa filmePessoa = Mockito.mock(FilmePessoa.class);
        Pessoa pessoa = Mockito.mock(Pessoa.class);

        Integer idPessoa = 10;
        String nome = "Christopher Nolan";
        String origem = "ENG";
        Tipo tipo = Tipo.DIRETOR;
        LocalDate nascimento = LocalDate.of(1970, 7, 30);

        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(true);
        Mockito.when(filmePessoa.getPessoa()).thenReturn(pessoa);
        Mockito.when(pessoa.getId()).thenReturn(idPessoa);
        Mockito.when(pessoa.getNome()).thenReturn(nome);
        Mockito.when(pessoa.getOrigem()).thenReturn(origem);
        Mockito.when(pessoa.getTipo()).thenReturn(tipo);
        Mockito.when(pessoa.getDataNascimento()).thenReturn(nascimento);

        Page<FilmePessoa> page = new PageImpl<>(List.of(filmePessoa));
        Mockito.when(filmePessoaRepository.findByFilmeIdAndPapel(idFilme, Papel.DIRETOR, pageable)).thenReturn(page);

        // Execução
        Page<DadosDetalhamentoPessoa> resultado = filmePessoaService.buscarDiretoresEmUmFilme(idFilme, pageable);

        // Verificações
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

        DadosDetalhamentoPessoa dto = resultado.getContent().get(0);
        assertEquals(idPessoa, dto.id());
        assertEquals(nome, dto.nome());
        assertEquals(origem, dto.origem());
        assertEquals(tipo.toString(), dto.tipo());
        assertEquals(nascimento, dto.dt_nascimento());
    }

    @Test
    @DisplayName("Deve lançar exceção quando dados forem nulos ao alterar papel")
    void deveLancarExcecaoQuandoDadosForemNulos() {
        Throwable ex = catchThrowable(() -> filmePessoaService.alterarPapelEmFilme(null));

        assertThat(ex).isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("Dados para alteração de papel são inválidos ou nulos.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando pessoa não existir ao alterar papel")
    void deveLancarExcecaoQuandoPessoaNaoExiste() {
        FilmePessoaId id = new FilmePessoaId(10, 20);
        DadosAlterarFilmePessoa dados = new DadosAlterarFilmePessoa(id, Papel.ATOR);

        Mockito.when(pessoaRepository.existsById(10)).thenReturn(false);

        Throwable ex = catchThrowable(() -> filmePessoaService.alterarPapelEmFilme(dados));

        assertThat(ex).isInstanceOf(PessoaNotFoundException.class)
                .hasMessage("Pessoa com ID 10 não encontrada.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando filme não existir ao alterar papel")
    void deveLancarExcecaoQuandoFilmeNaoExiste() {
        FilmePessoaId id = new FilmePessoaId(10, 20);
        DadosAlterarFilmePessoa dados = new DadosAlterarFilmePessoa(id, Papel.ATOR);

        Mockito.when(pessoaRepository.existsById(10)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(20)).thenReturn(false);

        Throwable ex = catchThrowable(() -> filmePessoaService.alterarPapelEmFilme(dados));

        assertThat(ex).isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme com ID 20 não encontrado.");
    }

    @Test
    @DisplayName("Deve lançar exceção se papel já estiver associado")
    void deveLancarExcecaoSePapelJaAssociado() {
        FilmePessoaId id = new FilmePessoaId(10, 20);
        DadosAlterarFilmePessoa dados = new DadosAlterarFilmePessoa(id, Papel.ATOR);

        Mockito.when(pessoaRepository.existsById(10)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(20)).thenReturn(true);

        Mockito.doThrow(new RuntimeException("Pessoa já associada com este papel"))
                .when(filmePessoaService)
                .validarPessoaJaAssociadaAoFilme(20, 10, Papel.ATOR);

        Throwable ex = catchThrowable(() -> filmePessoaService.alterarPapelEmFilme(dados));

        assertThat(ex).isInstanceOf(RuntimeException.class)
                .hasMessage("Pessoa já associada com este papel");
    }

    @Test
    @DisplayName("Não deve alterar o papel se papel for null")
    void naoDeveAlterarPapelSePapelForNull() {
        FilmePessoaId id = new FilmePessoaId(10, 20);
        DadosAlterarFilmePessoa dados = new DadosAlterarFilmePessoa(id, null);

        FilmePessoa filmePessoa = Mockito.mock(FilmePessoa.class);
        Pessoa pessoa = Mockito.mock(Pessoa.class);
        Filme filme = Mockito.mock(Filme.class);

        // Configura mocks para evitar NullPointerException no construtor do DTO
        Mockito.when(filmePessoa.getPessoa()).thenReturn(pessoa);
        Mockito.when(pessoa.getId()).thenReturn(10);
        Mockito.when(pessoa.getNome()).thenReturn("Fulano");

        Mockito.when(filmePessoa.getFilme()).thenReturn(filme);
        Mockito.when(filme.getId()).thenReturn(20);
        Mockito.when(filme.getNome()).thenReturn("Filme X");

        // ESSA LINHA PREVINE O NullPointerException
        Mockito.when(filmePessoa.getPapel()).thenReturn(Papel.ATOR);

        Mockito.when(pessoaRepository.existsById(10)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(20)).thenReturn(true);

        Mockito.doNothing().when(filmePessoaService)
                .validarPessoaJaAssociadaAoFilme(20, 10, null);
        Mockito.when(filmePessoaRepository.getReferenceById(id)).thenReturn(filmePessoa);

        DadosDetalhamentoFilmePessoa resultado = filmePessoaService.alterarPapelEmFilme(dados);

        assertNotNull(resultado);
        Mockito.verify(filmePessoa, Mockito.never()).setPapel(Mockito.any());
        Mockito.verify(filmePessoaRepository).getReferenceById(id);
    }

    @Test
    @DisplayName("Deve alterar o papel da pessoa no filme com sucesso")
    void deveAlterarPapelComSucesso() {
        FilmePessoaId id = new FilmePessoaId(10, 20);
        DadosAlterarFilmePessoa dados = new DadosAlterarFilmePessoa(id, Papel.DIRETOR);

        FilmePessoa filmePessoa = Mockito.mock(FilmePessoa.class);
        Pessoa pessoa = Mockito.mock(Pessoa.class);
        Filme filme = Mockito.mock(Filme.class);

        Mockito.when(pessoa.getId()).thenReturn(10);
        Mockito.when(pessoa.getNome()).thenReturn("Fulano");

        Mockito.when(filme.getId()).thenReturn(20);
        Mockito.when(filme.getNome()).thenReturn("Filme X");

        Mockito.when(filmePessoa.getPessoa()).thenReturn(pessoa);
        Mockito.when(filmePessoa.getFilme()).thenReturn(filme);
        Mockito.when(filmePessoa.getPapel()).thenReturn(Papel.DIRETOR);

        Mockito.when(pessoaRepository.existsById(10)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(20)).thenReturn(true);

        Mockito.doNothing().when(filmePessoaService)
                .validarPessoaJaAssociadaAoFilme(20, 10, Papel.DIRETOR);

        Mockito.when(filmePessoaRepository.getReferenceById(id)).thenReturn(filmePessoa);

        // Ação
        DadosDetalhamentoFilmePessoa resultado = filmePessoaService.alterarPapelEmFilme(dados);

        // Verificações
        assertNotNull(resultado);
        assertEquals(10, resultado.idPessoa());
        assertEquals("Fulano", resultado.nomePessoa());
        assertEquals(20, resultado.idFilme());
        assertEquals("Filme X", resultado.nomeFilme());
        assertEquals("DIRETOR", resultado.papel());

        Mockito.verify(filmePessoa).setPapel(Papel.DIRETOR);
        Mockito.verify(filmePessoaRepository).getReferenceById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pessoa não existir")
    void deveLancarExcecaoSePessoaNaoExistirAoDeletar() {
        Integer idPessoa = 10;
        Integer idFilme = 20;

        Mockito.when(pessoaRepository.existsById(idPessoa)).thenReturn(false);

        Throwable ex = catchThrowable(() -> filmePessoaService.deletarPessoaDeFilme(idPessoa, idFilme));

        assertThat(ex).isInstanceOf(PessoaNotFoundException.class)
                .hasMessage("Pessoa com ID 10 não encontrada.");

        Mockito.verify(pessoaRepository).existsById(idPessoa);
        verifyNoMoreInteractions(filmeRepository, filmePessoaRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando filme não existir")
    void deveLancarExcecaoSeFilmeNaoExistirAoDeletar() {
        Integer idPessoa = 10;
        Integer idFilme = 20;

        Mockito.when(pessoaRepository.existsById(idPessoa)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(false);

        Throwable ex = catchThrowable(() -> filmePessoaService.deletarPessoaDeFilme(idPessoa, idFilme));

        assertThat(ex).isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme com ID 20 não encontrado.");

        Mockito.verify(pessoaRepository).existsById(idPessoa);
        Mockito.verify(filmeRepository).existsById(idFilme);
        verifyNoMoreInteractions(filmePessoaRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se pessoa não estiver associada ao filme")
    void deveLancarExcecaoSePessoaNaoAssociadaAoFilme() {
        Integer idPessoa = 10;
        Integer idFilme = 20;

        Mockito.when(pessoaRepository.existsById(idPessoa)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(true);
        Mockito.when(filmePessoaRepository.findByFilmeIdAndPessoaId(idFilme, idPessoa)).thenReturn(List.of());

        Throwable ex = catchThrowable(() -> filmePessoaService.deletarPessoaDeFilme(idPessoa, idFilme));

        assertThat(ex).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Essa pessoa não está associada a este filme.");

        Mockito.verify(pessoaRepository).existsById(idPessoa);
        Mockito.verify(filmeRepository).existsById(idFilme);
        Mockito.verify(filmePessoaRepository).findByFilmeIdAndPessoaId(idFilme, idPessoa);
        verifyNoMoreInteractions(filmePessoaRepository);
    }

    @Test
    @DisplayName("Deve deletar as relações quando pessoa estiver associada ao filme")
    void deveDeletarPessoaDeFilmeComSucesso() {
        Integer idPessoa = 10;
        Integer idFilme = 20;

        FilmePessoa rel1 = Mockito.mock(FilmePessoa.class);
        FilmePessoa rel2 = Mockito.mock(FilmePessoa.class);

        List<FilmePessoa> relacoes = List.of(rel1, rel2);

        Mockito.when(pessoaRepository.existsById(idPessoa)).thenReturn(true);
        Mockito.when(filmeRepository.existsById(idFilme)).thenReturn(true);
        Mockito.when(filmePessoaRepository.findByFilmeIdAndPessoaId(idFilme, idPessoa)).thenReturn(relacoes);

        filmePessoaService.deletarPessoaDeFilme(idPessoa, idFilme);

        Mockito.verify(pessoaRepository).existsById(idPessoa);
        Mockito.verify(filmeRepository).existsById(idFilme);
        Mockito.verify(filmePessoaRepository).findByFilmeIdAndPessoaId(idFilme, idPessoa);
        Mockito.verify(filmePessoaRepository).deleteAll(relacoes);
    }

    @Test
    @DisplayName("Deve lançar exceção se pessoa já estiver associada ao filme com mesmo papel")
    void deveLancarExcecaoSePessoaJaAssociadaAoFilme() {
        Integer filmeId = 1;
        Integer pessoaId = 2;
        Papel papel = Papel.ATOR;

        // Simula que já existe associação com esse papel
        Mockito.when(filmePessoaRepository.existsByFilmeIdAndPessoaIdAndPapel(filmeId, pessoaId, papel))
                .thenReturn(true);

        Throwable ex = catchThrowable(() ->
                filmePessoaService.validarPessoaJaAssociadaAoFilme(filmeId, pessoaId, papel));

        assertThat(ex)
                .isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("Pessoa já está associada como " + papel + " neste filme.");

        Mockito.verify(filmePessoaRepository).existsByFilmeIdAndPessoaIdAndPapel(filmeId, pessoaId, papel);
    }

    @Test
    @DisplayName("Deve lançar exceção quando filmeId for nulo")
    void deveLancarExcecaoSeFilmeIdForNulo() {
        Throwable ex = catchThrowable(() -> filmePessoaService.importarAtoresEDiretoresDoFilme(null));
        assertThat(ex).isInstanceOf(FilmeBadRequestException.class)
                .hasMessage("ID do filme não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando filme não existir")
    void deveLancarExcecaoSeFilmeNaoExistirAoImportarAtores() {
        Integer filmeId = 1;
        Mockito.when(filmeRepository.findById(filmeId)).thenReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> filmePessoaService.importarAtoresEDiretoresDoFilme(filmeId));
        assertThat(ex).isInstanceOf(FilmeNotFoundException.class)
                .hasMessage("Filme com ID " + filmeId + " não encontrado.");
    }

    @Test
    @DisplayName("Deve importar atores e diretores corretamente")
    void deveImportarAtoresEDiretores() {
        // ARRANGE
        Integer filmeId = 1;
        Integer tmdbId = 12345;
        Filme filme = new Filme(filmeId, "Filme Teste", null, null, null, null, null, tmdbId);

        // Mock: Filme existe
        Mockito.when(filmeRepository.findById(filmeId)).thenReturn(Optional.of(filme));

        // Mock: créditos da API TMDB
// Mock: créditos da API TMDB
        TMDBCast cast1 = new TMDBCast();
        cast1.name = "Ator Um";

        TMDBCast cast2 = new TMDBCast();
        cast2.name = "Ator Dois";

        TMDBCrew crew1 = new TMDBCrew();
        crew1.name = "Diretor Um";
        crew1.job = "Director";

        TMDBCrew crew2 = new TMDBCrew();
        crew2.name = "Produtor X";
        crew2.job = "Producer";


        TMDBCredits credits = Mockito.mock(TMDBCredits.class);
        Mockito.when(credits.getCast()).thenReturn(List.of(cast1, cast2));
        Mockito.when(credits.getCrew()).thenReturn(List.of(crew1, crew2));
        Mockito.when(movieServiceTMDB.buscarCreditosDoFilme(tmdbId)).thenReturn(credits);

        // Mock: PessoaRepository
        Pessoa atorExistente = new Pessoa(10, "Ator Um", Tipo.ATOR);
        Pessoa diretorExistente = new Pessoa(20, "Diretor Um", Tipo.DIRETOR);

        Mockito.when(pessoaRepository.findByNomeIgnoreCase("Ator Um"))
                .thenReturn(Optional.of(atorExistente));
        Mockito.when(pessoaRepository.findByNomeIgnoreCase("Ator Dois"))
                .thenReturn(Optional.empty());
        Mockito.when(pessoaRepository.findByNomeIgnoreCase("Diretor Um"))
                .thenReturn(Optional.of(diretorExistente));

        // Mock: salvar nova pessoa
        Mockito.when(pessoaRepository.save(Mockito.any(Pessoa.class)))
                .thenAnswer(invocation -> {
                    Pessoa p = invocation.getArgument(0);
                    return new Pessoa(11, p.getNome(), p.getTipo()); // simula ID atribuído
                });

        // Mock: salvar FilmePessoa
        Mockito.when(filmePessoaRepository.save(Mockito.any(FilmePessoa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        filmePessoaService.importarAtoresEDiretoresDoFilme(filmeId);

        // ASSERT — Atores
        Mockito.verify(pessoaRepository).findByNomeIgnoreCase("Ator Um");
        Mockito.verify(pessoaRepository).findByNomeIgnoreCase("Ator Dois");
        Mockito.verify(pessoaRepository).save(Mockito.argThat(p ->
                "Ator Dois".equals(p.getNome()) && p.getTipo() == Tipo.ATOR
        ));

        // ASSERT — Diretores
        Mockito.verify(pessoaRepository).findByNomeIgnoreCase("Diretor Um");
        Mockito.verify(pessoaRepository, Mockito.never()).findByNomeIgnoreCase("Produtor X");

        // Verifica se foram feitas as relações no filmePessoaRepository
        Mockito.verify(filmePessoaRepository, Mockito.atLeast(2)).save(Mockito.any(FilmePessoa.class));
    }

}