# Aula: Introdução ao Spring Security com MySQL
<p align="center">
<img src="https://media2.dev.to/dynamic/image/width=1000,height=420,fit=cover,gravity=auto,format=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2Fi8dkzpjbni9mcpne1ubq.png" alt="Spring Security">
</p>

Exemplo utilizado: [Spring-Security-MySQL](https://github.com/Herysson/Spring-Security-MySQL)

---

## 1. Objetivos da aula

Ao final desta aula, o aluno deverá ser capaz de:

- compreender o papel do Spring Security em uma aplicação web;
- diferenciar autenticação e autorização;
- entender como o Spring Security pode buscar usuários em um banco de dados;
- compreender o uso de papéis/perfis de acesso, como `ADMIN` e `USER`;
- identificar a função das principais classes de configuração de segurança;
- testar o acesso a páginas públicas, autenticadas e restritas por perfil.

---

## 2. Contextualização

Em muitos sistemas web, nem todas as páginas ou funcionalidades devem estar disponíveis para qualquer pessoa.

Por exemplo, em um sistema acadêmico:

- um aluno pode visualizar suas disciplinas;
- um professor pode lançar notas;
- um coordenador pode acessar relatórios administrativos;
- um administrador pode cadastrar usuários e configurar permissões.

Para controlar esse tipo de acesso, usamos mecanismos de segurança. No ecossistema Spring Boot, uma das ferramentas mais utilizadas para isso é o **Spring Security**.

O exemplo utilizado nesta aula apresenta uma aplicação simples com:

- login personalizado;
- autenticação baseada em usuários salvos no banco MySQL;
- senhas criptografadas com BCrypt;
- controle de acesso por papéis;
- página comum para usuários autenticados;
- painel administrativo acessível apenas para usuários com papel `ADMIN`.

---

## 3. Autenticação e autorização

Antes de analisar o código, é importante diferenciar dois conceitos.

### 3.1 Autenticação

Autenticação é o processo de verificar quem é o usuário.

Exemplo:

> O usuário informa login e senha. O sistema verifica se essas credenciais estão corretas.

No projeto, a autenticação ocorre quando o usuário acessa a página `/login` e informa:

```text
username
password
```

O Spring Security verifica essas informações consultando os dados salvos no banco.

---

### 3.2 Autorização

Autorização é o processo de verificar o que o usuário autenticado pode acessar.

Exemplo:

> O usuário fez login, mas apenas usuários administradores podem acessar o painel administrativo.

No projeto, a rota `/adminpanel` só pode ser acessada por usuários com autoridade `ADMIN`.

---

## 4. Visão geral do projeto

O projeto utiliza as seguintes tecnologias:

- Java 17;
- Spring Boot;
- Spring Security;
- Spring Data JPA;
- Thymeleaf;
- MySQL;
- Maven.

A aplicação possui uma estrutura simples:

```text
src/main/java/com/herysson/springsecuritymysql/
├── configuration/
│   ├── DataInitializer.java
│   └── SecurityConfig.java
├── controller/
│   └── HomeController.java
├── model/
│   └── User.java
├── repository/
│   └── UserRepository.java
├── service/
│   └── CustomUserDetailsService.java
└── SpringSecurityMySqlApplication.java

src/main/resources/
├── templates/
│   ├── login.html
│   ├── index.html
│   └── adminpanel.html
└── application.properties
```

---

## 5. Configuração do banco de dados

O arquivo `application.properties` configura a conexão com o banco MySQL.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/securitydb?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=laboratorio

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### Explicação

A propriedade `spring.datasource.url` define o endereço do banco de dados. Neste exemplo, o banco se chama `securitydb`.

O parâmetro:

```properties
createDatabaseIfNotExist=true
```

permite que o banco seja criado automaticamente caso ele ainda não exista.

A propriedade:

```properties
spring.jpa.hibernate.ddl-auto=update
```

faz com que o Hibernate atualize a estrutura das tabelas com base nas entidades JPA do projeto.

A propriedade:

```properties
spring.jpa.show-sql=true
```

exibe no console os comandos SQL gerados pela aplicação.

Em ambiente de aula isso é útil, pois permite visualizar melhor o que o JPA está fazendo. Em ambiente de produção, essa configuração normalmente deve ser usada com mais cuidado.

---

## 6. Entidade User

A classe `User` representa os usuários do sistema.

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String role;
}
```

### Explicação da classe

A anotação `@Entity` indica que a classe será mapeada para uma tabela no banco de dados.

A anotação:

```java
@Table(name = "users")
```

define que a tabela se chamará `users`.

Isso é importante porque o nome `user` pode gerar conflito em alguns bancos de dados, pois pode ser uma palavra reservada.

O atributo `id` é a chave primária da tabela.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

O atributo `username` representa o nome de usuário usado no login.

```java
@Column(unique = true)
private String username;
```

A anotação `unique = true` impede que existam dois usuários com o mesmo nome de usuário.

O atributo `password` armazena a senha do usuário. Porém, no sistema, a senha não é salva em texto puro. Ela é criptografada com BCrypt antes de ser armazenada.

O atributo `role` representa o papel do usuário no sistema.

Exemplos:

```text
ADMIN
USER
```

---

## 7. Repositório UserRepository

O repositório é responsável por acessar os dados da entidade `User`.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

### Explicação

Ao estender `JpaRepository<User, Long>`, a interface herda vários métodos prontos, como:

- `save`;
- `findById`;
- `findAll`;
- `deleteById`.

O método:

```java
Optional<User> findByUsername(String username);
```

é uma consulta derivada do Spring Data JPA.

Isso significa que o Spring interpreta o nome do método e cria automaticamente a consulta para buscar um usuário pelo campo `username`.

Esse método será essencial para o login, pois o Spring Security precisa localizar o usuário informado no formulário.

---

## 8. Serviço CustomUserDetailsService

A classe `CustomUserDetailsService` integra o banco de dados com o Spring Security.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
```

### O que essa classe faz?

Essa classe informa ao Spring Security como buscar um usuário.

Quando alguém tenta fazer login, o Spring Security chama o método:

```java
loadUserByUsername(String username)
```

Esse método recebe o nome de usuário digitado no formulário e procura esse usuário no banco de dados.

Se o usuário não for encontrado, é lançada a exceção:

```java
UsernameNotFoundException
```

Se o usuário for encontrado, o método retorna um objeto do tipo `UserDetails`.

Esse objeto contém as informações que o Spring Security precisa:

- nome de usuário;
- senha criptografada;
- autoridades/permissões.

A autoridade é criada a partir do campo `role`:

```java
new SimpleGrantedAuthority(user.getRole())
```

Assim, se o usuário possui `role = "ADMIN"`, ele receberá a autoridade `ADMIN`.

---

## 9. Configuração de segurança: SecurityConfig

A classe `SecurityConfig` é uma das partes mais importantes do projeto.

Ela define:

- quais rotas são públicas;
- quais rotas exigem login;
- quais rotas exigem perfil específico;
- qual página será usada para login;
- para onde o usuário será redirecionado após o login;
- como será feito o logout;
- qual algoritmo será usado para criptografar senhas.

---

### 9.1 Habilitando a segurança

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
}
```

A anotação `@Configuration` indica que a classe possui configurações do Spring.

A anotação `@EnableWebSecurity` habilita as configurações de segurança web do Spring Security.

---

### 9.2 Configurando as rotas

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**").permitAll()
            .requestMatchers("/adminpanel").hasAuthority("ADMIN")
            .anyRequest().authenticated()
        );

    return http.build();
}
```

Essa configuração define as permissões das rotas.

A linha:

```java
.requestMatchers("/login", "/css/**").permitAll()
```

permite que qualquer pessoa acesse a página de login e os arquivos CSS.

A linha:

```java
.requestMatchers("/adminpanel").hasAuthority("ADMIN")
```

define que somente usuários com autoridade `ADMIN` podem acessar a página `/adminpanel`.

A linha:

```java
.anyRequest().authenticated()
```

define que qualquer outra requisição exige que o usuário esteja autenticado.

Isso significa que páginas como `/index` só podem ser acessadas depois do login.

---

### 9.3 Configurando o formulário de login

```java
.formLogin(form -> form
    .loginPage("/login")
    .defaultSuccessUrl("/index", true)
    .permitAll()
)
```

A linha:

```java
.loginPage("/login")
```

indica que a aplicação utilizará uma página de login personalizada.

A linha:

```java
.defaultSuccessUrl("/index", true)
```

define que, após o login bem-sucedido, o usuário será redirecionado para `/index`.

O parâmetro `true` força esse redirecionamento, mesmo que o usuário tenha tentado acessar outra página antes.

---

### 9.4 Configurando o logout

```java
.logout(logout -> logout
    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
    .logoutSuccessUrl("/")
)
```

Essa configuração define que o logout será realizado pela rota `/logout` usando o método `GET`.

Após o logout, o usuário será redirecionado para `/`.

Observação: em aplicações reais, é comum utilizar logout por `POST`, pois é uma opção mais segura contra determinados tipos de ataques. Neste exemplo didático, o `GET` facilita o teste em aula usando um link simples.

---

### 9.5 Criptografia de senhas com BCrypt

```java
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Esse método cria um objeto responsável por criptografar e validar senhas.

O BCrypt é usado para evitar que senhas sejam armazenadas em texto puro no banco.

Por exemplo, a senha `123456` não será salva exatamente assim no banco. Ela será convertida em um hash.

Exemplo de hash BCrypt:

```text
$2a$10$...
```

Quando o usuário tenta fazer login, o Spring Security compara a senha digitada com o hash armazenado no banco.

---

### 9.6 Provedor de autenticação

```java
@Bean
public DaoAuthenticationProvider authProvider(CustomUserDetailsService userDetailsService,
                                              BCryptPasswordEncoder encoder) {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(encoder);
    return authProvider;
}
```

O `DaoAuthenticationProvider` é o componente que realiza a autenticação usando dados vindos de um repositório ou serviço.

Nesse caso, ele utiliza:

- `CustomUserDetailsService`, para buscar o usuário no banco;
- `BCryptPasswordEncoder`, para comparar a senha digitada com a senha criptografada.

---

## 10. Inicialização automática de usuários

A classe `DataInitializer` cria usuários de teste automaticamente quando a aplicação inicia.

```java
@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole("USER");
            userRepository.save(user);
        }
    }
}
```

### O que é CommandLineRunner?

`CommandLineRunner` é uma interface do Spring Boot que permite executar um código automaticamente logo após a aplicação iniciar.

Neste projeto, ela é usada para criar dois usuários iniciais:

| Usuário | Senha | Papel |
|---|---|---|
| admin | 123456 | ADMIN |
| user | 123456 | USER |

Antes de criar cada usuário, o sistema verifica se ele já existe:

```java
userRepository.findByUsername("admin").isEmpty()
```

Isso evita que o mesmo usuário seja criado várias vezes a cada execução da aplicação.

As senhas são criptografadas com:

```java
passwordEncoder.encode("123456")
```

---

## 11. Controller da aplicação

A classe `HomeController` define as rotas que retornam as páginas Thymeleaf.

```java
@Controller
public class HomeController {
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/adminpanel")
    public String adminPanel() {
        return "adminpanel";
    }
}
```

### Explicação

A anotação `@Controller` indica que essa classe controla requisições web e retorna páginas HTML.

O método:

```java
@GetMapping("/login")
public String login() {
    return "login";
}
```

retorna o arquivo:

```text
src/main/resources/templates/login.html
```

O método:

```java
@GetMapping("/index")
public String index() {
    return "index";
}
```

retorna a página principal após o login.

O método:

```java
@GetMapping("/adminpanel")
public String adminPanel() {
    return "adminpanel";
}
```

retorna a página administrativa.

Porém, mesmo que o controller permita acessar `/adminpanel`, o acesso real depende da configuração de segurança definida em `SecurityConfig`.

Ou seja, a rota existe, mas somente usuários com autoridade `ADMIN` podem acessá-la.

---

## 12. Página de login

O arquivo `login.html` contém o formulário de autenticação.

```html
<form th:action="@{/login}" method="post">
    <div><input type="text" name="username" placeholder="Username"/></div>
    <div><input type="password" name="password" placeholder="Password"/></div>
    <div><button type="submit">Entrar</button></div>
    <div th:if="${param.error}">Credenciais inválidas.</div>
    <div th:if="${param.logout}">Você saiu com sucesso.</div>
</form>
```

### Pontos importantes

O formulário envia os dados para:

```html
th:action="@{/login}"
```

Essa rota é tratada automaticamente pelo Spring Security.

Os campos precisam ter os nomes:

```html
name="username"
name="password"
```

Esses são os nomes esperados pelo Spring Security por padrão.

A mensagem:

```html
<div th:if="${param.error}">Credenciais inválidas.</div>
```

é exibida quando ocorre erro no login.

A mensagem:

```html
<div th:if="${param.logout}">Você saiu com sucesso.</div>
```

é exibida quando o usuário sai do sistema.

---

## 13. Página inicial autenticada

O arquivo `index.html` é exibido após o login.

```html
<p>Olá, <span sec:authentication="name"></span>! Você foi autenticado com sucesso.</p>
```

Esse trecho usa o Spring Security integrado ao Thymeleaf para exibir o nome do usuário autenticado.

Para isso, a página possui o namespace:

```html
xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
```

Outro trecho importante é:

```html
<a href="/adminpanel" sec:authorize="hasAuthority('ADMIN')">Painel Administrador</a>
```

Esse link só aparece para usuários com autoridade `ADMIN`.

Assim:

- o usuário `admin` verá o link para o painel administrativo;
- o usuário `user` não verá esse link.

---

## 14. Página administrativa

O arquivo `adminpanel.html` representa uma página restrita.

```html
<h1>Admin Panel</h1>
<p>Bem-vindo, administrador!</p>
<a th:href="@{/logout}">Sair</a>
```

Essa página só pode ser acessada por usuários com autoridade `ADMIN`.

Se um usuário comum tentar acessar `/adminpanel`, o Spring Security bloqueará o acesso.

---

## 15. Classe principal da aplicação

A classe principal inicia a aplicação Spring Boot.

```java
@SpringBootApplication
public class SpringSecurityMySqlApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityMySqlApplication.class, args);
    }
}
```

A anotação `@SpringBootApplication` combina configurações importantes do Spring Boot, como:

- configuração automática;
- escaneamento de componentes;
- definição da classe principal da aplicação.

---

## 16. Fluxo de funcionamento do sistema

O funcionamento geral da aplicação pode ser entendido da seguinte forma:

1. A aplicação é iniciada.
2. O Spring Boot carrega as configurações.
3. O JPA conecta ao banco MySQL.
4. A classe `DataInitializer` verifica se os usuários `admin` e `user` existem.
5. Caso não existam, eles são criados com senha criptografada.
6. O usuário acessa `/login`.
7. O usuário informa login e senha.
8. O Spring Security chama `CustomUserDetailsService`.
9. O sistema busca o usuário no banco pelo `username`.
10. O Spring Security compara a senha digitada com a senha criptografada.
11. Se estiver correto, o usuário é autenticado.
12. Após o login, o usuário é redirecionado para `/index`.
13. Se o usuário possuir autoridade `ADMIN`, poderá acessar `/adminpanel`.

---

## 17. Testando a aplicação

### 17.1 Executar o projeto

No terminal, dentro da pasta do projeto, execute:

```bash
mvn spring-boot:run
```

Ou execute a classe principal pela IDE:

```java
SpringSecurityMySqlApplication
```

---

### 17.2 Acessar a página de login

Abra o navegador e acesse:

```text
http://localhost:8080/login
```

---

### 17.3 Testar o usuário comum

Use as credenciais:

```text
Usuário: user
Senha: 123456
```

Resultado esperado:

- o usuário acessa `/index`;
- o nome do usuário aparece na tela;
- o link para o painel administrativo não aparece;
- se tentar acessar `/adminpanel` diretamente, o acesso será bloqueado.

---

### 17.4 Testar o usuário administrador

Use as credenciais:

```text
Usuário: admin
Senha: 123456
```

Resultado esperado:

- o usuário acessa `/index`;
- o nome do usuário aparece na tela;
- o link para o painel administrativo aparece;
- o usuário consegue acessar `/adminpanel`.

---

## 18. Pontos importantes para discussão em aula

### 18.1 Por que não salvar senhas em texto puro?

Salvar senhas em texto puro é uma prática insegura.

Se alguém acessar o banco de dados, poderá visualizar todas as senhas dos usuários.

Por isso, usamos algoritmos como BCrypt para armazenar apenas o hash da senha.

---

### 18.2 Qual é a diferença entre esconder um link e proteger uma rota?

No arquivo `index.html`, o link do painel administrativo só aparece para administradores:

```html
sec:authorize="hasAuthority('ADMIN')"
```

Isso melhora a interface, pois evita mostrar opções que o usuário não pode usar.

Porém, esconder o link não é suficiente para proteger o sistema.

A proteção real acontece em `SecurityConfig`:

```java
.requestMatchers("/adminpanel").hasAuthority("ADMIN")
```

Mesmo que o usuário digite `/adminpanel` manualmente no navegador, o Spring Security bloqueará o acesso caso ele não tenha permissão.

---

### 18.3 Diferença entre role e authority

Neste projeto, o campo `role` é usado diretamente como autoridade:

```java
new SimpleGrantedAuthority(user.getRole())
```

Assim, os valores usados são:

```text
ADMIN
USER
```

Em muitos projetos Spring Security, também é comum usar o prefixo:

```text
ROLE_ADMIN
ROLE_USER
```

Nesse caso, a configuração poderia usar métodos como `hasRole("ADMIN")`.

Neste exemplo, como estamos usando `hasAuthority("ADMIN")`, o valor salvo no banco deve ser exatamente `ADMIN`.

---

## 19. Possíveis melhorias futuras

Este projeto é simples e adequado para fins didáticos. Porém, em uma aplicação real, poderíamos melhorar alguns pontos:

- criar uma tela para cadastro de usuários;
- permitir alteração de senha;
- criar mais papéis de acesso;
- proteger mais rotas;
- criar uma página personalizada para acesso negado;
- usar logout por `POST`;
- validar dados de entrada;
- criar relacionamento entre usuário e permissões;
- separar melhor regras de negócio em services;
- criar DTOs para entrada e saída de dados.

---

## 20. Exercício 1: criar uma página para usuários comuns

Crie uma nova página chamada `userpanel.html`.

Essa página deve ser acessível somente para usuários com autoridade `USER`.

### Requisitos

1. Criar o arquivo:

```text
src/main/resources/templates/userpanel.html
```

2. Criar uma rota no controller:

```java
@GetMapping("/userpanel")
public String userPanel() {
    return "userpanel";
}
```

3. Alterar a classe `SecurityConfig` para permitir acesso à rota `/userpanel` somente para usuários com autoridade `USER`.

4. Adicionar um link na página `index.html` que apareça somente para usuários com autoridade `USER`.

### Resultado esperado

- o usuário `user` deve conseguir acessar `/userpanel`;
- o usuário `admin` não deve acessar `/userpanel`, considerando a regra solicitada;
- o link para `/userpanel` deve aparecer apenas para o usuário comum.

---

## 21. Exercício 2: criar um novo usuário de teste

Altere a classe `DataInitializer` para criar automaticamente um terceiro usuário.

### Requisitos

Crie o seguinte usuário:

```text
Usuário: aluno
Senha: 123456
Papel: USER
```

### Resultado esperado

Ao iniciar a aplicação, o usuário `aluno` deve ser criado automaticamente no banco, caso ainda não exista.

Depois, teste o login com:

```text
Usuário: aluno
Senha: 123456
```

O usuário deve conseguir acessar a página `/index`, mas não deve acessar o painel administrativo.

---

## 22. Desafio opcional

Crie uma página personalizada de acesso negado.

### Sugestão

Criar uma página:

```text
access-denied.html
```

E configurar o Spring Security para redirecionar usuários sem permissão para essa página.

Esse desafio permite discutir com a turma o que acontece quando um usuário está autenticado, mas não possui autorização para acessar determinada rota.

---

