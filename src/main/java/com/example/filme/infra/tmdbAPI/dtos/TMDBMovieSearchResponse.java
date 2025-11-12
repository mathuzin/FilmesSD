package com.example.filme.infra.tmdbAPI.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TMDBMovieSearchResponse {

    @JsonProperty("results")
    private List<TMDBMovieResult> results;

    @JsonProperty("total_results")
    private long totalResults;

    @JsonProperty("page")
    private int page;

    @JsonProperty("total_pages")
    private int totalPages;
}
