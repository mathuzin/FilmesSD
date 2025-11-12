package com.example.filme.domain.genero;

import com.example.filme.domain.genero.dtos.DadosCadastrarGenero;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "genero")
@Entity(name = "Genero")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(unique = true)
    private String nome;

    public Genero(DadosCadastrarGenero dados) {
        this.nome = dados.nome();
    }
}
