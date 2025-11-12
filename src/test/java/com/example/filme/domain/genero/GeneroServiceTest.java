package com.example.filme.domain.genero;

import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import com.example.filme.domain.genero.dtos.DadosDetalhamentoGenero;
import com.example.filme.domain.genero.dtos.DadosEditarGenero;
import com.example.filme.domain.genero.exceptions.GeneroBadRequestException;
import com.example.filme.domain.genero.exceptions.GeneroNotFoundException;
import com.example.filme.infra.tmdbAPI.MovieServiceTMDB;
import com.example.filme.infra.tmdbAPI.dtos.TMDBGenre;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class GeneroServiceTest {

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private MovieServiceTMDB movieServiceTMDB;

    @Captor
    private ArgumentCaptor<DadosDetalhamentoGenero> captor;

    @InjectMocks
    private GeneroService generoService;

    @Test
    void deveriaAdicionarGenero() {
        // Arrange
        DadosCadastrarGenero dados = new DadosCadastrarGenero("Ação");

        ArgumentCaptor<Genero> captor = ArgumentCaptor.forClass(Genero.class);

        // Act
        DadosDetalhamentoGenero resultado = generoService.adicionarGenero(dados);

        // Assert
        Mockito.verify(generoRepository, times(1)).save(captor.capture());
        Genero generoSalvo = captor.getValue();

        assertNotNull(resultado);
        assertEquals("Ação", generoSalvo.getNome());
        assertEquals("Ação", resultado.nome());
    }

    @Test
    void deveriaLancarExcecaoQuandoDadosAdicionarForemNulos() {
        // Arrange
        DadosCadastrarGenero dados = null;

        // Act + Assert
        GeneroBadRequestException exception = assertThrows(
                GeneroBadRequestException.class,
                () -> generoService.adicionarGenero(dados)
        );

        assertEquals("Dados para cadastrar não devem ser nulos.", exception.getMessage());
    }

    @Test
    void deveriaImportarGenerosDaApi() {
        // Arrange
        TMDBGenre generoApi1 = new TMDBGenre();
        generoApi1.setName("Ação");

        TMDBGenre generoApi2 = new TMDBGenre();
        generoApi2.setName("Comédia");

        List<TMDBGenre> generosDaApi = List.of(generoApi1, generoApi2);

        Mockito.when(movieServiceTMDB.buscarGeneros()).thenReturn(generosDaApi);

        Mockito.when(generoRepository.existsByNomeIgnoreCase("Ação")).thenReturn(false);
        Mockito.when(generoRepository.existsByNomeIgnoreCase("Comédia")).thenReturn(false);

        // Act
        generoService.importarGenerosDaApi();

        // Assert
        ArgumentCaptor<List<Genero>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(generoRepository).saveAll(captor.capture());

        List<Genero> generosSalvos = captor.getValue();
        assertEquals(2, generosSalvos.size());
        assertEquals("Ação", generosSalvos.get(0).getNome());
        assertEquals("Comédia", generosSalvos.get(1).getNome());

        Mockito.verify(generoRepository).saveAll(Mockito.anyList());
    }

    @Test
    void deveriaListarTodosOsGeneros() {
        // Arrange
        List<Genero> generos = List.of(new Genero(1, "Ação"), new Genero(2, "Drama"));

        Mockito.when(generoRepository.findAll()).thenReturn(generos);

        // Act
        List<DadosDetalhamentoGenero> resultado = generoService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Ação", resultado.get(0).nome());
        assertEquals("Drama", resultado.get(1).nome());
    }

    @Test
    void deveriaEditarUmGenero() {
        // Arrange
        Genero generoExistente = new Genero(1, "Aventura");
        DadosEditarGenero dados = new DadosEditarGenero(1, "Ação");

        Mockito.when(generoRepository.findById(1))
                .thenReturn(Optional.of(generoExistente));

        Mockito.when(generoRepository.findByNomeIgnoreCase("Ação"))
                .thenReturn(Optional.empty());

        Genero generoEditado = new Genero(1, "Ação");
        Mockito.when(generoRepository.save(Mockito.any(Genero.class)))
                .thenReturn(generoEditado);

        // Act
        DadosDetalhamentoGenero resultado = generoService.editarGenero(dados);

        // Assert
        assertEquals("Ação", resultado.nome());
        assertEquals(1, resultado.id());

        Mockito.verify(generoRepository).save(Mockito.argThat(g -> g.getNome().equals("Ação")));
    }

    @Test
    void deveriaLancarExcecaoQuandoDadosEditarForemNulos() {
        // Arrange
        DadosEditarGenero dados = null;

        // Act + Assert
        GeneroBadRequestException exception = assertThrows(
                GeneroBadRequestException.class,
                () -> generoService.editarGenero(dados)
        );

        assertEquals("Dados para alterar não devem ser nulos.", exception.getMessage());
    }


}