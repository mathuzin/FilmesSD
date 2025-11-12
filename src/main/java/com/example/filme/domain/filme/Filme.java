package com.example.filme.domain.filme;

import com.example.filme.domain.filme.dtos.DadosAlterarFilme;
import com.example.filme.domain.filme.dtos.DadosCadastrarFilme;
import com.example.filme.domain.filme.dtos.DadosDetalhamentoFilme;
import com.example.filme.domain.genero.Genero;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

@Table(name = "filme")
@Entity(name = "Filme")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(nullable = false)
    private String nome;

    @Setter
    private Float popularidade;

    @Setter
    @ManyToOne
    @JoinColumn(name = "genero_id")
    private Genero genero;

    @Setter
    @Column(name = "poster_url")
    private String posterUrl;

    @Setter
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Setter
    @Column(name = "ano")
    private Integer ano;

    @Setter
    @Column(name = "tmdb_id", unique = true)
    private Integer tmdbId;


    public Filme(DadosCadastrarFilme dados) {
        this.nome = dados.nome();
        this.popularidade = 0f;
        this.posterUrl = dados.posterUrl();
        this.descricao = dados.descricao();
        this.ano = dados.ano();
    }

}