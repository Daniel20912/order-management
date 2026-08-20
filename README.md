# Sistema de Gerenciamento de Encomendas para Pequenos Produtores

API REST desenvolvida para resolver um problema real: substituir o controle manual em caderno usado por uma produtora artesanal de alimentos para organizar encomendas, evitar esquecimentos e controlar a produção diária.

O primeiro (e atual) usuário do sistema é uma produtora de alimentos que, antes deste projeto, controlava todos os seus pedidos anotando em um caderno físico.

## O problema

Pequenos produtores que trabalham sob encomenda — confeiteiros, produtores de alimentos artesanais, vendedores independentes — costumam controlar pedidos de forma manual. Isso gera problemas recorrentes:

- Pedidos esquecidos ou perdidos entre páginas do caderno.
- Nenhum alerta quando o horário de entrega está próximo.
- Nenhum cálculo automático de valores, quantidades ou resumo do dia.
- Dificuldade de localizar pedidos antigos ainda pendentes.

Este projeto substitui o caderno por um sistema simples, rápido de usar e que ativamente ajuda o produtor a não esquecer nada — sem impor uma estrutura rígida de negócio que não reflete como esses profissionais realmente trabalham no dia a dia.

## Funcionalidades da v1

- **Cadastro rápido de pedidos**, com cliente, itens (produto, quantidade e valor unitário livres, sem catálogo fixo) e data de retirada.
- **Cálculo automático** de subtotais e valor total — nunca confiado ao cliente da API, sempre recalculado no backend.
- **Status de pedido livre** (`PENDENTE`, `PRONTO`, `RETIRADO`, `CANCELADO`), sem regras rígidas de transição, refletindo a informalidade real do negócio.
- **Listagem de pedidos por data** e **por status** (útil para localizar pedidos pendentes esquecidos de dias anteriores).
- **Resumo financeiro diário** (quantidade de pedidos e valor total esperado, desconsiderando cancelados).
- **Configuração de horário limite de produção**, definida livremente pelo usuário e podendo mudar a qualquer momento.
- **Alertas automáticos de prazo**: um job agendado verifica periodicamente se existem pedidos pendentes perto do horário limite configurado, e dispara uma notificação (atualmente simulada via log — ver seção de limitações).

## Arquitetura

```
                Aplicativo Mobile (futuro)
                       |
                       |
                  API REST
               Spring Boot
                       |
          -------------------------
          |                       |
      PostgreSQL              Serviços externos
     (Supabase)                    |
                                   |
                        Firebase Notifications (futuro)
```

O aplicativo mobile ainda não foi desenvolvido — o foco desta primeira fase foi construir um backend robusto e bem testado, que sirva de base sólida para o app futuramente.

## Decisões técnicas relevantes

Algumas decisões de design que valem ser destacadas, porque refletem escolhas conscientes de arquitetura, não falta de conhecimento das alternativas:

- **Cliente não é uma entidade própria.** Os dados do cliente (`nome`, `telefone`) são um Value Object (`@Embeddable`) embutido diretamente no pedido, não uma tabela relacional separada. Como a v1 não possui cadastro de clientes, criar uma entidade separada exigiria resolver um problema de deduplicação (duas pessoas com o mesmo nome) que não existe hoje — a estrutura atual permite evoluir para uma entidade completa no futuro sem grandes refatorações.
- **Produto não possui catálogo fixo.** Cada item de pedido carrega um `nomeProduto` livre, digitado no momento do pedido — refletindo o fato de que preços e produtos variam constantemente nesse tipo de negócio informal.
- **Valores monetários sempre recalculados no backend.** `subtotal` e `valorTotal` nunca são aceitos do cliente da API; são sempre recalculados a partir dos itens, tanto na criação quanto na edição do pedido, evitando inconsistência entre o que foi enviado e o que realmente está persistido.
- **Status de pedido sem máquina de estados.** As transições entre status são completamente livres, refletindo como o negócio realmente funciona (um cliente pode cancelar e depois voltar atrás, por exemplo).
- **Controle de alerta sem tabela de histórico.** Em vez de uma tabela dedicada de notificações, o sistema usa um único campo (`ultimaDataAlertada`) na configuração para evitar alertas repetidos no mesmo dia — resolve o problema real sem overhead de uma estrutura que não tem uso hoje.
- **Resumo diário e listagens sempre calculados em tempo real.** Nenhum valor agregado é persistido; tudo é calculado via query no momento da consulta, garantindo que nunca fique desatualizado.

## Tecnologias utilizadas

- **Java 21**
- **Spring Boot** (Web, Data JPA, Validation, Scheduler)
- **PostgreSQL**
- **Flyway** (versionamento de schema)
- **MapStruct** (mapeamento entre entidades e DTOs)
- **Lombok**
- **springdoc-openapi** (Swagger/OpenAPI)
- **JUnit + Mockito** (testes automatizados)
- **Docker** (ambiente local e build de produção)

## Infraestrutura de hospedagem (gratuita)

Todo o projeto foi hospedado utilizando exclusivamente planos gratuitos, o que exigiu algumas decisões específicas de infraestrutura:

- **API**: hospedada no [Render](https://render.com), plano free, via imagem Docker (multi-stage build).
- **Banco de dados**: [Supabase](https://supabase.com) (PostgreSQL gerenciado), plano free.
- **Manter a API ativa**: o plano free do Render "dorme" após períodos de inatividade. Como o sistema depende de um job agendado rodando continuamente (verificação de prazos), foi configurado o [UptimeRobot](https://uptimerobot.com) para fazer ping periódico e manter o serviço acordado.
- **Conexão com o banco via Session Pooler**: a conexão direta do Supabase, no plano free, só está disponível via IPv6 — o que causa falhas de conexão em algumas plataformas de hospedagem. A solução foi conectar através do **Supavisor em modo session** (porta `5432`), que é IPv4 mesmo no plano gratuito e mantém uma conexão física dedicada por sessão — compatível com o pool de conexões do HikariCP e com o uso de `@Transactional`/dirty checking do Hibernate, diferente do modo *transaction* do mesmo pooler, que reaproveita conexões de forma mais agressiva e pode gerar comportamento inconsistente em transações com múltiplas operações.
- **Fuso horário**: o container é forçado a rodar no fuso `America/Sao_Paulo` (via `ENV TZ` no Dockerfile), já que containers Docker normalmente rodam em UTC por padrão — sem essa configuração, a lógica de horário limite do Scheduler calcularia os alertas com 3 horas de diferença do horário real.

## Como rodar localmente

### Pré-requisitos
- Java 21
- Docker e Docker Compose
- Maven (ou usar o wrapper do projeto, se disponível)

### Passos

1. Clone o repositório.
2. Crie um arquivo `.env` na raiz do projeto com as variáveis de ambiente necessárias:
   ```
   DB_NAME=seu_banco
   DB_USER=seu_usuario
   DB_PASSWORD=sua_senha
   SPRING_PROFILES_ACTIVE=dev
   ```
3. Suba o banco de dados local:
   ```
   docker-compose up -d
   ```
4. Rode a aplicação:
   ```
   mvn spring-boot:run
   ```
5. A API estará disponível em `http://localhost:8080`, e a documentação interativa (Swagger UI) em `http://localhost:8080/swagger-ui.html`.

O Flyway aplica as migrations automaticamente na inicialização — não é necessário nenhum passo manual de criação de schema.

## Limitações conhecidas e próximos passos

Este projeto está em constante evolução. Limitações conscientes da v1 atual:

- **Notificações ainda são simuladas** (log no servidor), não enviadas de fato ao celular do usuário. O envio real via Firebase Cloud Messaging depende da existência do aplicativo mobile, que ainda não foi desenvolvido — é necessário capturar e persistir o token de dispositivo do Firebase, algo que só existe após a instalação do app.
- **Sem autenticação.** Como decisão consciente de escopo para a v1 (sistema utilizado por uma única pessoa, sem necessidade de múltiplos usuários ou permissões), a API não possui nenhuma camada de autenticação. Esta é uma melhoria planejada caso o sistema evolua para uso mais amplo.
- **Aplicativo mobile ainda não desenvolvido.** O foco desta fase foi consolidar um backend robusto, testado e documentado.

Funcionalidades planejadas para versões futuras:

- Integração com WhatsApp para registro automático de pedidos.
- Processamento de mensagens via IA para extração automática de dados do pedido.
- Histórico de clientes (pedidos anteriores, clientes frequentes).
- Relatórios de vendas por período e produtos mais vendidos.
- Controle de estoque de ingredientes.

## Documentação da API

Com a aplicação rodando, a documentação completa e interativa de todos os endpoints está disponível via Swagger UI (`/swagger-ui.html`), incluindo exemplos de request e todas as respostas de erro possíveis.

Como a API não está publicamente exposta com dados de demonstração, segue abaixo um resumo de todos os endpoints disponíveis, para consulta sem precisar rodar o projeto.

### Pedidos

#### `POST /order`
Cria um novo pedido. O valor total e os subtotais dos itens são calculados automaticamente pelo backend — nunca aceitos do cliente. O status inicial é sempre `PENDENTE`.

**Request body:**
```json
{
  "cliente": {
    "nome": "Maria Silva",
    "telefone": "55999998888"
  },
  "dataRetirada": "2026-07-12",
  "observacoes": "Sem cobertura",
  "itens": [
    { "nomeProduto": "Bolo de chocolate", "quantidade": 2, "valorUnitario": 15.00 },
    { "nomeProduto": "Pão", "quantidade": 3, "valorUnitario": 5.00 }
  ]
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "cliente": { "nome": "Maria Silva", "telefone": "55999998888" },
  "dataRetirada": "2026-07-12",
  "observacoes": "Sem cobertura",
  "status": "PENDENTE",
  "valorTotal": 45.00,
  "itens": [
    { "id": 1, "nomeProduto": "Bolo de chocolate", "quantidade": 2, "valorUnitario": 15.00, "subtotal": 30.00 },
    { "id": 2, "nomeProduto": "Pão", "quantidade": 3, "valorUnitario": 5.00, "subtotal": 15.00 }
  ],
  "dataCriacao": "2026-07-09T14:32:00",
  "dataAtualizacao": "2026-07-09T14:32:00"
}
```

---

#### `GET /order/date?date={data}`
Lista todos os pedidos (de qualquer status) cuja data de retirada seja a informada, com os itens completos. Retorna lista vazia se não houver pedidos naquela data.

**Query param:** `date` — formato ISO `yyyy-MM-dd`

**Response `200 OK`:** lista de pedidos, no mesmo formato do response acima.

---

#### `GET /order/status?status={status}`
Lista todos os pedidos (de qualquer data) com o status informado. Utilizado, por exemplo, para localizar pedidos `PENDENTE` esquecidos de dias anteriores. Retorna lista vazia se não houver pedidos com aquele status.

**Query param:** `status` — um dos valores: `PENDENTE`, `PRONTO`, `RETIRADO`, `CANCELADO`

**Response `200 OK`:** lista de pedidos, no mesmo formato do response de criação.

---

#### `GET /order/{id}`
Retorna os dados completos de um pedido específico, incluindo seus itens.

**Response `200 OK`:** pedido, no mesmo formato do response de criação.
**Response `404 Not Found`:** pedido não encontrado para o id informado.

---

#### `PUT /order/{id}`
Atualiza um pedido existente. Substitui cliente, data de retirada, observações e a lista de itens por completo — todos os itens devem ser reenviados, mesmo os que não mudaram. O valor total é recalculado automaticamente. **O status do pedido não é alterado por esta operação.**

**Request body:** mesmo formato do `POST /order`.

**Response `200 OK`:** pedido atualizado, no mesmo formato do response de criação.
**Response `404 Not Found`:** pedido não encontrado para o id informado.

---

#### `PATCH /order/{id}/status`
Atualiza apenas o status de um pedido, sem afetar os demais campos. As transições entre status são livres, sem restrição de fluxo.

**Request body:**
```json
{ "status": "PRONTO" }
```

**Response `200 OK`:** pedido atualizado, no mesmo formato do response de criação.
**Response `404 Not Found`:** pedido não encontrado para o id informado.

---

#### `GET /order/summary?date={data}`
Retorna a quantidade de pedidos e o valor total esperado para a data informada, desconsiderando pedidos com status `CANCELADO`. Calculado dinamicamente a cada consulta, nunca persistido.

**Query param:** `date` — formato ISO `yyyy-MM-dd`

**Response `200 OK`:**
```json
{
  "data": "2026-07-12",
  "quantidadePedidos": 7,
  "valorTotalEsperado": 480.00
}
```

### Configuração

#### `GET /config`
Retorna a configuração global atual: horário limite de produção e antecedência (em minutos) do alerta de prazo. Como o sistema possui uma única configuração global, não é necessário informar nenhum identificador.

**Response `200 OK`:**
```json
{
  "horarioLimite": "17:00",
  "minutosAntecedenciaAlerta": 30
}
```

---

#### `PUT /config`
Atualiza a configuração global.

**Request body:**
```json
{
  "horarioLimite": "18:00",
  "minutosAntecedenciaAlerta": 60
}
```

**Response `200 OK`:** configuração atualizada, no mesmo formato do `GET /config`.

### Tratamento de erros

Todas as respostas de erro seguem um formato padronizado:

```json
{
  "path": "/order/99",
  "method": "GET",
  "status": 404,
  "statusText": "Not Found",
  "message": "Pedido com id 99 não encontrado"
}
```

Em erros de validação de campo (`400`), o corpo inclui adicionalmente um mapa `errors`, com o nome de cada campo inválido e a respectiva mensagem:

```json
{
  "path": "/order",
  "method": "POST",
  "status": 400,
  "statusText": "Bad Request",
  "message": "Campo(s) Inválido(s)",
  "errors": {
    "dataRetirada": "não deve ser nulo"
  }
}
```
