

# Aula – Mapeamento Objeto Relacional Spring JPA: Relacionamentos Unidirecionais

---

# O que são relacionamentos unidirecionais?

Relacionamentos unidirecionais são associações entre entidades em que apenas uma das classes conhece a outra.

Exemplo:

```java
public class Aluno {
    private Curso curso;
}
```

Nesse caso, `Aluno` conhece `Curso`, mas `Curso` não possui uma lista de alunos.

---

# Exemplo 1 – @ManyToOne Unidirecional

## Cenário

Muitos alunos pertencem a um curso.

* `Aluno` conhece `Curso`.
* `Curso` não conhece `Aluno`.

## Classe Curso

```java
@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Integer cargaHoraria;

    // getters e setters
}
```

## Classe Aluno

```java
@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    // getters e setters
}
```

## Resultado no banco

Tabela `alunos`:

```text
id | nome | email | curso_id
```

---

# Exemplo 2 – @OneToOne Unidirecional

## Cenário

Um professor possui uma sala.

* `Professor` conhece `Sala`.
* `Sala` não conhece `Professor`.

## Classe Sala

```java
@Entity
@Table(name = "salas")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    private String predio;

    // getters e setters
}
```

## Classe Professor

```java
@Entity
@Table(name = "professores")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String area;

    @OneToOne
    @JoinColumn(name = "sala_id")
    private Sala sala;

    // getters e setters
}
```

## Resultado no banco

Tabela `professores`:

```text
id | nome | area | sala_id
```

---

# Exemplo 3 – @OneToMany Unidirecional

## Cenário

Uma disciplina possui vários materiais.

* `Disciplina` conhece `Material`.
* `Material` não conhece `Disciplina`.

## Classe Material

```java
@Entity
@Table(name = "materiais")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String tipo;

    // getters e setters
}
```

## Classe Disciplina

```java
@Entity
@Table(name = "disciplinas")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Integer semestre;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "disciplina_id")
    private List<Material> materiais;

    // getters e setters
}
```

## Resultado no banco

Tabela `materiais`:

```text
id | titulo | tipo | disciplina_id
```

---

# Exemplo 4 – @ManyToMany Unidirecional

## Cenário

Uma disciplina possui vários professores convidados.

* `Disciplina` conhece `ProfessorConvidado`.
* `ProfessorConvidado` não conhece `Disciplina`.

## Classe ProfessorConvidado

```java
@Entity
@Table(name = "professores_convidados")
public class ProfessorConvidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String instituicao;

    // getters e setters
}
```

## Classe Disciplina

```java
@Entity
@Table(name = "disciplinas")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Integer semestre;

    @ManyToMany
    @JoinTable(
        name = "disciplinas_professores_convidados",
        joinColumns = @JoinColumn(name = "disciplina_id"),
        inverseJoinColumns = @JoinColumn(name = "professor_convidado_id")
    )
    private List<ProfessorConvidado> professoresConvidados;

    // getters e setters
}
```

## Resultado no banco

Tabela `disciplinas_professores_convidados`:

```text
disciplina_id | professor_convidado_id
```

---

# Exemplo 5 – @Embedded e @Embeddable

## Cenário

Um aluno possui um endereço.

Neste caso, `Endereco` não será uma entidade própria. Ele será incorporado dentro da tabela `alunos`.

## Classe Endereco

```java
@Embeddable
public class Endereco {

    private String rua;

    private String numero;

    private String bairro;

    private String cidade;

    private String estado;

    private String cep;

    // getters e setters
}
```

## Classe Aluno

```java
@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    @Embedded
    private Endereco endereco;

    // getters e setters
}
```

## Resultado no banco

Tabela `alunos`:

```text
id | nome | email | rua | numero | bairro | cidade | estado | cep
```

---

# Personalizando colunas com @AttributeOverrides

Caso seja necessário alterar os nomes das colunas do objeto incorporado:

```java
@Embedded
@AttributeOverrides({
    @AttributeOverride(name = "rua", column = @Column(name = "endereco_rua")),
    @AttributeOverride(name = "numero", column = @Column(name = "endereco_numero")),
    @AttributeOverride(name = "bairro", column = @Column(name = "endereco_bairro")),
    @AttributeOverride(name = "cidade", column = @Column(name = "endereco_cidade")),
    @AttributeOverride(name = "estado", column = @Column(name = "endereco_estado")),
    @AttributeOverride(name = "cep", column = @Column(name = "endereco_cep"))
})
private Endereco endereco;
```

Resultado:

```text
id | nome | email | endereco_rua | endereco_numero | endereco_bairro | endereco_cidade | endereco_estado | endereco_cep
```

---

# Exemplo 6 – @EmbeddedId

## Cenário

Uma matrícula possui uma chave composta formada por:

* id do aluno;
* id da disciplina.

Esse tipo de situação é comum quando uma tabela representa uma associação entre duas entidades.

---

# Classe MatriculaId

```java
@Embeddable
public class MatriculaId implements Serializable {

    private Long alunoId;

    private Long disciplinaId;

    public MatriculaId() {
    }

    public MatriculaId(Long alunoId, Long disciplinaId) {
        this.alunoId = alunoId;
        this.disciplinaId = disciplinaId;
    }

    // getters e setters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatriculaId)) return false;

        MatriculaId that = (MatriculaId) o;

        return Objects.equals(alunoId, that.alunoId)
                && Objects.equals(disciplinaId, that.disciplinaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alunoId, disciplinaId);
    }
}
```

---

# Classe Matricula

```java
@Entity
@Table(name = "matriculas")
public class Matricula {

    @EmbeddedId
    private MatriculaId id;

    private LocalDate dataMatricula;

    private String status;

    // getters e setters
}
```

## Resultado no banco

Tabela `matriculas`:

```text
aluno_id | disciplina_id | data_matricula | status
```

---

# Exemplo 7 – @EmbeddedId com relacionamentos

Também é possível usar `@EmbeddedId` junto com `@ManyToOne`.

## Classe Matricula

```java
@Entity
@Table(name = "matriculas")
public class Matricula {

    @EmbeddedId
    private MatriculaId id;

    @ManyToOne
    @MapsId("alunoId")
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne
    @MapsId("disciplinaId")
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    private LocalDate dataMatricula;

    private String status;

    // getters e setters
}
```

## Classe MatriculaId

```java
@Embeddable
public class MatriculaId implements Serializable {

    @Column(name = "aluno_id")
    private Long alunoId;

    @Column(name = "disciplina_id")
    private Long disciplinaId;

    public MatriculaId() {
    }

    public MatriculaId(Long alunoId, Long disciplinaId) {
        this.alunoId = alunoId;
        this.disciplinaId = disciplinaId;
    }

    // getters, setters, equals e hashCode
}
```

---

# Observação importante

O uso de `@EmbeddedId` exige que a classe da chave composta:

* seja anotada com `@Embeddable`;
* implemente `Serializable`;
* possua construtor vazio;
* possua `equals()` e `hashCode()`;
* represente corretamente os campos da chave primária composta.

---

# Prática

Desenvolva um sistema acadêmico com as seguintes entidades:

## Curso

```text
id
nome
cargaHoraria
```

## Aluno

```text
id
nome
email
endereco
curso
```

## Endereco

```text
rua
numero
bairro
cidade
estado
cep
```

## Professor

```text
id
nome
area
sala
```

## Sala

```text
id
numero
predio
```

## Disciplina

```text
id
nome
semestre
materiais
professoresConvidados
```

## Material

```text
id
titulo
tipo
```

## Matricula

```text
aluno
disciplina
dataMatricula
status
```

---

# Relacionamentos exigidos

```text
Aluno N:1 Curso
Professor 1:1 Sala
Disciplina 1:N Material
Disciplina N:N ProfessorConvidado
Aluno possui Endereco incorporado
Matricula possui chave composta com alunoId e disciplinaId
```

---

# Resumo das anotações utilizadas

| Anotação          | Finalidade                                          |
| ----------------- | --------------------------------------------------- |
| `@Entity`         | Define uma classe como entidade JPA                 |
| `@Table`          | Define o nome da tabela                             |
| `@Id`             | Define a chave primária                             |
| `@GeneratedValue` | Gera automaticamente o valor da chave               |
| `@ManyToOne`      | Relacionamento muitos-para-um                       |
| `@OneToOne`       | Relacionamento um-para-um                           |
| `@OneToMany`      | Relacionamento um-para-muitos                       |
| `@ManyToMany`     | Relacionamento muitos-para-muitos                   |
| `@JoinColumn`     | Define a coluna de chave estrangeira                |
| `@JoinTable`      | Define a tabela intermediária                       |
| `@Embedded`       | Incorpora uma classe dentro de uma entidade         |
| `@Embeddable`     | Define uma classe incorporável                      |
| `@EmbeddedId`     | Define uma chave primária composta incorporada      |
| `@MapsId`         | Liga um relacionamento a um campo da chave composta |
