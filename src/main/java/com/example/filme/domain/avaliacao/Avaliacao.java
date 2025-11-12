package com.example.filme.domain.avaliacao;

import com.example.filme.domain.avaliacao.dtos.DadosCadastroAvaliacao;
import com.example.filme.domain.filme.Filme;
import com.example.filme.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "avaliacao")
@Entity(name = "Avaliacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Float nota;

    @ManyToOne
    @JoinColumn(name = "filme_id")
    private Filme filme;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String ds_avaliacao;

    public Avaliacao(DadosCadastroAvaliacao dados, Filme filme, Usuario usuario) {
        this.nota = dados.nota();
        this.filme = filme;
        this.usuario = usuario;
        this.ds_avaliacao = dados.ds_avaliacao();
    }
}
