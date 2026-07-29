CREATE TABLE pedidos
(
    id               BIGSERIAL PRIMARY KEY,
    cliente_nome     VARCHAR(100)   NOT NULL,
    cliente_telefone VARCHAR(20),
    data_retirada    DATE           NOT NULL,
    observacoes      TEXT,
    status           VARCHAR(30)    NOT NULL,
    valor_total      NUMERIC(10, 2) NOT NULL,
    data_criacao     TIMESTAMP      NOT NULL,
    data_atualizacao TIMESTAMP      NOT NULL
);

CREATE TABLE item_pedidos
(
    id             BIGSERIAL PRIMARY KEY,
    fk_pedido_id   BIGINT         NOT NULL REFERENCES pedidos (id),
    nome_produto   VARCHAR(150)   NOT NULL,
    quantidade     INTEGER        NOT NULL,
    valor_unitario NUMERIC(10, 2) NOT NULL,
    subtotal       NUMERIC(10, 2) NOT NULL
);

CREATE TABLE configuracoes
(
    id                          BIGSERIAL PRIMARY KEY,
    horario_limite              TIME    NOT NULL,
    minutos_antecedencia_alerta INTEGER NOT NULL CHECK ( minutos_antecedencia_alerta >= 0 )
);

CREATE INDEX idx_pedidos_data_retirada ON pedidos (data_retirada);

INSERT INTO configuracoes (horario_limite, minutos_antecedencia_alerta)
VALUES ('17:00', 30);