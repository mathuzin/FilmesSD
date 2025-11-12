package com.example.filme.infra.tmdbAPI.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TMDBGenresResponse {
    private List<TMDBGenre> genres;
}
