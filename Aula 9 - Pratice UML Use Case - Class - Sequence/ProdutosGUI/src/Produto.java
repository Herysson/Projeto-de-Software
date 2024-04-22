
public class Produto {
    private String codigoDeBarras;
    private String nome;
    private String tipo;
    private double preco;
    private int quantidade;
    private String fabricante;
    private String descricao;

    public Produto(String codigoDeBarras, String nome, String tipo, double preco, int quantidade, String fabricante, String descricao) {
        this.codigoDeBarras = codigoDeBarras;
        this.nome = nome;
        this.tipo = tipo;
        this.preco = preco;
        this.quantidade = quantidade;
        this.fabricante = fabricante;
        this.descricao = descricao;
    }

    // Getters e Setters
    public String getCodigoDeBarras() { return codigoDeBarras; }

    @Override
    public String toString() {
        return codigoDeBarras + "," + nome + "," + tipo + "," + preco + "," + quantidade + "," + fabricante + "," + descricao;
    }
}
