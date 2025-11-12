package com.example.filme.infra.tmdbAPI.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class TMDBMovieDetail {
    public Integer id;
    public String title;
    public String poster_path;
    public String overview;
    public String release_date;
    public List<TMDBGenre> genres;
}

