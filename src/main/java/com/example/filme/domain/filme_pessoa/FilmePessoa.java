package com.example.filme.domain.filme_pessoa;

import com.example.filme.domain.filme.Filme;
import com.example.filme.domain.filme_pessoa.dtos.DadosCadastroFilmePessoa;
import com.example.filme.domain.pessoa.Pessoa;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "filme_pessoa", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"filme_id", "pessoa_id", "papel"})
})
@Entity(name = "Filme_Pessoa")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class FilmePessoa {

    @EmbeddedId
    private FilmePessoaId id;

    @ManyToOne
    @MapsId("pessoaId")
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    @ManyToOne
    @MapsId("filmeId")
    @JoinColumn(name = "filme_id")
    private Filme filme;

    @Setter
    @Enumerated(EnumType.STRING)
    private Papel papel;

    public FilmePessoa(DadosCadastroFilmePessoa dados, Pessoa pessoa, Filme filme) {
        this.id = new FilmePessoaId(pessoa.getId(), filme.getId());
        this.pessoa = pessoa;
        this.filme = filme;
        this.papel = dados.papel();
    }

}
