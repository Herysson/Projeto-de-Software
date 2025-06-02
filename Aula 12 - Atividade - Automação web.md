<div align="center">
  <img src="https://github.com/Herysson/Projeto-de-Software/assets/7634437/4a0e840e-9a7e-4932-878e-67fc369f77c8" alt="Imagem 1" width="400"/>
  <img src="https://github.com/Herysson/Projeto-de-Software/assets/7634437/383a870e-1351-4643-a655-2839b613a689" alt="Imagem 2" width="400"/>
</div>



# **Comunicação Serial entre Java, Arduino e Spring Web**

Bem-vindos à nossa aula prática sobre Comunicação Serial entre Java, Arduino e Spring Web! Hoje, vamos explorar como integrar esses poderosos recursos para criar uma aplicação completa que comunica com dispositivos físicos através de uma interface web.

**Objetivos da Aula:**
1. **Compreender os Fundamentos da Comunicação Serial**:
   - Entender o que é comunicação serial e como ela funciona.
   - Conhecer os principais protocolos e padrões utilizados, como RS232.

2. **Configuração do Arduino**:
   - Programar o Arduino para enviar e receber dados através da porta serial.
   - Explorar bibliotecas e comandos específicos do Arduino para comunicação serial.

3. **Integração do Arduino com Java**:
   - Utilizar bibliotecas como RXTX ou JSerialComm para estabelecer comunicação serial entre Java e o Arduino.
   - Implementar um aplicativo Java que possa ler e escrever dados na porta serial.

4. **Desenvolvimento de uma Interface Web com Spring**:
   - Configurar um projeto Spring Boot para servir como intermediário entre a aplicação web e o Arduino.
   - Criar endpoints RESTful para enviar comandos ao Arduino e receber dados.

5. **Construção de uma Aplicação Web Interativa**:
   - Desenvolver uma interface web utilizando tecnologias como HTML, CSS e JavaScript para interagir com o backend Spring.
   - Enviar comandos e receber respostas do Arduino através da aplicação web.

## Hardware
<div align="center">
  <img src="https://github.com/user-attachments/assets/8d1b2baf-ae43-48ee-bce8-41e52b8b369b" alt="arduino_led_circuit_pwm_pin" />
</div>

## **Download dos Arquivos**

Para iniciar nossa aula prática sobre comunicação serial entre Java, Arduino e Spring Web, o primeiro passo é baixar os arquivos necessários para configurar nosso ambiente de desenvolvimento. Esses arquivos incluem bibliotecas, exemplos de código e esboços de configuração.

### Instruções para o Download

Acesse o [site](https://jlog.org/rxtx.html) e faça download dos arquivos rxtxSerial.dll, e adicionar a dependência RXTX ao nosso projeto Spring Boot usando Maven. Aqui está como você pode fazer isso:

### Adicionando a Dependência RXTX ao Projeto Maven

1. **Abra o arquivo `pom.xml`** do seu projeto Spring Boot.

2. **Adicione a dependência RXTX** dentro da tag `<dependencies>`:

```xml
<dependencies>
    <!-- Outras dependências do seu projeto -->

    <!-- Dependência RXTX para comunicação serial -->
    <dependency>
	<groupId>org.rxtx</groupId>
	<artifactId>rxtx</artifactId>
	<version>2.1.7</version>
        </dependency>
    </dependencies>
```

3. **Atualize o Maven**:
   - Se estiver usando uma IDE como IntelliJ IDEA, Eclipse ou VSCode, use a opção de atualizar o projeto Maven para baixar e incluir a nova dependência no projeto.

### Instalação dos arquivos
1. **Windows** Copie o arquivo rxtxSerial.dll para:
- C:\Program Files\Java\jdkx.x.x\bin, onde x.x.x é a versão do JDK, por exemplo C:\Program Files\Java\jdk1.6.40\bin;
- C:\Program Files\Java\jrex\bin,  onde x é a versão do JRE, por exemplo C:\Program Files\Java\jre7\bin;
- C:\Windows\System32;
- C:\Windows\SysWOW64 (caso sistema operacional 64-bits (x64)).

## Programa Arduino
### Código para Arduino

O código a seguir configura o Arduino para receber comandos pela porta serial e controlar um LED conectado ao pino 13. A comunicação é feita com uma frequência de 9600 bps. Dependendo do dado recebido, o LED será ligado ou desligado.

```cpp
int ledPin = 13; // atribui o pino 13 à variável ledPin 
int dado; // variável que receberá os dados da porta serial

void setup(){
  Serial.begin(9600); // frequência da porta serial
  pinMode(ledPin, OUTPUT); // define o pino o ledPin como saída
}

void loop(){
  if(Serial.available() > 0){ // verifica se existe comunicação com a porta serial
    dado = Serial.read(); // lê os dados da porta serial
    switch(dado){
      case '1':
        digitalWrite(ledPin, HIGH); // liga o pino ledPin
        break;
      case '2':
        digitalWrite(ledPin, LOW); // desliga o pino ledPin
        break;
    }
  }
}
```

### Explicação do Código

- **Variáveis**:
  - `int ledPin = 13;`: Define o pino 13 como o pino onde o LED está conectado.
  - `int dado;`: Variável que armazena os dados recebidos pela porta serial.

- **Função `setup()`**:
  - `Serial.begin(9600);`: Inicializa a comunicação serial com a frequência de 9600 bps.
  - `pinMode(ledPin, OUTPUT);`: Configura o pino 13 como saída.

- **Função `loop()`**:
  - `if(Serial.available() > 0)`: Verifica se há dados disponíveis na porta serial.
  - `dado = Serial.read();`: Lê o dado da porta serial.
  - `switch(dado)`: Avalia o valor do dado recebido:
    - `case 1`: Liga o LED no pino 13.
    - `case 2`: Desliga o LED no pino 13.
      
## Código para abrir Conexão e Enviar dados
### Código para Abrir Conexão e Enviar Dados

O código Java abaixo demonstra como abrir uma conexão serial com um Arduino e enviar dados através dessa conexão. Ele utiliza a biblioteca RXTX para comunicação serial.

```java
package br.com.embarcados.comunicacaoserial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JOptionPane;

public class ControlePorta {
  private OutputStream serialOut;
  private int taxa;
  private String portaCOM;

  /**
   * Construtor da classe ControlePorta
   * @param portaCOM - Porta COM que será utilizada para enviar os dados para o arduino
   * @param taxa - Taxa de transferência da porta serial geralmente é 9600
   */
  public ControlePorta(String portaCOM, int taxa) {
    this.portaCOM = portaCOM;
    this.taxa = taxa;
    this.initialize();
  }     
 
  /**
   * Método que verifica se a comunicação com a porta serial está ok
   */
  private void initialize() {
    try {
      // Define uma variável portId do tipo CommPortIdentifier para realizar a comunicação serial
      CommPortIdentifier portId = null;
      try {
        // Tenta verificar se a porta COM informada existe
        portId = CommPortIdentifier.getPortIdentifier(this.portaCOM);
      } catch (NoSuchPortException npe) {
        // Caso a porta COM não exista, será exibido um erro 
        JOptionPane.showMessageDialog(null, "Porta COM não encontrada.",
                  "Porta COM", JOptionPane.PLAIN_MESSAGE);
        return;
      }
      // Abre a porta COM 
      SerialPort port = (SerialPort) portId.open("Comunicação serial", this.taxa);
      serialOut = port.getOutputStream();
      port.setSerialPortParams(this.taxa, // taxa de transferência da porta serial 
                               SerialPort.DATABITS_8, // taxa de 10 bits 8 (envio)
                               SerialPort.STOPBITS_1, // taxa de 10 bits 1 (recebimento)
                               SerialPort.PARITY_NONE); // receber e enviar dados
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Método que fecha a comunicação com a porta serial
   */
  public void close() {
    try {
        serialOut.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Não foi possível fechar porta COM.",
                "Fechar porta COM", JOptionPane.PLAIN_MESSAGE);
    }
  }

  /**
   * @param opcao - Valor a ser enviado pela porta serial
   */
  public void enviaDados(int opcao) {
    try {
      serialOut.write(opcao); // escreve o valor na porta serial para ser enviado
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(null, "Não foi possível enviar o dado.",
                "Enviar dados", JOptionPane.PLAIN_MESSAGE);
    }
  } 
}
```

### Explicação do Código

- **Construtor `ControlePorta`**:
  - Inicializa a porta COM e a taxa de transferência (geralmente 9600 bps).

- **Método `initialize`**:
  - Verifica se a porta COM existe e abre a porta serial.
  - Configura os parâmetros da porta serial (taxa de transferência, bits de dados, bits de parada, paridade).

- **Método `close`**:
  - Fecha a conexão com a porta serial.

- **Método `enviaDados`**:
  - Envia dados para o Arduino pela porta serial.

### Utilização

1. **Adicionar a Dependência RXTX no Maven**:
   Certifique-se de que você adicionou a dependência RXTX no seu arquivo `pom.xml`.

   ```xml
   <dependency>
	<groupId>org.rxtx</groupId>
	<artifactId>rxtx</artifactId>
	<version>2.1.7</version>
        </dependency>
    </dependencies>
   ```

2. **Inicializar a Classe e Enviar Dados**:

   ```java
   public class Main {
     public static void main(String[] args) {
       ControlePorta controle = new ControlePorta("COM3", 9600); // Substitua "COM3" pela sua porta COM

       // Enviar dados
       controle.enviaDados('1'); // Liga o LED
       try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
       controle.enviaDados('2'); // Desliga o LED

       // Fechar a porta
       controle.close();
     }
   }
   ```

Certifique-se de que a porta COM especificada (como "COM3") corresponde à porta onde o Arduino está conectado. 
Este código demonstra como abrir uma conexão serial, enviar dados para o Arduino e fechar a conexão após o uso.

## Referências

1. **Artigo: Comunicação Serial Java e Arduino**
   - Embarcados. "Comunicação Serial Java e Arduino". Disponível em: [https://embarcados.com.br/comunicacao-serial-java-arduino/](https://embarcados.com.br/comunicacao-serial-java-arduino/)

2. **Livro: Getting Started with Arduino**
   - Banzi, Massimo. "Getting Started with Arduino". O'Reilly Media, 2nd Edition, 2011. 
3. **Livro: Arduino Cookbook**
   - Margolis, Michael. "Arduino Cookbook". O'Reilly Media, 2nd Edition, 2011. 

4. **Documentação oficial do Arduino**
   - Arduino. "Arduino Documentation". Disponível em: [https://www.arduino.cc/en/Guide](https://www.arduino.cc/en/Guide).

5. **Documentação da biblioteca RXTX**
   - RXTX. "RXTX Serial Communication API". Disponível em: [https://fizzed.com/oss/rxtx-for-java](https://fizzed.com/oss/rxtx-for-java). 

6. **Tutorial: Java Simple Serial Connector (jSSC)**
   - Java Simple Serial Connector (jSSC). "jSSC Documentation". Disponível em: [https://code.google.com/archive/p/java-simple-serial-connector/wikis/jSSC_examples.wiki](https://code.google.com/archive/p/java-simple-serial-connector/wikis/jSSC_examples.wiki). 

7. **Site: Instructables - Projetos Arduino**
   - Instructables. "Arduino Projects". Disponível em: [https://www.instructables.com/howto/arduino/](https://www.instructables.com/howto/arduino/). 

8. **Ferramenta: Fritzing**
   - Fritzing. "Fritzing Software". Disponível em: [http://fritzing.org/home/](http://fritzing.org/home/). 
