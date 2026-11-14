# DoeLivro 📚

> Plataforma de doação de livros desenvolvida como Prova de Conceito (PoC) para a Atividade de Estudo Programada (AEP) do 6º semestre de Engenharia de Software.

## Alunos

- **Danilo Antonio Alves Rosa**
- **Renan Homiak Guimarães**

---

## Problema

O acesso à leitura ainda é um desafio para muitas pessoas devido ao alto custo de livros didáticos, técnicos e literários. Ao mesmo tempo, milhares de livros permanecem armazenados sem uso ou acabam sendo descartados após cumprirem sua finalidade.

A ausência de uma plataforma simples para conectar pessoas que desejam doar livros com aquelas que precisam deles reduz o reaproveitamento desses materiais e dificulta o acesso ao conhecimento.

O **DoeLivro** busca solucionar esse problema oferecendo uma plataforma onde usuários podem cadastrar livros disponíveis para doação, promovendo o compartilhamento de conhecimento, o incentivo à leitura e o reaproveitamento de livros em bom estado.

---

## ODS relacionada

### ODS 4 — Educação de Qualidade

O projeto está alinhado ao Objetivo de Desenvolvimento Sustentável nº 4 da Organização das Nações Unidas (ONU), que busca assegurar uma educação inclusiva, equitativa e de qualidade, promovendo oportunidades de aprendizagem ao longo da vida para todos.

Ao facilitar a doação de livros, o DoeLivro contribui para ampliar o acesso ao conhecimento, incentivando a circulação de materiais educacionais e literários e reduzindo barreiras econômicas ao aprendizado.

---

## Público-alvo

A plataforma é destinada a pessoas interessadas em doar livros e a estudantes, leitores e instituições que buscam acesso a materiais de leitura de forma gratuita.

---

## Tecnologias utilizadas

- **Java 21**
- **Spring Boot 4.1.1**
  - Spring WebMVC
  - Spring Data MongoDB
  - Spring Validation
- **MongoDB** — Banco de Dados NoSQL
- **Maven** — Gerenciamento de dependências e build
- **JUnit 5 e Mockito** — Testes automatizados
- **JaCoCo 0.8.14** — Análise de cobertura dos testes
- **HTML5**
- **CSS3**
- **JavaScript**
- **Git e GitHub**

---

## Funcionalidades

### 1. Gerenciamento de usuários

- Cadastro de usuários;
- Listagem de usuários;
- Consulta por identificador;
- Atualização de informações;
- Exclusão de usuários;
- Validação de e-mail;
- Cadastro de endereços associados ao usuário;
- Verificação de vínculos antes da exclusão.

### 2. Gerenciamento de livros

- Cadastro de livros;
- Listagem dos livros cadastrados;
- Consulta por identificador;
- Atualização de informações;
- Exclusão de livros;
- Associação de livros a doadores;
- Controle de disponibilidade;
- Validação dos dados cadastrados.

### 3. Gerenciamento de doações

- Registro de novas doações;
- Listagem de todas as doações;
- Consulta por identificador;
- Consulta por doador;
- Consulta por livro;
- Filtragem por status;
- Atualização do status da doação;
- Registro do histórico de alterações;
- Validação de livros e usuários associados;
- Controle de transições permitidas entre os status.

### Status das doações

| Status | Descrição |
|---|---|
| `DISPONIVEL` | Livro disponível para doação |
| `RESERVADO` | Livro reservado |
| `DOADO` | Doação concluída |
| `CANCELADO` | Doação cancelada |

---

## API REST

A aplicação disponibiliza endpoints REST para comunicação com o sistema.

### Livros — `/livros`

| Método | Rota | Descrição |
|---|---|---|
| POST | `/livros` | Cadastrar livro |
| GET | `/livros` | Listar livros |
| GET | `/livros/{id}` | Buscar livro por ID |
| PUT | `/livros/{id}` | Atualizar livro |
| DELETE | `/livros/{id}` | Excluir livro |

### Usuários — `/usuarios`

| Método | Rota | Descrição |
|---|---|---|
| POST | `/usuarios` | Cadastrar usuário |
| GET | `/usuarios` | Listar usuários |
| GET | `/usuarios/{id}` | Buscar usuário por ID |
| PUT | `/usuarios/{id}` | Atualizar usuário |
| DELETE | `/usuarios/{id}` | Excluir usuário |

### Doações — `/doacoes`

| Método | Rota | Descrição |
|---|---|---|
| POST | `/doacoes` | Registrar doação |
| GET | `/doacoes` | Listar doações |
| GET | `/doacoes/{id}` | Buscar doação por ID |
| GET | `/doacoes/doador/{doadorId}` | Listar por doador |
| GET | `/doacoes/livro/{livroId}` | Listar por livro |
| GET | `/doacoes/status/{status}` | Filtrar por status |
| PATCH | `/doacoes/{id}/status` | Atualizar status |
| DELETE | `/doacoes/{id}` | Excluir doação |

---

## Arquitetura

O projeto está organizado em camadas para manter uma separação clara de responsabilidades:

```text
controller/  → recebe e responde às requisições HTTP
service/     → implementa as regras de negócio
repository/  → realiza o acesso ao MongoDB
model/       → representa as entidades e os dados
```

Essa arquitetura facilita a manutenção, evolução e organização do código, seguindo princípios da Programação Orientada a Objetos.

---

## Estrutura do banco de dados

O sistema utiliza o MongoDB como banco de dados NoSQL, organizando as informações em três coleções principais:

- **usuarios:** dados dos usuários e seus endereços;
- **livros:** informações dos livros cadastrados;
- **doacoes:** registros de doações e histórico de alterações de status.

O histórico de status é armazenado dentro do próprio documento da doação, utilizando uma estrutura de documentos embutidos.

---

## Como executar o projeto

### Pré-requisitos

- Java 21 instalado;
- MongoDB Community Server instalado e em execução;
- Git.

O MongoDB Compass pode ser utilizado como interface gráfica para visualizar o banco de dados.

### 1. Clonar o repositório

```bash
git clone https://github.com/renanhomiak/AEP-6-Semestre.git
```

### 2. Entrar na pasta do projeto

```bash
cd AEP-6-Semestre/DoeLivro
```

### 3. Configuração do MongoDB

Por padrão, a aplicação utiliza a seguinte conexão:

```text
mongodb://localhost:27017/doelivro
```

A URI pode ser personalizada por meio da variável de ambiente `MONGODB_URI`.

### 4. Executar a aplicação

**Git Bash:**

```bash
./mvnw spring-boot:run
```

**PowerShell:**

```powershell
.\mvnw.cmd spring-boot:run
```

### 5. Acessar a aplicação

Após iniciar o servidor, acesse:

```text
http://localhost:8080
```

---

## Testes automatizados

O projeto possui testes automatizados para os módulos de livros, usuários e doações, contemplando:

- Testes dos modelos;
- Testes das regras de negócio dos serviços;
- Testes dos controllers e endpoints;
- Validação de cenários de sucesso e erro.

### Executar os testes

**Git Bash:**

```bash
./mvnw test
```

**PowerShell:**

```powershell
.\mvnw.cmd test
```

### Gerar relatório de cobertura

Para executar os testes e gerar o relatório de cobertura com JaCoCo:

**Git Bash:**

```bash
./mvnw clean verify
```

**PowerShell:**

```powershell
.\mvnw.cmd clean verify
```

### Resultados dos testes

Na execução realizada durante o desenvolvimento:

- **62 testes executados;**
- **0 falhas;**
- **0 erros;**
- **0 testes ignorados.**

### Cobertura de testes — JaCoCo

| Métrica | Resultado |
|---|---:|
| Cobertura de instruções | 82% |
| Cobertura de branches | 59% |
| Classes analisadas | 14 |

O relatório HTML é gerado automaticamente no seguinte caminho:

```text
target/site/jacoco/index.html
```

Abra o arquivo `index.html` em um navegador para visualizar os detalhes da cobertura.

---

## Estrutura do projeto

```text
AEP-6-Semestre/
├── DoeLivro/
│   ├── .mvn/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/aep/ler/
│       │   │   ├── LerApplication.java
│       │   │   ├── controller/
│       │   │   │   ├── ApiExceptionHandler.java
│       │   │   │   ├── DoacaoController.java
│       │   │   │   ├── LivroController.java
│       │   │   │   └── UsuarioController.java
│       │   │   ├── model/
│       │   │   │   ├── Doacao.java
│       │   │   │   ├── Endereco.java
│       │   │   │   ├── Livro.java
│       │   │   │   └── Usuario.java
│       │   │   ├── repository/
│       │   │   │   ├── DoacaoRepository.java
│       │   │   │   ├── LivroRepository.java
│       │   │   │   └── UsuarioRepository.java
│       │   │   └── service/
│       │   │       ├── DoacaoService.java
│       │   │       ├── LivroService.java
│       │   │       └── UsuarioService.java
│       │   └── resources/
│       │       ├── application.properties
│       │       └── static/
│       │           ├── index.html
│       │           ├── script.js
│       │           └── style.css
│       └── test/
│           └── java/com/aep/ler/
│               ├── LerApplicationTests.java
│               ├── controller/
│               │   ├── DoacaoControllerTest.java
│               │   ├── LivroControllerTest.java
│               │   └── UsuarioControllerTest.java
│               ├── model/
│               │   ├── DoacaoTest.java
│               │   ├── LivroTest.java
│               │   └── UsuarioTest.java
│               └── service/
│                   ├── DoacaoServiceTest.java
│                   ├── LivroServiceTest.java
│                   └── UsuarioServiceTest.java
├── docs/
└── README.md
```

---

## Considerações finais

O DoeLivro evoluiu de uma prova de conceito focada no cadastro de livros para uma plataforma com gerenciamento de usuários, livros e doações.

A segunda entrega ampliou a API REST, implementou regras de negócio para o processo de doação, integrou os módulos ao MongoDB e adicionou testes automatizados com análise de cobertura utilizando JaCoCo.

O projeto busca demonstrar, na prática, conceitos de Programação Orientada a Objetos, desenvolvimento de APIs REST, persistência NoSQL e qualidade de software.
