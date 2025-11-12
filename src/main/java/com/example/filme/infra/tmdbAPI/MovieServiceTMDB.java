package com.example.filme.infra.tmdbAPI;

import com.example.filme.infra.tmdbAPI.dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MovieServiceTMDB {

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final String BASE_URL = "https://api.themoviedb.org/3";

    @Autowired
    private RestTemplate restTemplate;

    private long totalResults = 0;

    public TMDBMovieSearchResponse buscarFilmePorNome(String nome) {
        String url = BASE_URL + "/search/movie?query={nome}&language=pt-BR&api_key={apiKey}";
        return restTemplate.getForObject(url, TMDBMovieSearchResponse.class, nome, apiKey);
    }

    public TMDBMovieDetail buscarDetalhesDoFilme(Integer movieId) {
        String url = BASE_URL + "/movie/{id}?language=pt-BR&api_key={apiKey}";
        return restTemplate.getForObject(url, TMDBMovieDetail.class, movieId, apiKey);
    }

    public TMDBCredits buscarCreditosDoFilme(Integer movieId) {
        String url = BASE_URL + "/movie/{id}/credits?language=pt-BR&api_key={apiKey}";
        return restTemplate.getForObject(url, TMDBCredits.class, movieId, apiKey);
    }

    public TMDBMovieDetail buscarFilmePorId(Integer idFilme) {
        return buscarFilmePorId(idFilme);
    }

    // Busca filmes populares por página — você pode ajustar para buscar filmes populares ou outro endpoint
    public List<TMDBMovieDetail> buscarFilmesPorPagina(int paginaAtual) {
        TMDBMovieSearchResponse response = restTemplate.getForObject(
                BASE_URL + "/movie/popular?language=pt-BR&page={pagina}&api_key={apiKey}",
                TMDBMovieSearchResponse.class,
                paginaAtual,
                apiKey
        );

        assert response != null;
        List<TMDBMovieResult> resultados = response.getResults();

        // Agora para cada resultado, buscar o detalhe completo
        List<TMDBMovieDetail> detalhes = resultados.stream()
                .map(r -> buscarDetalhesDoFilme(r.getId()))
                .toList();

        return detalhes;
    }

    public List<TMDBGenre> buscarGeneros() {
        String url = BASE_URL + "/genre/movie/list?language=pt-BR&api_key={apiKey}";
        TMDBGenresResponse response = restTemplate.getForObject(url, TMDBGenresResponse.class, apiKey);
        return response != null ? response.getGenres() : List.of();
    }


    // Retorna o total de resultados da última busca feita — para paginação
    public long totalResultados() {
        return totalResults;
    }
}

