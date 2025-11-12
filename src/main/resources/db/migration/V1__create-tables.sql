-- Table: genero
CREATE TABLE genero (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

-- Table: pessoa
CREATE TABLE pessoa (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('ATOR', 'DIRETOR', 'USUARIO', 'ATOR_DIRETOR')),
    data_nascimento DATE,
    origem VARCHAR(3)
);

-- Table: usuario
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    pessoa_id INT NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL,
    senha VARCHAR(50) NOT NULL,
    perfil VARCHAR(20) NOT NULL CHECK (perfil IN ('COMUM', 'ADM')),
    ativo BOOLEAN DEFAULT TRUE,
    CONSTRAINT usuario_pessoa_fk FOREIGN KEY (pessoa_id) REFERENCES pessoa(id)
);

-- Table: filme
CREATE TABLE filme (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    popularidade DECIMAL(4,2) NOT NULL DEFAULT 0,
    poster_url VARCHAR(500),
    descricao TEXT,
    ano INT,
    tmdb_id INT UNIQUE,
    genero_id INT,
    CONSTRAINT filme_genero_fk FOREIGN KEY (genero_id) REFERENCES genero(id)
);

-- Table: avaliacao
CREATE TABLE avaliacao (
    id SERIAL PRIMARY KEY,
    nota DECIMAL(4,2) NOT NULL CHECK (nota >= 0 AND nota <= 5),
    ds_avaliacao TEXT,
    filme_id INT NOT NULL,
    usuario_id INT NOT NULL,
    CONSTRAINT avaliacao_filme_fk FOREIGN KEY (filme_id) REFERENCES filme(id),
    CONSTRAINT avaliacao_usuario_fk FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT unique_avaliacao_usuario_filme UNIQUE (filme_id, usuario_id)
);

-- Table: filme_pessoa
CREATE TABLE filme_pessoa (
    pessoa_id INT NOT NULL,
    filme_id INT NOT NULL,
    papel VARCHAR(20) NOT NULL CHECK (papel IN ('ATOR', 'DIRETOR')),
    PRIMARY KEY (pessoa_id, filme_id),
    CONSTRAINT un_filme_pessoa UNIQUE (filme_id, pessoa_id, papel),
    CONSTRAINT filme_pessoa_filme_fk FOREIGN KEY (filme_id) REFERENCES filme(id),
    CONSTRAINT filme_pessoa_pessoa_fk FOREIGN KEY (pessoa_id) REFERENCES pessoa(id)
);
