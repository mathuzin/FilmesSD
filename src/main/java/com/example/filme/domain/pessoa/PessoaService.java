package com.example.filme.domain.pessoa;

import com.example.filme.domain.pessoa.dtos.*;
import com.example.filme.domain.pessoa.exceptions.PessoaBadRequestException;
import com.example.filme.domain.pessoa.exceptions.PessoaNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    // Cadastra uma pessoa
    @Transactional
    public Pessoa cadastrarPessoa(@Valid DadosCadastroPessoa dados) {
        if (dados == null) {
            throw new PessoaBadRequestException("Dados não podem ser nulos.");
        }

        Pessoa pessoa = new Pessoa(dados);
        return pessoaRepository.save(pessoa);
    }

    // Lista todas as pessoas no banco de dados
    public Page<DadosDetalhamentoPessoa> listarTodasAsPessoas(Pageable paginacao) {
        return pessoaRepository.findAll(paginacao)
                .map(DadosDetalhamentoPessoa::new);
    }

    // Busca uma pessoa específica do banco de dados
    public DadosDetalhamentoPessoa listarPessoa(Integer idPessoa) {
        if (idPessoa == null) {
            throw new PessoaBadRequestException("ID não pode ser nulo.");
        }

        Pessoa pessoa = pessoaRepository.findById(idPessoa)
                .orElseThrow(() -> new PessoaNotFoundException("Pessoa não encontrada com ID: " + idPessoa));

        return new DadosDetalhamentoPessoa(pessoa);
    }

    // Altera uma pessoa do banco de dados
    @Transactional
    public DadosDetalhamentoPessoa alterarPessoa(@Valid DadosAlterarPessoa dados) {
        if (dados == null) {
            throw new PessoaBadRequestException("Dados não podem ser nulos.");
        }

        Pessoa pessoaAlterada = pessoaRepository.findById(dados.id())
                .orElseThrow(() -> new PessoaNotFoundException("Pessoa não encontrada com ID: " + dados.id()));

        if (dados.nome() != null) {
            pessoaAlterada.setNome(dados.nome());
        }

        if (dados.tipo() != null) {
            pessoaAlterada.setTipo(dados.tipo());
        }

        pessoaRepository.save(pessoaAlterada);

        return new DadosDetalhamentoPessoa(pessoaAlterada);
    }

}
