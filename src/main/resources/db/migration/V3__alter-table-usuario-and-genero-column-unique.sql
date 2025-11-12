ALTER TABLE usuario
ADD CONSTRAINT uk_usuario_login UNIQUE (login);

ALTER TABLE genero
ADD CONSTRAINT uk_genero_nome UNIQUE (nome);
