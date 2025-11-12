package com.example.filme.domain.pessoa;

import com.example.filme.domain.pessoa.dtos.DadosCadastroPessoa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "pessoa")
@Entity(name = "Pessoa")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    private String nome;

    @Setter
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Setter
    @Column(length = 3)
    private String origem;


    @Setter
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    public Pessoa(DadosCadastroPessoa dados) {
        this.nome = dados.nome();
        this.tipo = dados.tipo();
        this.dataNascimento = dados.dataNascimento();
        this.origem = dados.origem();
    }

    public Pessoa(Object o, String name, Tipo tipo) {
        this.nome = name;
        this.tipo = tipo;
    }
}
