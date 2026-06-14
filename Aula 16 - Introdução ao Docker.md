# Aula: Dockerizando uma aplicação Spring Boot simples com Docker Compose

## 1. Objetivo da aula

Nesta aula, vamos aprender os primeiros passos com **Docker** utilizando uma aplicação web simples desenvolvida com **Spring Boot**.

A aplicação usada como exemplo está disponível no repositório:

```text
https://github.com/Herysson/SpringDocker
```

A ideia desta aula é que o aluno aprenda, de forma prática, a:

- entender o que é uma imagem Docker;
- entender o que é um container;
- criar um `Dockerfile`;
- criar um arquivo `compose.yml`;
- executar uma aplicação Spring Boot dentro de um container;
- alterar a aplicação e testar novamente com Docker;
- publicar a imagem no Docker Hub;
- executar a aplicação em uma VPS Linux Ubuntu.

Nesta primeira aula, **não utilizaremos banco de dados**. O objetivo é deixar o exemplo mais simples possível para o primeiro contato com Docker.

---

## 2. Antes de começar: o que é Docker?

Docker é uma tecnologia que permite empacotar uma aplicação junto com tudo o que ela precisa para executar.

Em vez de depender diretamente da configuração do computador do usuário, criamos uma **imagem Docker**.

Essa imagem pode ser executada em diferentes ambientes, como:

- computador do desenvolvedor;
- laboratório da universidade;
- servidor Linux;
- VPS;
- ambiente de produção.

---

## 3. Conceitos básicos

### 3.1 Imagem Docker

Uma **imagem** é como um modelo da aplicação.

Ela contém:

- o sistema base necessário;
- o Java;
- o arquivo `.jar` da aplicação;
- as instruções para iniciar o sistema.

Exemplo de imagem:

```text
springdocker:dev
```

---

### 3.2 Container

Um **container** é uma aplicação em execução a partir de uma imagem.

Podemos imaginar assim:

```text
Imagem  ->  modelo da aplicação
Container -> aplicação rodando
```

Uma mesma imagem pode gerar vários containers.

---

### 3.3 Dockerfile

O `Dockerfile` é o arquivo que ensina o Docker a criar a imagem da aplicação.

Nele, definimos passos como:

- qual imagem base usar;
- onde copiar os arquivos;
- como compilar a aplicação;
- qual comando executa o sistema.

---

### 3.4 Docker Compose

O Docker Compose permite configurar e executar containers usando um arquivo chamado:

```text
compose.yml
```

Com ele, evitamos comandos grandes como:

```bash
docker run -d --name springdocker -p 8080:8080 springdocker:dev
```

E passamos a usar comandos mais simples:

```bash
docker compose up -d --build
```

---

## 4. Clonando o projeto

Abra o terminal e execute:

```bash
git clone https://github.com/Herysson/SpringDocker.git
```

Entre na pasta do projeto:

```bash
cd SpringDocker
```

---

## 5. Estrutura inicial do projeto

O projeto possui uma estrutura parecida com esta:

```text
SpringDocker/
├── .mvn/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
│           └── static/
│               └── index.html
├── mvnw
├── mvnw.cmd
├── pom.xml
└── .gitignore
```

O arquivo principal da página está em:

```text
src/main/resources/static/index.html
```

Esse arquivo será exibido quando acessarmos a aplicação no navegador.

---

## 6. Testando a aplicação sem Docker

Antes de usar Docker, podemos testar a aplicação normalmente.

No Windows, execute:

```bash
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS, execute:

```bash
./mvnw spring-boot:run
```

Depois acesse no navegador:

```text
http://localhost:8080
```

Se a aplicação abrir, podemos parar a execução pressionando:

```text
CTRL + C
```

---

## 7. Criando o arquivo Dockerfile

Agora vamos criar o arquivo responsável por gerar a imagem Docker da aplicação.

Na raiz do projeto, crie um arquivo chamado:

```text
Dockerfile
```

O arquivo deve ficar assim:

```text
SpringDocker/
├── src/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── Dockerfile
```

Adicione o seguinte conteúdo no `Dockerfile`:

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 8. Entendendo o Dockerfile

Vamos entender cada parte.

### 8.1 Imagem usada para compilar o projeto

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
```

Essa linha usa uma imagem que já possui:

- Maven;
- Java 17.

Ela será usada para compilar o projeto e gerar o arquivo `.jar`.

---

### 8.2 Pasta de trabalho dentro do container

```dockerfile
WORKDIR /app
```

Define a pasta `/app` como local de trabalho dentro do container.

---

### 8.3 Copiando os arquivos do projeto

```dockerfile
COPY pom.xml .
COPY src ./src
```

Copia o `pom.xml` e a pasta `src` para dentro da imagem.

---

### 8.4 Gerando o arquivo `.jar`

```dockerfile
RUN mvn clean package -DskipTests
```

Esse comando compila o projeto e gera o arquivo `.jar`.

A opção:

```text
-DskipTests
```

faz com que os testes sejam ignorados durante a geração do pacote.

---

### 8.5 Imagem final da aplicação

```dockerfile
FROM eclipse-temurin:17-jre
```

Essa será a imagem final usada para executar a aplicação.

Ela é menor que a imagem com Maven, pois precisa apenas do Java para rodar o `.jar`.

---

### 8.6 Copiando o `.jar` para a imagem final

```dockerfile
COPY --from=build /app/target/*.jar app.jar
```

Copia o arquivo `.jar` gerado na etapa anterior e renomeia para:

```text
app.jar
```

---

### 8.7 Expondo a porta da aplicação

```dockerfile
EXPOSE 8080
```

Informa que a aplicação usa a porta `8080`.

---

### 8.8 Comando para iniciar a aplicação

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Define o comando que será executado quando o container iniciar.

---

## 9. Criando o arquivo .dockerignore

Na raiz do projeto, crie também o arquivo:

```text
.dockerignore
```

Esse arquivo serve para evitar que pastas e arquivos desnecessários sejam enviados para o processo de criação da imagem.

Adicione o seguinte conteúdo:

```text
target
.git
.gitignore
.idea
.vscode
*.iml
```

---

## 10. Criando o arquivo compose.yml

Agora vamos criar o arquivo que facilitará a execução da aplicação.

Na raiz do projeto, crie o arquivo:

```text
compose.yml
```

Adicione o seguinte conteúdo:

```yaml
services:
  app:
    build: .
    image: springdocker:dev
    container_name: springdocker
    ports:
      - "8080:8080"
```

A estrutura do projeto ficará assim:

```text
SpringDocker/
├── src/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── Dockerfile
├── .dockerignore
└── compose.yml
```

---

## 11. Entendendo o compose.yml

### 11.1 Definição dos serviços

```yaml
services:
```

No Compose, cada container é definido como um serviço.

Neste exemplo, temos apenas um serviço: a aplicação Spring Boot.

---

### 11.2 Serviço app

```yaml
app:
```

Nome do serviço que representa a nossa aplicação.

---

### 11.3 Build da imagem

```yaml
build: .
```

Indica que a imagem será criada a partir do `Dockerfile` localizado na pasta atual.

---

### 11.4 Nome da imagem

```yaml
image: springdocker:dev
```

Define o nome da imagem que será criada localmente.

Neste caso:

```text
springdocker:dev
```

A palavra `dev` indica que é uma versão local de desenvolvimento.

---

### 11.5 Nome do container

```yaml
container_name: springdocker
```

Define o nome do container.

Isso facilita comandos como:

```bash
docker logs springdocker
```

---

### 11.6 Mapeamento de portas

```yaml
ports:
  - "8080:8080"
```

Essa configuração conecta:

```text
porta 8080 do computador -> porta 8080 do container
```

Assim, conseguimos acessar a aplicação pelo navegador em:

```text
http://localhost:8080
```

---

## 12. Executando a aplicação com Docker Compose

Agora, na raiz do projeto, execute:

```bash
docker compose up -d --build
```

Esse comando faz três coisas:

1. cria a imagem Docker;
2. cria o container;
3. inicia a aplicação.

A opção:

```text
-d
```

executa o container em segundo plano.

A opção:

```text
--build
```

força a criação ou atualização da imagem.

---

## 13. Verificando se o container está rodando

Execute:

```bash
docker ps
```

Você deverá ver um container chamado:

```text
springdocker
```

---

## 14. Acessando a aplicação

Abra o navegador e acesse:

```text
http://localhost:8080
```

Se tudo estiver correto, a aplicação será exibida.

---

## 15. Visualizando os logs da aplicação

Para acompanhar os logs da aplicação, execute:

```bash
docker compose logs -f
```

Para sair da visualização dos logs, pressione:

```text
CTRL + C
```

---

## 16. Parando a aplicação

Para parar e remover o container, execute:

```bash
docker compose down
```

Esse comando remove o container, mas mantém a imagem criada.

---

## 17. Fazendo uma alteração na aplicação

Agora imagine que precisamos alterar a página inicial da aplicação.

Abra o arquivo:

```text
src/main/resources/static/index.html
```

Substitua o conteúdo por um HTML simples:

```html
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Spring Docker</title>
</head>
<body>
    <h1>Olá Docker!</h1>
    <p>Minha aplicação Spring Boot está rodando dentro de um container.</p>
</body>
</html>
```

Salve o arquivo.

---

## 18. Testando a alteração no Docker

Depois de alterar o código, execute novamente:

```bash
docker compose up -d --build
```

Esse comando reconstrói a imagem e recria o container.

Agora acesse novamente:

```text
http://localhost:8080
```

A nova versão da página deve aparecer.

---

## 19. Fluxo de trabalho para desenvolvimento

Sempre que alterar o projeto, use o seguinte fluxo:

```text
alterar o código
        ↓
salvar os arquivos
        ↓
executar docker compose up -d --build
        ↓
testar no navegador
```

O comando principal será:

```bash
docker compose up -d --build
```

---

## 20. Comandos úteis

### 20.1 Subir a aplicação

```bash
docker compose up -d --build
```

---

### 20.2 Parar a aplicação

```bash
docker compose down
```

---

### 20.3 Ver containers em execução

```bash
docker ps
```

---

### 20.4 Ver todos os containers

```bash
docker ps -a
```

---

### 20.5 Ver imagens Docker

```bash
docker images
```

---

### 20.6 Ver logs

```bash
docker compose logs -f
```

---

### 20.7 Remover a imagem local

Caso queira apagar a imagem criada:

```bash
docker rmi springdocker:dev
```

Se a imagem estiver sendo usada por algum container, primeiro execute:

```bash
docker compose down
```

Depois remova a imagem:

```bash
docker rmi springdocker:dev
```

---

## 21. Publicando a imagem no Docker Hub

Depois de testar a aplicação localmente, podemos publicar a imagem no Docker Hub.

### 21.1 Criar uma conta no Docker Hub

Acesse:

```text
https://hub.docker.com
```

Crie uma conta, caso ainda não tenha.

---

### 21.2 Fazer login no Docker pelo terminal

Execute:

```bash
docker login
```

Informe seu usuário e senha ou token do Docker Hub.

---

### 21.3 Alterar o nome da imagem

Para publicar uma imagem no Docker Hub, ela precisa seguir o formato:

```text
usuario-dockerhub/nome-da-imagem:versao
```

Exemplo:

```text
herysson/springdocker:1.0
```

Você pode criar essa imagem com o comando:

```bash
docker build -t herysson/springdocker:1.0 .
```

Importante: troque `herysson` pelo seu usuário do Docker Hub.

---

### 21.4 Enviar a imagem para o Docker Hub

Execute:

```bash
docker push herysson/springdocker:1.0
```

Novamente, troque `herysson` pelo seu usuário do Docker Hub.

---

## 22. Usando o Compose para gerar a imagem do Docker Hub

Outra opção é alterar o `compose.yml` para gerar a imagem já com o nome correto do Docker Hub.

Exemplo:

```yaml
services:
  app:
    build: .
    image: herysson/springdocker:1.0
    container_name: springdocker
    ports:
      - "8080:8080"
```

Depois execute:

```bash
docker compose build
docker push herysson/springdocker:1.0
```

Essa abordagem é interessante porque mantém o nome da imagem registrado no próprio `compose.yml`.

---

## 23. Preparando a VPS Linux Ubuntu

Agora vamos imaginar que temos uma VPS Linux Ubuntu.

Primeiro, acesse a VPS via SSH:

```bash
ssh usuario@ip-da-vps
```

Exemplo:

```bash
ssh root@123.123.123.123
```

---

## 24. Instalando o Docker na VPS Ubuntu

Na VPS, execute os comandos abaixo.

Atualize os pacotes:

```bash
sudo apt update
```

Instale pacotes necessários:

```bash
sudo apt install -y ca-certificates curl
```

Crie a pasta para a chave do Docker:

```bash
sudo install -m 0755 -d /etc/apt/keyrings
```

Baixe a chave oficial do Docker:

```bash
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
```

Ajuste a permissão da chave:

```bash
sudo chmod a+r /etc/apt/keyrings/docker.asc
```

Adicione o repositório oficial do Docker:

```bash
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

Atualize novamente:

```bash
sudo apt update
```

Instale o Docker:

```bash
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

Teste a instalação:

```bash
sudo docker run hello-world
```

---

## 25. Criando a pasta da aplicação na VPS

Na VPS, crie uma pasta para o projeto:

```bash
mkdir springdocker
```

Entre na pasta:

```bash
cd springdocker
```

---

## 26. Criando o compose.yml da VPS

Na VPS, crie o arquivo:

```bash
nano compose.yml
```

Adicione o seguinte conteúdo:

```yaml
services:
  app:
    image: herysson/springdocker:1.0
    container_name: springdocker
    ports:
      - "8080:8080"
    restart: unless-stopped
```

Atenção: troque `herysson` pelo seu usuário do Docker Hub.

Neste arquivo, não usamos:

```yaml
build: .
```

Na VPS, a imagem não será construída localmente. Ela será baixada do Docker Hub.

---

## 27. Executando a aplicação na VPS

Ainda dentro da pasta `springdocker`, execute:

```bash
sudo docker compose up -d
```

Verifique se o container está rodando:

```bash
sudo docker ps
```

A aplicação estará disponível em:

```text
http://ip-da-vps:8080
```

Exemplo:

```text
http://123.123.123.123:8080
```

---

## 28. Liberando a porta no firewall da VPS

Se a aplicação não abrir no navegador, talvez a porta `8080` esteja bloqueada.

Caso esteja usando `ufw`, execute:

```bash
sudo ufw allow 8080
```

Verifique o status:

```bash
sudo ufw status
```

---

## 29. Atualizando a aplicação na VPS

Imagine que você alterou o projeto localmente e criou uma nova versão:

```text
herysson/springdocker:1.1
```

No computador local, gere e envie a nova imagem:

```bash
docker build -t herysson/springdocker:1.1 .
docker push herysson/springdocker:1.1
```

Na VPS, edite o `compose.yml`:

```bash
nano compose.yml
```

Troque:

```yaml
image: herysson/springdocker:1.0
```

por:

```yaml
image: herysson/springdocker:1.1
```

Depois execute:

```bash
sudo docker compose down
sudo docker compose pull
sudo docker compose up -d
```

Agora a VPS estará executando a nova versão da aplicação.

---

## 30. Fluxo completo: desenvolvimento até VPS

O fluxo completo fica assim:

```text
1. Alterar o código no computador local
2. Testar localmente com docker compose up -d --build
3. Criar uma versão da imagem
4. Enviar para o Docker Hub
5. Acessar a VPS
6. Atualizar o compose.yml da VPS
7. Executar docker compose pull
8. Subir novamente o container
```

---

## 31. Resumo dos arquivos criados

Ao final da aula, adicionamos três arquivos ao projeto.

### 31.1 Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

### 31.2 .dockerignore

```text
target
.git
.gitignore
.idea
.vscode
*.iml
```

---

### 31.3 compose.yml para desenvolvimento local

```yaml
services:
  app:
    build: .
    image: springdocker:dev
    container_name: springdocker
    ports:
      - "8080:8080"
```

---

### 31.4 compose.yml para VPS

```yaml
services:
  app:
    image: herysson/springdocker:1.0
    container_name: springdocker
    ports:
      - "8080:8080"
    restart: unless-stopped
```

---

## 32. Exercício prático

Faça as seguintes alterações na aplicação:

1. Altere o arquivo `index.html` para exibir seu nome.
2. Adicione um parágrafo explicando o que é Docker.
3. Execute novamente a aplicação usando:

```bash
docker compose up -d --build
```

4. Acesse:

```text
http://localhost:8080
```

5. Verifique se a alteração apareceu no navegador.

---

## 33. Exercício extra

Crie uma nova versão da imagem com o seu usuário do Docker Hub.

Exemplo:

```bash
docker build -t seu-usuario/springdocker:1.0 .
docker push seu-usuario/springdocker:1.0
```

Depois, crie um arquivo `compose.yml` de produção usando essa imagem.

---

## 34. Conclusão

Nesta aula, aprendemos a empacotar uma aplicação Spring Boot simples em um container Docker.

Também vimos como o Docker Compose facilita a execução da aplicação, principalmente para iniciantes.

O comando mais importante da aula foi:

```bash
docker compose up -d --build
```

Ele permite criar a imagem, criar o container e executar a aplicação de forma simples.

Com isso, o aluno já entende o fluxo básico:

```text
código-fonte
    ↓
Dockerfile
    ↓
imagem Docker
    ↓
container
    ↓
aplicação rodando
```

Depois disso, publicamos a imagem no Docker Hub e executamos a aplicação em uma VPS Linux Ubuntu.

---
## 35. Fluxo completo resumido para alterações.

No PC:
```bash
.\mvnw.cmd clean package -DskipTests
docker compose down
docker compose up -d --build
docker compose logs -f
docker push herysson/sos-app:1.1
```

Na VPS:
```bash
docker compose down
docker pull herysson/sos-app:1.1
docker compose up -d
docker compose logs -f
```

## 36. Referências

- Repositório SpringDocker: https://github.com/Herysson/SpringDocker
- Documentação Dockerfile: https://docs.docker.com/reference/dockerfile/
- Documentação Docker Compose: https://docs.docker.com/reference/compose-file/
- Comando docker compose up: https://docs.docker.com/reference/cli/docker/compose/up/
- Docker Hub: https://hub.docker.com/
- Publicando imagens no Docker Hub: https://docs.docker.com/docker-hub/repos/manage/hub-images/push/
- Instalação do Docker no Ubuntu: https://docs.docker.com/engine/install/ubuntu/
