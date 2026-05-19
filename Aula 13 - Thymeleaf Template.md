# Aula: Utilização de Fragments no Spring Boot com Thymeleaf

## 1. Objetivo da aula

Nesta aula, vamos estudar como utilizar **fragments** no Thymeleaf para criar páginas HTML reutilizáveis em uma aplicação Spring Boot. A ideia principal é evitar a repetição de código em várias páginas, centralizando partes comuns da interface, como **menu**, **rodapé**, **cabeçalho** e **estrutura principal da página**.

Ao final da aula, o aluno deverá ser capaz de:

- Compreender o conceito de fragmentos no Thymeleaf;
- Criar um layout base reutilizável;
- Separar partes da interface em arquivos próprios;
- Criar páginas que utilizam o mesmo layout;
- Utilizar Bootstrap para estilizar as páginas;
- Criar páginas como `index`, `login` e uma tela genérica de cadastro.

---

## 2. Contextualização

Em aplicações web, é comum que várias páginas possuam elementos repetidos, como:

- Barra de navegação;
- Rodapé;
- Importação de arquivos CSS e JavaScript;
- Estrutura visual principal;
- Títulos e containers.

Sem o uso de templates reutilizáveis, cada página precisaria repetir essas estruturas, tornando o projeto mais difícil de manter. Caso fosse necessário alterar o menu, por exemplo, seria preciso modificar todas as páginas individualmente.

O Thymeleaf permite resolver esse problema por meio dos **fragments**, que funcionam como blocos HTML reutilizáveis.

---

## 3. O que são Fragments no Thymeleaf?

Fragments são trechos de código HTML que podem ser definidos em um arquivo e reutilizados em outros templates.

Eles são muito úteis para criar:

- Menus;
- Rodapés;
- Cabeçalhos;
- Layouts base;
- Componentes visuais reutilizáveis.

No Thymeleaf, normalmente usamos os seguintes atributos:

| Atributo | Função |
|---|---|
| `th:fragment` | Define um fragmento reutilizável |
| `th:replace` | Substitui uma tag pelo fragmento indicado |
| `th:insert` | Insere um fragmento dentro da tag atual |
| `th:include` | Inclui apenas o conteúdo interno do fragmento |

Nesta aula, utilizaremos principalmente `th:fragment` e `th:replace`.

---

## 4. Estrutura do projeto

A estrutura dos arquivos HTML ficará da seguinte forma:

```text
src
└── main
    └── resources
        └── templates
            ├── index.html
            ├── login.html
            ├── cadastro.html
            └── fragments
                ├── layout.html
                ├── navbar.html
                └── footer.html
```

A pasta `fragments` será utilizada para armazenar os componentes reutilizáveis da interface.

---

## 5. Dependências necessárias

No projeto Spring Boot, é necessário ter a dependência do Thymeleaf no arquivo `pom.xml`.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

## 6. Criando o Controller

Crie um controller simples para carregar as páginas da aplicação.

Arquivo: `src/main/java/com/example/demo/controller/PageController.java`

```java
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
    }
}
```

Esse controller será responsável por retornar os templates HTML localizados na pasta `templates`.

---

## 7. Criando o fragmento da Navbar

Arquivo: `src/main/resources/templates/fragments/navbar.html`

```html
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org">
<body>

<nav th:fragment="navbar" class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" th:href="@{/}">Sistema Web</a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Alternar navegação">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/}">Início</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/cadastro}">Cadastro</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/login}">Login</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

</body>
</html>
```

Neste arquivo, foi criado um fragmento chamado `navbar`.

```html
<nav th:fragment="navbar">
```

Esse fragmento poderá ser utilizado em outras páginas por meio do `th:replace`.

---

## 8. Criando o fragmento do Footer

Arquivo: `src/main/resources/templates/fragments/footer.html`

```html
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org">
<body>

<footer th:fragment="footer" class="bg-light text-center text-muted py-4 mt-5 border-top">
    <div class="container">
        <p class="mb-1">Sistema Web desenvolvido com Spring Boot e Thymeleaf</p>
        <small>Exemplo didático para estudo de fragments</small>
    </div>
</footer>

</body>
</html>
```

O rodapé também foi separado em um fragmento próprio. Dessa forma, qualquer alteração no rodapé será feita apenas neste arquivo.

---

## 9. Criando o layout base

Arquivo: `src/main/resources/templates/fragments/layout.html`

```html
<!DOCTYPE html>
<html lang="pt-br"
      xmlns:th="http://www.thymeleaf.org"
      th:fragment="layout(titulo, conteudo)">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title th:text="${titulo}">Sistema Web</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light d-flex flex-column min-vh-100">

<header th:replace="~{fragments/navbar :: navbar}"></header>

<!-- ÁREA PRINCIPAL -->
<div class="container py-4 flex-fill d-flex flex-column">

    <main class="flex-fill d-flex flex-column"
          th:replace="${conteudo}">

        <p>Conteúdo principal</p>

    </main>

</div>

<footer th:replace="~{fragments/footer :: footer}"></footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
```

Neste arquivo, criamos um fragmento chamado `layout`, que recebe dois parâmetros:

```html
th:fragment="layout(titulo, conteudo)"
```

Os parâmetros são:

| Parâmetro | Finalidade |
|---|---|
| `titulo` | Define o título da página |
| `conteudo` | Define qual conteúdo será exibido dentro da página |

O layout base já contém:

- Importação do Bootstrap;
- Navbar;
- Área principal;
- Footer;
- Script do Bootstrap.

Assim, as páginas filhas não precisam repetir essa estrutura.

---

## 10. Criando a página Index

Arquivo: `src/main/resources/templates/index.html`

```html
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout :: layout('Página Inicial', ~{::main})}">
<body>

<main>
    <section class="p-5 mb-4 bg-white rounded-3 shadow-sm">
        <div class="container-fluid py-4">
            <h1 class="display-5 fw-bold">Bem-vindo ao Sistema Web</h1>
            <p class="col-md-8 fs-5">
                Esta é uma página inicial criada com Spring Boot, Thymeleaf, fragments e Bootstrap.
            </p>
            <a class="btn btn-primary btn-lg" th:href="@{/cadastro}">Ir para cadastro</a>
        </div>
    </section>

    <section class="row">
        <div class="col-md-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Fragments</h5>
                    <p class="card-text">Permitem reutilizar partes da interface em várias páginas.</p>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Thymeleaf</h5>
                    <p class="card-text">Motor de templates utilizado para integrar HTML com dados do Spring Boot.</p>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Bootstrap</h5>
                    <p class="card-text">Framework CSS utilizado para criar páginas responsivas e estilizadas.</p>
                </div>
            </div>
        </div>
    </section>
</main>

</body>
</html>
```

Observe a linha principal:

```html
th:replace="~{fragments/layout :: layout('Página Inicial', ~{::main})}"
```

Essa linha indica que a página `index.html` será renderizada usando o fragmento `layout`, localizado no arquivo `fragments/layout.html`.

O conteúdo da tag `<main>` da página atual será enviado para o parâmetro `conteudo` do layout.

---

## 11. Criando a página Login

Arquivo: `src/main/resources/templates/login.html`

```html
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout :: layout('Login', ~{::main})}">
<body>

<main>
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">Acesso ao Sistema</h4>
                </div>

                <div class="card-body">
                    <form method="post" action="#">
                        <div class="mb-3">
                            <label for="email" class="form-label">E-mail</label>
                            <input type="email" class="form-control" id="email" name="email" placeholder="Digite seu e-mail">
                        </div>

                        <div class="mb-3">
                            <label for="senha" class="form-label">Senha</label>
                            <input type="password" class="form-control" id="senha" name="senha" placeholder="Digite sua senha">
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">Entrar</button>
                        </div>
                    </form>
                </div>

                <div class="card-footer text-center">
                    <small class="text-muted">Exemplo de tela de login</small>
                </div>
            </div>
        </div>
    </div>
</main>

</body>
</html>
```

Essa página utiliza o mesmo layout da página inicial, mas possui um conteúdo diferente dentro da tag `<main>`.

---

## 12. Criando a página genérica de cadastro

Arquivo: `src/main/resources/templates/cadastro.html`

```html
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout :: layout('Cadastro', ~{::main})}">
<body>

<main>
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">Cadastro Genérico</h4>
                </div>

                <div class="card-body">
                    <form method="post" action="#">
                        <div class="mb-3">
                            <label for="nome" class="form-label">Nome</label>
                            <input type="text" class="form-control" id="nome" name="nome" placeholder="Digite o nome">
                        </div>

                        <div class="mb-3">
                            <label for="descricao" class="form-label">Descrição</label>
                            <textarea class="form-control" id="descricao" name="descricao" rows="3"
                                      placeholder="Digite uma descrição"></textarea>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="categoria" class="form-label">Categoria</label>
                                <input type="text" class="form-control" id="categoria" name="categoria"
                                       placeholder="Digite a categoria">
                            </div>

                            <div class="col-md-6 mb-3">
                                <label for="status" class="form-label">Status</label>
                                <select class="form-select" id="status" name="status">
                                    <option value="ativo">Ativo</option>
                                    <option value="inativo">Inativo</option>
                                </select>
                            </div>
                        </div>

                        <div class="d-flex justify-content-end gap-2">
                            <button type="reset" class="btn btn-secondary">Limpar</button>
                            <button type="submit" class="btn btn-primary">Salvar</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

</body>
</html>
```

Essa página representa uma tela genérica de cadastro. Posteriormente, ela pode ser adaptada para cadastrar produtos, usuários, categorias, clientes ou qualquer outra entidade do sistema.

---

## 13. Diferença entre `th:replace`, `th:insert` e `th:include`

### `th:replace`

Substitui a tag atual pelo fragmento informado.

```html
<header th:replace="~{fragments/navbar :: navbar}"></header>
```

Nesse exemplo, a tag `<header>` será substituída pelo conteúdo do fragmento `navbar`.

### `th:insert`

Insere o fragmento dentro da tag atual, mantendo a tag externa.

```html
<div th:insert="~{fragments/navbar :: navbar}"></div>
```

Nesse caso, o fragmento será inserido dentro da `div`.

### `th:include`

Inclui apenas o conteúdo interno do fragmento. Em versões mais recentes do Thymeleaf, recomenda-se priorizar `th:replace` ou `th:insert`.

---

## 14. Exercício prático

Implemente o exemplo apresentado nesta aula e, em seguida, realize as seguintes modificações:

1. Altere o nome do sistema exibido na navbar.
2. Adicione um novo item no menu chamado `Sobre`.
3. Crie uma nova página chamada `sobre.html`.
4. Crie uma rota `/sobre` no controller.
5. Faça a página `sobre.html` utilizar o mesmo layout base.
6. Modifique o rodapé para exibir o nome da disciplina.
7. Altere a cor da navbar utilizando classes do Bootstrap.
8. Crie uma nova página de listagem com uma tabela Bootstrap.

---

## 15. Atividade 

Desenvolva uma pequena aplicação Spring Boot com Thymeleaf utilizando fragments para padronizar o layout das páginas.

A aplicação deve conter:

- Um layout base;
- Um fragmento para navbar;
- Um fragmento para footer;
- Uma página inicial;
- Uma página de login;
- Uma página de cadastro;
- Uma página de listagem;
- Utilização de Bootstrap;
- Rotas organizadas em um controller.
