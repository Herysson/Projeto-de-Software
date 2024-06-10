# Introdução à Segurança Web com Spring Security
<p align="center">
<img src="https://media.licdn.com/dms/image/C4E12AQG9PzLTPHvRVA/article-cover_image-shrink_600_2000/0/1615137890447?e=2147483647&v=beta&t=VWwwXtX-MnatXpTgypGqluwX50FTUyRTOaC7P12noBg" alt="Spring Security">
</p>
### O que você irá construir
Uma aplicação Spring MVC que protege uma página com um formulário de login suportado por uma lista fixa de usuários.


## **Inicialize o Projeto**
   - Use o [Spring Initializr](https://start.spring.io/) para configurar o projeto com dependências Spring Web e Thymeleaf.
   - Baixe e descompacte o arquivo ZIP resultante.


##  **Crie uma Aplicação Web Não Segura**
  Antes de aplicar segurança a uma aplicação web, você precisa de uma aplicação web para proteger. Esta seção guia você na criação de uma aplicação web simples. Em seguida, você a protegerá com o Spring Security na próxima seção.

  A aplicação web inclui duas views simples: uma página inicial e uma página "Hello, World". A página inicial é definida no seguinte template Thymeleaf (de src/main/resources/templates/home.html):

  

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org">
    <head>
        <title>Spring Security Example</title>
    </head>
    <body>
        <h1>Welcome!</h1>

        <p>Click <a th:href="@{/hello}">here</a> to see a greeting.</p>
    </body>
</html>
```

Essa visualização simples inclui um link para a página /hello, que é definida no seguinte template Thymeleaf (de src/main/resources/templates/hello.html):

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org">
    <head>
        <title>Hello World!</title>
    </head>
    <body>
        <h1>Hello world!</h1>
    </body>
</html>
```

## **Configurar o Spring MVC**

A aplicação web é baseada no Spring MVC. Portanto, é necessário configurar o Spring MVC e definir controladores de visualização para expor esses templates.
A listagem a seguir (de src/main/java/com/example/securingweb/MvcConfig.java) mostra uma classe que configura o Spring MVC na aplicação:

```java
package com.example.securingweb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/home").setViewName("home");
		registry.addViewController("/").setViewName("home");
		registry.addViewController("/hello").setViewName("hello");
		registry.addViewController("/login").setViewName("login");
	}

}

```
O método `addViewControllers()` (que sobrescreve o método com o mesmo nome em `WebMvcConfigurer`) adiciona quatro controladores de visualização. 
Dois dos controladores de visualização referenciam a view cujo nome é `home` (definida em `home.html`), e outro referencia a view chamada `hello` (definida em `hello.html`). 
O quarto controlador de visualização referencia outra view chamada `login`.

## **Configure o Spring Security**
   - Adicione as dependências do Spring Security ao `build.gradle` ou `pom.xml`.
   - Crie uma configuração de segurança (`WebSecurityConfig.java`) para proteger a página `/hello`.

Com Maven, você precisa adicionar duas entradas extras (uma para a aplicação e outra para testes) ao elemento `<dependencies>` no `pom.xml`, conforme mostrado na listagem a seguir:

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
	<groupId>org.thymeleaf.extras</groupId>
	<artifactId>thymeleaf-extras-springsecurity6</artifactId>
	<!-- Temporary explicit version to fix Thymeleaf bug -->
	<version>3.1.1.RELEASE</version>
</dependency>
<dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-test</artifactId>
	<scope>test</scope>
</dependency>
```
   
A seguinte configuração de segurança (de src/main/java/com/example/securingweb/WebSecurityConfig.java) garante que apenas usuários autenticados possam ver a saudação secreta:

```java
package com.example.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
			)
			.logout((logout) -> logout.permitAll());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user =
			 User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(user);
	}
}
```


### **Anotações e Configuração Básica**
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
```
- `@Configuration`: Indica que esta classe é uma classe de configuração Spring.
- `@EnableWebSecurity`: Habilita a segurança web do Spring Security.

### **Configuração do Filtro de Segurança**
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	http
		.authorizeHttpRequests((requests) -> requests
			.requestMatchers("/", "/home").permitAll()
			.anyRequest().authenticated()
		)
		.formLogin((form) -> form
			.loginPage("/login")
			.permitAll()
		)
		.logout((logout) -> logout.permitAll());

	return http.build();
}
```
- `@Bean`: Define um bean gerenciado pelo Spring.
- `securityFilterChain(HttpSecurity http)`: Método que configura a cadeia de filtros de segurança.

### **Configurações Dentro do Método `securityFilterChain`**
- `http.authorizeHttpRequests(...)`: Configura a autorização de requisições HTTP.
  - `requestMatchers("/", "/home").permitAll()`: Permite acesso sem autenticação às URLs raiz (`/`) e `/home`.
  - `anyRequest().authenticated()`: Exige autenticação para qualquer outra requisição.

- `http.formLogin(...)`: Configura o formulário de login.
  - `form.loginPage("/login").permitAll()`: Define a página de login personalizada em `/login` e permite acesso a todos.

- `http.logout(...)`: Configura o logout.
  - `logout.permitAll()`: Permite que todos os usuários acessem a funcionalidade de logout.

- `return http.build()`: Constrói a cadeia de filtros de segurança configurada.

**Serviço de Detalhes do Usuário**
```java
@Bean
public UserDetailsService userDetailsService() {
	UserDetails user =
		 User.withDefaultPasswordEncoder()
			.username("user")
			.password("password")
			.roles("USER")
			.build();

	return new InMemoryUserDetailsManager(user);
}
```
- `userDetailsService()`: Método que define um serviço de detalhes do usuário.
- `User.withDefaultPasswordEncoder()`: Cria um usuário com um codificador de senha padrão (para fins de demonstração, não recomendado para produção).
- `.username("user").password("password").roles("USER").build()`: Define um usuário com nome de usuário "user", senha "password" e papel "USER".
- `return new InMemoryUserDetailsManager(user)`: Retorna um `InMemoryUserDetailsManager` que gerencia usuários na memória.


## **Adicione uma Página de Login**
   - Crie a view `login.html` que captura o nome de usuário e senha e envia para `/login`.
   - Atualize `hello.html` para exibir o nome do usuário atual e fornecer um formulário de logout.
  
  Agora você precisa criar a página de login. Já existe um controlador de visualização para a view de login, 
  então você só precisa criar a própria view de login, conforme mostrado na listagem a seguir (de src/main/resources/templates/login.html):

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org">
    <head>
        <title>Spring Security Example </title>
    </head>
    <body>
        <div th:if="${param.error}">
            Invalid username and password.
        </div>
        <div th:if="${param.logout}">
            You have been logged out.
        </div>
        <form th:action="@{/login}" method="post">
            <div><label> User Name : <input type="text" name="username"/> </label></div>
            <div><label> Password: <input type="password" name="password"/> </label></div>
            <div><input type="submit" value="Sign In"/></div>
        </form>
    </body>
</html>
```
Este template Thymeleaf apresenta um formulário que captura um nome de usuário e uma senha e os envia para /login.
Conforme configurado, o Spring Security fornece um filtro que intercepta essa solicitação e autentica o usuário. 
Se o usuário não conseguir autenticar, a página é redirecionada para /login?error, e sua página exibe a mensagem de erro apropriada. 
Ao sair com sucesso, sua aplicação é enviada para /login?logout, e sua página exibe a mensagem de sucesso apropriada.

Por fim, você precisa fornecer ao visitante uma maneira de exibir o nome do usuário atual e sair. 
Para fazer isso, atualize o hello.html para cumprimentar o usuário atual e conter um formulário de saída, conforme mostrado na listagem a seguir (de src/main/resources/templates/hello.html):

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org"
      xmlns:sec="https://www.thymeleaf.org/thymeleaf-extras-springsecurity6">
    <head>
        <title>Hello World!</title>
    </head>
    <body>
        <h1 th:inline="text">Hello <span th:remove="tag" sec:authentication="name">thymeleaf</span>!</h1>
        <form th:action="@{/logout}" method="post">
            <input type="submit" value="Sign Out"/>
        </form>
    </body>
</html>
```
Exibimos o nome de usuário utilizando a integração do Thymeleaf com o Spring Security. 
O formulário "Sign Out" envia um POST para /logout. Ao sair com sucesso, o usuário é redirecionado para /login?logout.

## **Execute a Aplicação**
   - Execute a aplicação com Gradle (`./gradlew bootRun`) ou Maven (`./mvnw spring-boot:run`).
   - Acesse `http://localhost:8080`, faça login com `user` e `password`.

```java

package com.example.securingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecuringWebApplication {

	public static void main(String[] args) throws Throwable {
		SpringApplication.run(SecuringWebApplication.class, args);
	}

}

```
Você pode executar a aplicação a partir da linha de comando com Gradle ou Maven. 
Também pode criar um único arquivo JAR executável que contém todas as dependências, classes e recursos necessários e executá-lo. 
Construir um JAR executável facilita o envio, versionamento e implantação do serviço como uma aplicação ao longo do ciclo de desenvolvimento, em diferentes ambientes, etc.

Se você usar Maven, pode executar a aplicação usando `./mvnw spring-boot:run`. 
Alternativamente, pode construir o arquivo JAR com `./mvnw clean package` e, em seguida, executar o arquivo JAR, da seguinte forma:

```bash
java -jar target/gs-securing-web-0.1.0.jar
```


### Referências

1. **Documentação Oficial do Spring Security**:
   - [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html).

2. **Guia de Início Rápido**:
   - [Spring Security Quickstart](https://spring.io/guides/gs/securing-web/)

3. **Livro**:
   - "Spring Security in Action" por Laurentiu Spilca

4. **Artigos e Tutoriais**:
   - [Baeldung Spring Security Tutorials](https://www.baeldung.com/spring-security-tutorial)

5. **Vídeos**:
   - [Spring Security Playlist no YouTube](https://www.youtube.com/playlist?list=PLqq-6Pq4lTTZSKAFG6aCDVDP86Qx4lNas)
