package com.example.filme.domain.filme;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FilmeRepository extends JpaRepository<Filme, Integer> {
    Optional<Filme> findByTmdbId(Integer id);

    @Query(value = """
            SELECT DISTINCT f.*
            FROM filme f
            JOIN avaliacao a ON a.nota >= 3.5 AND a.usuario_id = :idUsuario
            LEFT JOIN filme f_avaliado ON f_avaliado.id = a.filme_id AND f.genero_id = f_avaliado.genero_id
            LEFT JOIN filme_pessoa fp_f ON fp_f.filme_id = f.id
            LEFT JOIN filme_pessoa fp_a ON fp_a.filme_id = a.filme_id AND fp_a.pessoa_id = fp_f.pessoa_id
            WHERE f.id NOT IN (
                SELECT av.filme_id FROM avaliacao av WHERE av.usuario_id = :idUsuario
            )
            AND (
                f.genero_id = f_avaliado.genero_id OR fp_a.pessoa_id IS NOT NULL
            )
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT f.id)
                    FROM filme f
                    JOIN avaliacao a ON a.nota >= 3.5 AND a.usuario_id = :idUsuario
                    LEFT JOIN filme f_avaliado ON f_avaliado.id = a.filme_id AND f.genero_id = f_avaliado.genero_id
                    LEFT JOIN filme_pessoa fp_f ON fp_f.filme_id = f.id
                    LEFT JOIN filme_pessoa fp_a ON fp_a.filme_id = a.filme_id AND fp_a.pessoa_id = fp_f.pessoa_id
                    WHERE f.id NOT IN (
                        SELECT av.filme_id FROM avaliacao av WHERE av.usuario_id = :idUsuario
                    )
                    AND (
                        f.genero_id = f_avaliado.genero_id OR fp_a.pessoa_id IS NOT NULL
                    )
                    """,
            nativeQuery = true)
    List<Filme> recomendarFilmesParecidos(@Param("idUsuario") Integer idUsuario);

}