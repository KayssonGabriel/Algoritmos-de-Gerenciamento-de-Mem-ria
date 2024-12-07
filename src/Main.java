import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Processo> processos = Arrays.asList(
                new Processo("P1", 5), new Processo("P2", 4), new Processo("P3", 2),
                new Processo("P4", 5), new Processo("P5", 8), new Processo("P6", 3),
                new Processo("P7", 5), new Processo("P8", 8), new Processo("P9", 2),
                new Processo("P10", 6)
        );

        GerenciadorMemoria gerenciamento = new GerenciadorMemoria(32, processos);

        gerenciamento.simular("First Fit", gerenciamento::alocarFirstFit);
        gerenciamento.simular("Next Fit", gerenciamento::alocarNextFit);
        gerenciamento.simular("Best Fit", gerenciamento::alocarBestFit);
        gerenciamento.simular("Quick Fit", gerenciamento::alocarQuickFit);
        gerenciamento.simular("Worst Fit", gerenciamento::alocarWorstFit);
    }
}
