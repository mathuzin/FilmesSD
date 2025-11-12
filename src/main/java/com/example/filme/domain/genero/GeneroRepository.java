package com.example.filme.domain.genero;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneroRepository extends JpaRepository<Genero, Integer> {
    Optional<Genero> findByNomeIgnoreCase(String nomeGenero);

    boolean existsByNomeIgnoreCase(String name);
}
