package com.example.filme.infra.tmdbAPI.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class TMDBCredits {
    public List<TMDBCast> cast;
    public List<TMDBCrew> crew;
}

