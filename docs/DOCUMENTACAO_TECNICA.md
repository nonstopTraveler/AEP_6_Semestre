# Documentação técnica — DoeLivro

## 1. Visão geral

O DoeLivro é uma aplicação web de apoio à doação e circulação de livros. A interface é construída com HTML, CSS e JavaScript. O backend utiliza Java 21 e Spring Boot, e os dados são persistidos no MongoDB.

## 2. Arquitetura

A aplicação utiliza separação em camadas:

- `model`: representa os documentos persistidos no MongoDB.
- `repository`: abstrai as operações de persistência por meio do Spring Data MongoDB.
- `service`: concentra regras de negócio e validações.
- `controller`: recebe requisições HTTP e chama os serviços.
- `resources/static`: contém a interface web.

## 3. Modelo de dados NoSQL

### Coleção `usuarios`

Campos principais: `_id`, `nome`, `email`, `telefone`, `cidade`, `estado` e `enderecos`.

`enderecos` é uma lista de subdocumentos embutidos. Essa estrutura permite que um usuário possua mais de um endereço sem criar uma coleção separada para esse dado.

### Coleção `livros`

Campos principais: `_id`, `titulo`, `autor`, `categoria`, `estadoConservacao`, `disponivel` e `doadorId`.

`doadorId` armazena o identificador do usuário associado ao livro.

### Coleção `doacoes`

Campos principais: `_id`, `livroId`, `doadorId`, `status` e `historico`.

`livroId` referencia um livro e `doadorId` referencia um usuário. `historico` é uma lista de subdocumentos com `status`, `data` e `observacao`. Assim, a aplicação demonstra documentos relacionados por identificadores e estrutura complexa embutida.

## 4. Regras de negócio das doações

1. Uma doação exige um livro e um usuário existentes.
2. Se o livro já estiver associado a outro doador, o cadastro é recusado.
3. Um livro não pode possuir mais de uma doação ativa. Um registro cancelado não impede uma nova doação.
4. O backend define o status inicial como `DISPONIVEL` e cria a primeira entrada no histórico.
5. Status aceitos: `DISPONIVEL`, `RESERVADO`, `DOADO` e `CANCELADO`.
6. Transições permitidas: `DISPONIVEL` para `RESERVADO`, `DOADO` ou `CANCELADO`; `RESERVADO` para `DISPONIVEL`, `DOADO` ou `CANCELADO`.
7. `DOADO` e `CANCELADO` são estados finais do registro. Para relistar um livro cancelado, cria-se um novo registro de doação.
8. Cada mudança efetiva de status registra data, status e observação no histórico.

## 5. Exemplos de requisição

### Cadastrar usuário

`POST /usuarios`

```json
{
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "telefone": "44999999999",
  "cidade": "Maringá",
  "estado": "PR",
  "enderecos": [
    {
      "rua": "Rua das Flores",
      "numero": "123",
      "complemento": "Casa",
      "bairro": "Centro",
      "cidade": "Maringá",
      "estado": "PR",
      "cep": "87000-000"
    }
  ]
}
```

### Cadastrar livro

`POST /livros`

```json
{
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "categoria": "Romance",
  "estadoConservacao": "Bom",
  "disponivel": true,
  "doadorId": "ID_REAL_DO_USUARIO"
}
```

### Registrar doação

`POST /doacoes`

```json
{
  "livroId": "ID_REAL_DO_LIVRO",
  "doadorId": "ID_REAL_DO_USUARIO"
}
```

Não envie `status` nem `historico`: os dois são preparados pelo backend no cadastro.

### Atualizar status

`PATCH /doacoes/ID_REAL_DA_DOACAO/status`

```json
{
  "status": "RESERVADO",
  "observacao": "Interessado confirmou a reserva."
}
```

## 6. Execução e testes

A aplicação usa `mongodb://localhost:27017/doelivro`, configurado em `application.properties`. O projeto inclui testes automatizados com JUnit 5 e Mockito. A cobertura é medida por JaCoCo e o relatório é gerado por `mvn verify`, em `target/site/jacoco/index.html`.
