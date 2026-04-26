# Atividade - Automação Web com Java, Spring Boot, Arduino

<div align="center">
  <img src="https://github.com/Herysson/Projeto-de-Software/assets/7634437/4a0e840e-9a7e-4932-878e-67fc369f77c8" alt="Imagem 1" width="400"/>
  <img src="https://github.com/Herysson/Projeto-de-Software/assets/7634437/383a870e-1351-4643-a655-2839b613a689" alt="Imagem 2" width="400"/>
</div>

## Comunicação Serial entre Java, Arduino e Spring Web

Nesta atividade, vamos desenvolver uma aplicação simples para controlar LEDs conectados ao Arduino por meio de uma aplicação Java/Spring Boot. A comunicação entre o sistema Java e o Arduino será realizada pela porta serial utilizando a biblioteca **jSerialComm**.

## Objetivos da atividade

* Compreender o funcionamento básico da comunicação serial;
* Programar o Arduino para receber comandos pela porta serial;
* Utilizar Java para enviar dados ao Arduino;
* Integrar a comunicação serial com uma aplicação Spring Boot;
* Criar uma interface web simples para ligar e desligar LEDs.

## Hardware

<div align="center">
  <img src="https://github.com/user-attachments/assets/8d1b2baf-ae43-48ee-bce8-41e52b8b369b" alt="arduino_led_circuit_pwm_pin" />
</div>

* 1 Arduino;
* 1 cabo USB;
* 1 protoboard;
* LEDs;
* Resistores;
* Jumpers.

## Dependência Maven

No arquivo `pom.xml`, adicione a seguinte dependência:

```xml id="pom001"
<dependency>
    <groupId>com.fazecast</groupId>
    <artifactId>jSerialComm</artifactId>
    <version>2.11.0</version>
</dependency>
```

---

## Código Arduino

O código abaixo configura o pino e 13 como saída. Ao receber o *caractere* `'1'`, o LED é ligado. Ao receber o caractere `'2'`, é desligado.

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

---

## Classe Java para comunicação serial

Crie uma classe chamada `ConexaoPorta`:

```java id="java001"

import com.fazecast.jSerialComm.SerialPort;

public class ConexaoPorta {

    private SerialPort serialPort;

    public ConexaoPorta(String portaCOM, int taxa) {
        serialPort = SerialPort.getCommPort(portaCOM);
        serialPort.setBaudRate(taxa);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (serialPort.openPort()) {
            System.out.println("Porta aberta com sucesso: " + portaCOM);
        } else {
            System.out.println("Erro ao abrir a porta: " + portaCOM);
        }
    }

    public void enviaDados(char opcao) {
        if (serialPort != null && serialPort.isOpen()) {
            byte[] dados = new byte[]{(byte) opcao};
            serialPort.writeBytes(dados, dados.length);
            System.out.println("Enviado para o Arduino: " + opcao);
        } else {
            System.out.println("Porta serial não está aberta.");
        }
    }

    public void close() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("Porta fechada.");
        }
    }
}
```

---

## Exemplo de uso da classe

```java id="java002"
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ConexaoPorta conexao = new ConexaoPorta("COM3", 9600);

        conexao.enviaDados('1');
        Thread.sleep(2000);

        conexao.enviaDados('2');

        conexao.close();
    }
}
```

A porta `"COM3"` deve ser substituída pela porta em que o Arduino está conectado. No Windows, normalmente será algo como `COM3`, `COM4` ou `COM5`.

---

## Atenção

Antes de executar a aplicação Java, feche o Serial Monitor do Arduino IDE ou do Arduino Cloud. Apenas um programa pode acessar a porta serial por vez.

Também verifique se a velocidade configurada no Arduino é a mesma configurada no Java:

```cpp id="arduino002"
Serial.begin(9600);
```

```java id="java004"
new ConexaoPorta("COM3", 9600);
```

---

## Requisitos adicionais da atividade

Além da comunicação com o Arduino, o sistema desenvolvido deve seguir uma arquitetura baseada no padrão **MVC**.

A aplicação deve possuir:

- **Model**: classes responsáveis por representar os dados do sistema, como o histórico de acionamento dos LEDs;
- **View**: páginas web responsáveis por exibir os botões de controle e a tabela com o histórico;
- **Controller**: classes responsáveis por receber as requisições da interface web e acionar as regras de negócio;
- **Service**: classe responsável por concentrar a lógica de comunicação com o Arduino e o registro das ações realizadas;
- **Repository**: interface responsável pela persistência dos dados no banco.

Sempre que o usuário clicar em **Ligar** ou **Desligar**, o sistema deve enviar o comando correspondente ao Arduino e registrar no banco de dados um histórico da ação realizada.

O histórico deve conter, no mínimo:

- Identificador do registro;
- Ação realizada: `LIGADO` ou `DESLIGADO`;
- Data e hora da ação.

A página principal do sistema deve exibir:

- Um botão para ligar os LEDs;
- Um botão para desligar os LEDs;
- Uma tabela com o histórico de acionamentos.

---

## Critérios de avaliação

| Critério | Peso |
|---|---:|
| Comunicação serial com o Arduino utilizando jSerialComm | 2,0 |
| Funcionamento correto dos comandos de ligar e desligar LEDs | 3,0 |
| Organização do projeto seguindo a arquitetura MVC | 2,0 |
| Persistência do histórico no banco de dados | 1,5 |
| Exibição do histórico em uma tabela na interface web | 1,5 |
| **Total** | **10,0** |

---
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


