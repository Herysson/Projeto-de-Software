# Design Patterns

Design patterns, ou padrões de design, são soluções reutilizáveis para problemas comuns que ocorrem frequentemente no desenvolvimento de software. 
Eles são como "receitas" que podem ser aplicadas em várias situações para resolver problemas de design e arquitetura de software de maneira eficiente.

## Objetivo dos Design Patterns

Os design patterns são usados para:

- **Promover boas práticas:** Eles incentivam soluções que seguem princípios de design de software bem estabelecidos, como encapsulamento, modularidade e reutilização.
- **Facilitar a comunicação:** Proporcionam uma linguagem comum para desenvolvedores discutirem soluções de design. Termos como "Singleton", "Observer" e "Factory" são rapidamente compreendidos por desenvolvedores experientes.
- **Aumentar a reutilização:** Fornecem soluções testadas e comprovadas que podem ser aplicadas a diferentes problemas de software, economizando tempo e esforço.
- **Melhorar a manutenção:** Soluções bem projetadas são mais fáceis de entender, modificar e manter ao longo do tempo.

## Creational Patterns (Padrões de Criação):
Lidam com a criação de objetos, escondendo a lógica de criação e fazendo com que o sistema seja independente de como seus objetos são criados e compostos.
Exemplo: Singleton, Factory Method, Abstract Factory, Builder, Prototype.

Os padrões de criação (Creational Patterns) tratam da maneira como os objetos são criados. Eles abstraem o processo de instanciação, tornando o sistema mais flexível e reutilizável. Aqui estão explicações sobre os padrões Builder, Factory Method e Singleton:

### Builder (Construtor)
O padrão Builder separa a construção de um objeto complexo da sua representação, permitindo que o mesmo processo de construção crie diferentes representações.

#### Estrutura
- **Builder**: Interface ou classe abstrata que define as etapas para construir as partes de um produto.
- **ConcreteBuilder**: Implementa a interface Builder para construir e montar partes específicas do produto.
- **Product**: Representa o objeto complexo que está sendo construído.
- **Director**: Constrói um objeto usando a interface Builder.

#### Exemplo
Imagine a construção de uma casa, onde o processo de construção pode variar (casa de madeira, casa de tijolos).

```java
class House {
    private String foundation;
    private String structure;
    private String roof;

    public void setFoundation(String foundation) { this.foundation = foundation; }
    public void setStructure(String structure) { this.structure = structure; }
    public void setRoof(String roof) { this.roof = roof; }
}

interface HouseBuilder {
    void buildFoundation();
    void buildStructure();
    void buildRoof();
    House getHouse();
}

class WoodenHouseBuilder implements HouseBuilder {
    private House house = new House();

    public void buildFoundation() { house.setFoundation("Wooden Foundation"); }
    public void buildStructure() { house.setStructure("Wooden Structure"); }
    public void buildRoof() { house.setRoof("Wooden Roof"); }
    public House getHouse() { return house; }
}

class HouseDirector {
    private HouseBuilder builder;

    public HouseDirector(HouseBuilder builder) { this.builder = builder; }

    public House constructHouse() {
        builder.buildFoundation();
        builder.buildStructure();
        builder.buildRoof();
        return builder.getHouse();
    }
}

// Uso
HouseBuilder builder = new WoodenHouseBuilder();
HouseDirector director = new HouseDirector(builder);
House house = director.constructHouse();
```

### Factory Method (Método de Fábrica)
O padrão Factory Method define uma interface para criar um objeto, mas deixa as subclasses decidirem qual classe instanciar. Ele permite que uma classe adie a instanciação para subclasses.

#### Estrutura
- **Product**: Interface ou classe abstrata para os objetos que a fábrica cria.
- **ConcreteProduct**: Implementa a interface Product.
- **Creator**: Declara o método de fábrica, que retorna um objeto do tipo Product. Pode definir uma implementação padrão.
- **ConcreteCreator**: Subclasses que implementam o método de fábrica para retornar uma instância de ConcreteProduct.

#### Exemplo
Imagine um sistema de gerenciamento de documentos que pode criar diferentes tipos de documentos (PDF, Word).

```java
interface Document {
    void open();
}

class PDFDocument implements Document {
    public void open() { System.out.println("Opening PDF Document"); }
}

class WordDocument implements Document {
    public void open() { System.out.println("Opening Word Document"); }
}

abstract class DocumentCreator {
    public abstract Document createDocument();

    public void openDocument() {
        Document doc = createDocument();
        doc.open();
    }
}

class PDFDocumentCreator extends DocumentCreator {
    public Document createDocument() {
        return new PDFDocument();
    }
}

class WordDocumentCreator extends DocumentCreator {
    public Document createDocument() {
        return new WordDocument();
    }
}

// Uso
DocumentCreator creator = new PDFDocumentCreator();
creator.openDocument();
```

### Singleton (Singleton)
O padrão Singleton garante que uma classe tenha apenas uma instância e fornece um ponto de acesso global a essa instância.

#### Estrutura
- **Singleton**: Classe que garante que só pode haver uma instância. Ela mantém uma referência estática para a única instância e fornece um método estático para acessar essa instância.

#### Exemplo
Imagine um sistema de configuração de aplicativo onde as configurações precisam ser acessíveis globalmente.

```java
class ConfigurationManager {
    private static ConfigurationManager instance;
    private Properties properties;

    private ConfigurationManager() {
        properties = new Properties();
        // Carrega as propriedades de um arquivo de configuração
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}

// Uso
ConfigurationManager config = ConfigurationManager.getInstance();
String value = config.getProperty("someKey");
```

## Structural Patterns (Padrões Estruturais): 
Lidam com a composição de classes e objetos, formando estruturas maiores e mais complexas.
Exemplo: Adapter, Composite, Proxy, Flyweight, Facade, Bridge, Decorator.

Os padrões estruturais (Structural Patterns) são um grupo de padrões de design que lidam com a composição de classes e objetos para formar estruturas maiores e mais complexas. Eles ajudam a garantir que as partes de um sistema funcionem bem juntas. Aqui estão explicações sobre os padrões Composite, Decorator e Facade:

### Composite (Composto)
O padrão Composite é usado para tratar objetos individuais e composições de objetos de maneira uniforme. Ele permite que você trate objetos individuais e composições de objetos de maneira homogênea.

#### Estrutura
- **Component**: Declara a interface para objetos da composição e declara operações que podem ser aplicadas tanto a objetos simples quanto a composições de objetos.
- **Leaf**: Representa objetos folha na composição. Uma folha não tem filhos.
- **Composite**: Define comportamentos para componentes que têm filhos. Armazena subcomponentes e implementa operações relacionadas à manipulação de filhos.

#### Exemplo
Imagine um sistema de gerenciamento de arquivos. Um arquivo individual é uma `Leaf`, enquanto uma pasta que contém arquivos e outras pastas é um `Composite`. Ambos implementam a mesma interface `Component`.

```java
interface Graphic {
    void draw();
}

class Dot implements Graphic {
    public void draw() {
        System.out.println("Dot");
    }
}

class Circle implements Graphic {
    public void draw() {
        System.out.println("Circle");
    }
}

class CompoundGraphic implements Graphic {
    private List<Graphic> children = new ArrayList<>();

    public void add(Graphic graphic) {
        children.add(graphic);
    }

    public void draw() {
        for (Graphic child : children) {
            child.draw();
        }
    }
}
```

### Decorator (Decorador)
O padrão Decorator permite adicionar funcionalidades a um objeto dinamicamente. Ele fornece uma alternativa flexível à herança para estender funcionalidades.

#### Estrutura
- **Component**: Interface ou classe abstrata para objetos que podem ter responsabilidades adicionais.
- **ConcreteComponent**: Implementação concreta do `Component`, à qual podemos adicionar responsabilidades.
- **Decorator**: Classe abstrata que implementa a interface `Component` e possui uma referência a um objeto `Component`.
- **ConcreteDecorator**: Adiciona responsabilidades ao `Component`.

#### Exemplo
Imagine um sistema de mensagens que precisa de várias funcionalidades, como criptografia e compressão. Podemos criar decoradores para adicionar essas funcionalidades.

```java
interface Message {
    String getContent();
}

class TextMessage implements Message {
    private String content;

    public TextMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

class MessageDecorator implements Message {
    protected Message message;

    public MessageDecorator(Message message) {
        this.message = message;
    }

    public String getContent() {
        return message.getContent();
    }
}

class EncryptedMessage extends MessageDecorator {
    public EncryptedMessage(Message message) {
        super(message);
    }

    public String getContent() {
        return "Encrypted(" + message.getContent() + ")";
    }
}

class CompressedMessage extends MessageDecorator {
    public CompressedMessage(Message message) {
        super(message);
    }

    public String getContent() {
        return "Compressed(" + message.getContent() + ")";
    }
}
```

### Facade (Fachada)
O padrão Facade fornece uma interface simplificada para um subsistema complexo. Ele oculta as complexidades do subsistema e fornece uma interface mais fácil de usar.

#### Estrutura
- **Facade**: Fornece uma interface simplificada para as funcionalidades do subsistema.
- **Subsystem Classes**: Implementam funcionalidades complexas e são usadas pela Facade.

#### Exemplo
Imagine um sistema de home theater com vários componentes como DVD player, projetor e som. A `Facade` simplifica a interação com esses componentes.

```java
class DVDPlayer {
    public void on() { System.out.println("DVD Player on"); }
    public void play() { System.out.println("DVD Playing"); }
}

class Projector {
    public void on() { System.out.println("Projector on"); }
    public void setInput() { System.out.println("Projector input set to DVD"); }
}

class SoundSystem {
    public void on() { System.out.println("Sound system on"); }
    public void setVolume(int level) { System.out.println("Volume set to " + level); }
}

class HomeTheaterFacade {
    private DVDPlayer dvdPlayer;
    private Projector projector;
    private SoundSystem soundSystem;

    public HomeTheaterFacade(DVDPlayer dvdPlayer, Projector projector, SoundSystem soundSystem) {
        this.dvdPlayer = dvdPlayer;
        this.projector = projector;
        this.soundSystem = soundSystem;
    }

    public void watchMovie() {
        dvdPlayer.on();
        projector.on();
        projector.setInput();
        soundSystem.on();
        soundSystem.setVolume(10);
        dvdPlayer.play();
    }
}
```

## Behavioral Patterns (Padrões Comportamentais): 
Lidam com a comunicação entre objetos, distribuindo responsabilidades e definindo algoritmos e fluxos de controle.
Exemplo: Observer, Strategy, Command, Chain of Responsibility, Mediator, Iterator, State, Visitor, Template Method, Memento.

Os padrões comportamentais (Behavioral Patterns) se concentram em como os objetos interagem e comunicam entre si. Eles ajudam a definir maneiras de gerenciar algoritmos, relacionamentos e responsabilidades entre objetos. Aqui estão explicações sobre os padrões Observer, Strategy e Mediator:

### Observer (Observador)
O padrão Observer define uma dependência um-para-muitos entre objetos, de modo que quando um objeto muda de estado, todos os seus dependentes são notificados e atualizados automaticamente. Ele é útil para implementar um sistema de eventos.

#### Estrutura
- **Subject**: Mantém uma lista de dependentes (observers) e fornece métodos para adicionar, remover e notificar observers.
- **Observer**: Define uma interface para atualizar os objetos que dependem do Subject.
- **ConcreteSubject**: Implementa o Subject e notifica os observers quando seu estado muda.
- **ConcreteObserver**: Implementa a interface Observer e atualiza seu estado para manter-se consistente com o Subject.

#### Exemplo
Imagine um sistema de notificações de notícias. Quando uma nova notícia é publicada, todos os inscritos (observers) são notificados.

```java
interface Observer {
    void update(String news);
}

class NewsChannel implements Observer {
    private String news;

    public void update(String news) {
        this.news = news;
        System.out.println("NewsChannel received news: " + news);
    }
}

class NewsAgency {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String news) {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }
}
```

### Strategy (Estratégia)
O padrão Strategy define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. O padrão permite que o algoritmo varie independentemente dos clientes que o utilizam.

#### Estrutura
- **Strategy**: Interface comum para todas as estratégias concretas.
- **ConcreteStrategy**: Implementa a interface Strategy com um algoritmo específico.
- **Context**: Mantém uma referência para um objeto Strategy e é configurado com uma estratégia concreta. Ele delega o trabalho para a estratégia atualmente configurada.

#### Exemplo
Imagine um sistema de ordenação de listas que pode usar diferentes algoritmos de ordenação (por exemplo, bubble sort, quick sort).

```java
interface SortingStrategy {
    void sort(int[] numbers);
}

class BubbleSortStrategy implements SortingStrategy {
    public void sort(int[] numbers) {
        System.out.println("Using Bubble Sort");
        // Implementação do Bubble Sort
    }
}

class QuickSortStrategy implements SortingStrategy {
    public void sort(int[] numbers) {
        System.out.println("Using Quick Sort");
        // Implementação do Quick Sort
    }
}

class SortContext {
    private SortingStrategy strategy;

    public void setStrategy(SortingStrategy strategy) {
        this.strategy = strategy;
    }

    public void sort(int[] numbers) {
        strategy.sort(numbers);
    }
}
```

### Mediator (Mediador)
O padrão Mediator define um objeto que encapsula a forma como um conjunto de objetos interage. Ele promove o acoplamento fraco ao evitar que objetos se refiram uns aos outros explicitamente e permite variar suas interações independentemente.

#### Estrutura
- **Mediator**: Define uma interface para comunicar com objetos Colega.
- **ConcreteMediator**: Implementa a interface Mediator e coordena a comunicação entre objetos Colega.
- **Colleague**: Cada objeto Colega se comunica com seu Mediador em vez de outros Colegas diretamente.

#### Exemplo
Imagine um sistema de controle de tráfego aéreo, onde o Mediador coordena a comunicação entre diferentes aeronaves.

```java
interface Mediator {
    void sendMessage(String message, Colleague colleague);
}

class ConcreteMediator implements Mediator {
    private List<Colleague> colleagues = new ArrayList<>();

    public void addColleague(Colleague colleague) {
        colleagues.add(colleague);
    }

    public void sendMessage(String message, Colleague colleague) {
        for (Colleague c : colleagues) {
            if (c != colleague) {
                c.receive(message);
            }
        }
    }
}

abstract class Colleague {
    protected Mediator mediator;

    public Colleague(Mediator mediator) {
        this.mediator = mediator;
    }

    public abstract void receive(String message);
}

class ConcreteColleague1 extends Colleague {
    public ConcreteColleague1(Mediator mediator) {
        super(mediator);
    }

    public void receive(String message) {
        System.out.println("Colleague1 received: " + message);
    }
}

class ConcreteColleague2 extends Colleague {
    public ConcreteColleague2(Mediator mediator) {
        super(mediator);
    }

    public void receive(String message) {
        System.out.println("Colleague2 received: " + message);
    }
}
```

## Referências sobre Design Patterns
- "Design Patterns: Elements of Reusable Object-Oriented Software" por Erich Gamma, Richard Helm, Ralph Johnson e John Vlissides 
- "Head First Design Patterns" por Eric Freeman, Elisabeth Robson, Bert Bates e Kathy Sierra. 
- "Patterns of Enterprise Application Architecture" por Martin Fowler. 
- "Refactoring to Patterns" por Joshua Kerievsky.
