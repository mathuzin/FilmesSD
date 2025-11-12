ALTER TABLE filme
ADD CONSTRAINT chk_ano_valido
CHECK (ano >= 1888);
