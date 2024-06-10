### Introdução à Segurança Web com Spring Security

#### O que você irá construir
Uma aplicação Spring MVC que protege uma página com um formulário de login suportado por uma lista fixa de usuários.

#### Passos

1. **Inicialize o Projeto**
   - Use o [Spring Initializr](https://start.spring.io/) para configurar o projeto com dependências Spring Web e Thymeleaf.
   - Baixe e descompacte o arquivo ZIP resultante.

2. **Crie uma Aplicação Web Não Segura**
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

3. **Configurar o Spring MVC**

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

4. **Configure o Spring Security**
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
   
A aplicação web é baseada no Spring MVC. Portanto, é necessário configurar o Spring MVC e definir controladores de visualização para expor esses templates.
A listagem a seguir (de src/main/java/com/example/securingweb/MvcConfig.java) mostra uma classe que configura o Spring MVC na aplicação:

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
A classe `WebSecurityConfig` é anotada com `@EnableWebSecurity` para habilitar o suporte à segurança web do Spring e fornecer a integração com o Spring MVC. Ela também expõe dois beans para definir alguns detalhes específicos da configuração de segurança web:

- O bean `SecurityFilterChain` define quais caminhos de URL devem ser protegidos e quais não devem. Especificamente, os caminhos `/` e `/home` são configurados para não exigir autenticação. Todos os outros caminhos devem ser autenticados.
  
- Quando um usuário faz login com sucesso, ele é redirecionado para a página solicitada anteriormente que exigia autenticação. Há uma página de login personalizada `/login` (especificada por `loginPage()`) que todos podem visualizar.

- O bean `UserDetailsService` configura um armazenamento de usuários em memória com um único usuário. Esse usuário tem o nome de usuário `user`, a senha `password` e o papel `USER`.
  

5. **Adicione uma Página de Login**
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

6. **Execute a Aplicação**
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
