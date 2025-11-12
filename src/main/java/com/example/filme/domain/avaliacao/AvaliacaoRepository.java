package com.example.filme.domain.avaliacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {
    List<Avaliacao> findByUsuarioId(Integer usuario_id);

    List<Avaliacao> findByFilmeId(Integer filme_id);

    Optional<Avaliacao> findByFilmeIdAndUsuarioId(Integer filmeId, Integer usuarioId);

    boolean existsByFilmeIdAndUsuarioId(Integer filmeId, Integer usuarioId);
}
