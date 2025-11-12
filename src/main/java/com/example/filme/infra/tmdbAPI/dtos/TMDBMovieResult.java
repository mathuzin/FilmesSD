package com.example.filme.infra.tmdbAPI.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TMDBMovieResult {
    public Integer id;
    public String title;
    public String overview;
    public String release_date;
}

