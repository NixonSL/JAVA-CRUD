# Projeto

API REST em Spring Boot para cadastro e consulta de produtos, com carrinho por usuário e autenticação JWT integrada a um serviço externo de autenticação.

## Visão geral

O projeto expõe endpoints versionados no modelo URI path. A versão atual é `v1`, então as rotas públicas da API seguem o padrão:

`/api/v1/...`

Principais responsabilidades:

- CRUD de produtos
- consultas por nome, categoria, preço e estoque
- carrinho vinculado ao usuário autenticado
- integração com Auth Service via Feign Client
- autenticação via JWT

## Stack

- Java 17
- Spring Boot 3.2
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Cloud OpenFeign
- Bean Validation
- PostgreSQL
- JWT com `jjwt`

## Estrutura

- `controller`: exposição dos endpoints REST
- `service`: regras de negócio
- `repository`: persistência com Spring Data JPA
- `entity`: entidades JPA
- `dto`: contratos de entrada e saída
- `security`: filtro JWT e configuração de segurança
- `client`: integração com o Auth Service
- `exception`: tratamento padronizado de erros

## Versionamento da API

O projeto usa versionamento por URI path, com prefixo fixo em todas as rotas internas:

- `GET /api/v1/produtos`
- `GET /api/v1/cart/{userId}`

Essa abordagem permite evoluir para novas versões sem quebrar clientes antigos. Quando houver uma `v2`, ela pode coexistir com a `v1`.

## Autenticação

As rotas da aplicação exigem autenticação JWT, com exceção dos endpoints explicitamente liberados na configuração de segurança.

O header esperado é:

```http
Authorization: Bearer <token>
```

O filtro JWT lê as claims do token, extrai `userId` e `role`, e monta a autenticação no contexto do Spring Security.

## Dependências externas

O carrinho depende de um Auth Service externo para validar usuário e carregar dados básicos do usuário.

Endpoints consumidos pelo cliente Feign:

- `GET /api/auth/users/{id}`
- `GET /api/auth/users/email/{email}`
- `GET /api/auth/users/{id}/exists`
- `GET /api/auth/users/{id}/role`

A URL base do serviço é configurada por `AUTH_SERVICE_URL`. Se não for informada, o cliente usa `http://localhost:8081`.

## Configuração

Variáveis de ambiente usadas pela aplicação:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `JWT_REFRESH_EXPIRATION`
- `BCRYPT_STRENGTH`
- `AUTH_SERVICE_URL`
- `FEIGN_LOG_LEVEL`

Exemplo de arquivo `.env`:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=projeto_db
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=uma-chave-forte-aqui
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000
AUTH_SERVICE_URL=http://localhost:8081
FEIGN_LOG_LEVEL=BASIC
```

## Banco de dados

O projeto foi configurado para PostgreSQL.

O `docker-compose.yml` sobe apenas o banco de dados:

```bash
docker compose up -d
```

O serviço espera o banco em `localhost:5432`, salvo se você alterar as variáveis de ambiente.

## Como executar

### 1. Subir o banco

```bash
docker compose up -d
```

### 2. Configurar ambiente

Garanta que as variáveis de ambiente estejam carregadas antes de iniciar a aplicação.

### 3. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### 4. Rodar testes

```bash
./mvnw test
```

## Build

Para gerar o JAR:

```bash
./mvnw clean package
```

O `dockerfile` da aplicação espera o artefato em `target/*.jar`.

## Endpoints

### Produtos

Base: `/api/v1/produtos`

#### `POST /api/v1/produtos`
Cria um produto. Requer perfil `ADMIN`.

Request:

```json
{
  "nome": "Notebook",
  "descricao": "Notebook para trabalho",
  "preco": 3999.90,
  "quantidade": 10,
  "categoria": "Eletronicos"
}
```

#### `GET /api/v1/produtos`
Lista todos os produtos.

#### `GET /api/v1/produtos/{id}`
Busca um produto por ID.

#### `PUT /api/v1/produtos/{id}`
Atualiza um produto. Requer perfil `ADMIN`.

#### `DELETE /api/v1/produtos/{id}`
Remove um produto. Requer perfil `ADMIN`.

#### `GET /api/v1/produtos/categoria/{categoria}`
Lista produtos por categoria.

#### `GET /api/v1/produtos/buscar/nome?nome=...`
Busca produtos por parte do nome, ignorando maiúsculas e minúsculas.

#### `GET /api/v1/produtos/buscar/preco?min=100&max=1000`
Busca produtos por faixa de preço.

#### `GET /api/v1/produtos/buscar/estoque-baixo?quantidade=5`
Lista produtos com estoque abaixo do valor informado.

#### `GET /api/v1/produtos/buscar/categoria-preco?categoria=...&precoMax=...`
Busca por categoria e preço máximo.

#### `GET /api/v1/produtos/ordenados/preco`
Lista produtos ordenados por preço crescente.

#### `GET /api/v1/produtos/ordenados/nome`
Lista produtos ordenados por nome crescente.

### Carrinho

Base: `/api/v1/cart`

#### `GET /api/v1/cart/{userId}`
Retorna o carrinho do usuário. Se não existir, a aplicação tenta criá-lo após validar o usuário no Auth Service.

#### `POST /api/v1/cart/{userId}/add`
Adiciona um produto ao carrinho.

Request:

```json
{
  "produtoId": 1
}
```

#### `DELETE /api/v1/cart/{userId}/remove/{produtoId}`
Remove um produto do carrinho.

#### `DELETE /api/v1/cart/{userId}/clear`
Limpa o carrinho.

#### `GET /api/v1/cart/{userId}/check/{produtoId}`
Verifica se um produto está no carrinho.

#### `GET /api/v1/cart/{userId}/count`
Retorna a quantidade de produtos no carrinho.

## Formato de erro

Erros de validação retornam um mapa de campos e mensagens.

Erros de recurso não encontrado retornam um objeto com:

```json
{
  "status": 404,
  "mensagem": "Produto não encontrado com id: 1",
  "timestamp": "2026-07-13T10:00:00",
  "path": "/api/v1/produtos/1"
}
```

Erros genéricos retornam o mesmo formato com status `500`.

## Observações

- O projeto usa `spring.jpa.hibernate.ddl-auto=update`.
- O carrinho depende de disponibilidade do Auth Service.
- A documentação dos exemplos acompanha a versão `v1`; novas versões devem ser publicadas em paths próprios.
