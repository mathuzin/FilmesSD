package com.example.filme.domain.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    UserDetails findByLogin(String login);

    Page<Usuario> findByAtivoTrue(Pageable paginacao);

    Optional<Usuario> findByLoginIgnoreCase(String login);
}
