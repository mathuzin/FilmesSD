package com.example.filme.domain.filme_pessoa;

import com.example.filme.domain.filme.Filme;
import com.example.filme.domain.pessoa.Pessoa;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilmePessoaRepository extends JpaRepository<FilmePessoa, FilmePessoaId> {

    Page<FilmePessoa> findByFilmeIdAndPapel(Integer filmeId, Papel papel, Pageable pageable);

    boolean existsByFilmeIdAndPessoaIdAndPapel(@NotNull Integer idFilme, @NotNull Integer idPessoa, @NotNull Papel papel);

    List<FilmePessoa> findByFilmeIdAndPessoaId(Integer idFilme, Integer idPessoa);
}