# Roteiro de entrega — DoeLivro

## Checklist antes de enviar

- [ ] Conferir se a aplicação inicia com MongoDB em execução.
- [ ] Cadastrar um usuário e conferir o documento em `usuarios`.
- [ ] Cadastrar um livro e conferir o documento em `livros`.
- [ ] Registrar uma doação usando os IDs reais de um livro e de um usuário.
- [ ] Conferir `livroId`, `doadorId`, `status` e `historico` em `doacoes`.
- [ ] Atualizar a doação para `RESERVADO` e depois para `DOADO` (ou testar o cancelamento).
- [ ] Executar todos os testes automatizados.
- [ ] Gerar `target/site/jacoco/index.html` e confirmar cobertura mínima de 70%.
- [ ] Atualizar o quadro Kanban no GitHub com tarefas concluídas e evidências.
- [ ] Fazer commit e push da versão final.
- [ ] Criar uma tag ou release identificando a versão entregue.
- [ ] Gravar o vídeo de demonstração com duração entre 3 e 5 minutos.

## Sugestão de quadro Kanban

Colunas: **A fazer**, **Em andamento**, **Concluído**.

Cartões sugeridos: cadastro de usuários; cadastro e edição de livros; relacionamento livro/usuário; módulo de doações; histórico de status; testes unitários; cobertura JaCoCo; documentação; revisão do GitHub; gravação do vídeo. Mova os cartões conforme o trabalho real e mantenha capturas de tela ou links como evidência.

## Roteiro sugerido para o vídeo (3–5 minutos)

1. **0:00–0:30 — Apresentação:** explique que o DoeLivro conecta pessoas que desejam doar livros a pessoas que precisam de materiais de leitura, apoiando a ODS 4.
2. **0:30–1:10 — Tecnologias e arquitetura:** mostre Java 21, Spring Boot, MongoDB, HTML/CSS/JavaScript e a separação model, repository, service e controller.
3. **1:10–2:30 — Demonstração:** cadastre um usuário, cadastre um livro, registre uma doação e mostre a coleção `doacoes` no MongoDB Compass.
4. **2:30–3:15 — Evolução NoSQL:** mostre as três coleções, os IDs que relacionam usuário/livro/doação, a lista de endereços embutida em `usuarios` e o histórico embutido em `doacoes`.
5. **3:15–4:15 — Testes e gestão:** execute os testes, mostre o relatório JaCoCo com o percentual real e apresente o quadro Kanban e a versão/tag no GitHub.
6. **4:15–4:40 — Encerramento:** retome o objetivo social do projeto e conclua a demonstração. Ajuste os tempos para manter o vídeo dentro do limite solicitado.

> O vídeo, o quadro Kanban atualizado, a tag/release no GitHub e o percentual de cobertura são evidências que precisam ser efetivamente produzidas e conferidas. Este roteiro não substitui essas evidências.
