import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class TelaCadastroProduto extends JFrame {
    private JTextField txtCodigoBarra, txtNome, txtTipo, txtPreco, txtQuantidade, txtFabricante, txtDescricao;
    private JButton btnSalvar;
    private ArquivoUtils arquivoUtils;

    public TelaCadastroProduto() {
        super("Cadastro de Produtos");
        setLayout(new GridLayout(8, 2));

        // Inicializa os componentes
        txtCodigoBarra = new JTextField(20);
        txtNome = new JTextField(20);
        txtTipo = new JTextField(20);
        txtPreco = new JTextField(20);
        txtQuantidade = new JTextField(20);
        txtFabricante = new JTextField(20);
        txtDescricao = new JTextField(20);
        btnSalvar = new JButton("Salvar Produto");

        // Adiciona os componentes à tela
        add(new JLabel("Código de Barras:"));
        add(txtCodigoBarra);
        add(new JLabel("Nome:"));
        add(txtNome);
        add(new JLabel("Tipo:"));
        add(txtTipo);
        add(new JLabel("Preço:"));
        add(txtPreco);
        add(new JLabel("Quantidade:"));
        add(txtQuantidade);
        add(new JLabel("Fabricante:"));
        add(txtFabricante);
        add(new JLabel("Descrição:"));
        add(txtDescricao);
        add(btnSalvar);

        // Configura o arquivo e o log path
        arquivoUtils = new ArquivoUtils("produtos.csv", "log.txt");

        // Define o comportamento do botão
        btnSalvar.addActionListener(this::salvarProduto);

        // Configurações finais da janela
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centraliza a janela
        setVisible(true);
    }

    private void salvarProduto(ActionEvent event) {
        try {
            double preco = Double.parseDouble(txtPreco.getText());
            int quantidade = Integer.parseInt(txtQuantidade.getText());

            Produto produto = new Produto(
                    txtCodigoBarra.getText(),
                    txtNome.getText(),
                    txtTipo.getText(),
                    preco,
                    quantidade,
                    txtFabricante.getText(),
                    txtDescricao.getText()
            );

            arquivoUtils.salvarProduto(produto);
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // Pergunta ao usuário se deseja cadastrar outro produto ou sair
            int response = JOptionPane.showConfirmDialog(this, "Deseja cadastrar outro produto?", "Continuar?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
                System.exit(0); // Encerra a aplicação
            } else {
                clearFields(); // Limpa os campos para novo cadastro
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Erro na entrada de dados. Preço deve ser um valor decimal e quantidade um valor inteiro.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o produto: " + ioe.getMessage(), "Erro de IO", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtCodigoBarra.setText("");
        txtNome.setText("");
        txtTipo.setText("");
        txtPreco.setText("");
        txtQuantidade.setText("");
        txtFabricante.setText("");
        txtDescricao.setText("");
    }

    public static void main(String[] args) {
        new TelaCadastroProduto();
    }
}