import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArquivoUtils {
    private String filePath;
    private String logPath;

    public ArquivoUtils(String filePath, String logPath) {
        this.filePath = filePath;
        this.logPath = logPath;
    }

    public void salvarProduto(Produto produto) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(produto.toString());
            registrarLog("Produto adicionado: " + produto.getCodigoDeBarras());
        }
    }

    private void registrarLog(String message) throws IOException {
        try (FileWriter logWriter = new FileWriter(logPath, true);
             PrintWriter logPrint = new PrintWriter(logWriter)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            logPrint.println(dtf.format(now) + " - " + message);
        }
    }
}