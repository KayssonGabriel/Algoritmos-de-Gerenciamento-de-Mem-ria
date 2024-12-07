import java.util.*;
import java.util.function.Function;

public class GerenciadorMemoria {
    private final int[] memoria;
    private final int tamanhoMemoria;
    private final List<Processo> processos;
    private final Random random = new Random();
    private int ponteiroNextFit = 0;
    private final Map<Integer, List<Integer>> quickFitLists = new HashMap<>();

    public GerenciadorMemoria(int tamanhoMemoria, List<Processo> processos) {
        this.tamanhoMemoria = tamanhoMemoria;
        this.memoria = new int[tamanhoMemoria];
        this.processos = processos;
        inicializarQuickFit();
    }

    public void simular(String nomeAlgoritmo, Function<Processo, Boolean> algoritmo) {
        Arrays.fill(memoria, 0);
        ponteiroNextFit = 0;
        inicializarQuickFit();
        System.out.println("\n\n====================================================== " + nomeAlgoritmo + " ======================================================");
        int menorTamanhoProcesso = processos.stream().mapToInt(Processo::getTamanho).min().orElse(1);

        for (int i = 0; i < 30; i++) {
            Processo processo = processos.get(random.nextInt(processos.size()));
            if (processo.isAlocado()) {
                desalocar(processo);
            } else {
                if (!algoritmo.apply(processo)) {
                    System.out.println("Erro: Não foi possível alocar o " + processo.getId());
                }
            }
            exibirMemoria();
            System.out.println("Fragmentação externa: " + calcularFragmentacaoExterna(menorTamanhoProcesso));
        }
    }

    public boolean alocarFirstFit(Processo processo) {
        for (int i = 0; i <= tamanhoMemoria - processo.getTamanho(); i++) {
            if (isLivre(i, processo.getTamanho())) {
                alocar(processo, i);
                return true;
            }
        }
        return false;
    }

    public boolean alocarNextFit(Processo processo) {
        int start = ponteiroNextFit;
        do {
            if (isLivre(ponteiroNextFit, processo.getTamanho())) {
                alocar(processo, ponteiroNextFit);
                return true;
            }
            ponteiroNextFit = (ponteiroNextFit + 1) % tamanhoMemoria;
        } while (ponteiroNextFit != start);
        return false;
    }

    public boolean alocarBestFit(Processo processo) {
        int melhorIndice = -1;
        int menorTamanho = Integer.MAX_VALUE;

        for (int i = 0; i <= tamanhoMemoria - processo.getTamanho(); i++) {
            if (isLivre(i, processo.getTamanho())) {
                int tamanhoLivre = calcularBlocoLivre(i);
                if (tamanhoLivre < menorTamanho) {
                    melhorIndice = i;
                    menorTamanho = tamanhoLivre;
                }
            }
        }

        if (melhorIndice != -1) {
            alocar(processo, melhorIndice);
            return true;
        }
        return false;
    }

    public boolean alocarWorstFit(Processo processo) {
        int piorIndice = -1;
        int maiorTamanho = -1;

        for (int i = 0; i <= tamanhoMemoria - processo.getTamanho(); i++) {
            if (isLivre(i, processo.getTamanho())) {
                int tamanhoLivre = calcularBlocoLivre(i);
                if (tamanhoLivre > maiorTamanho) {
                    piorIndice = i;
                    maiorTamanho = tamanhoLivre;
                }
            }
        }

        if (piorIndice != -1) {
            alocar(processo, piorIndice);
            return true;
        }
        return false;
    }

    public boolean alocarQuickFit(Processo processo) {
        List<Integer> listaTamanho = quickFitLists.get(processo.getTamanho());
        if (listaTamanho != null && !listaTamanho.isEmpty()) {
            int indice = listaTamanho.remove(0);
            alocar(processo, indice);
            return true;
        }
        return alocarFirstFit(processo);
    }

    private void desalocar(Processo processo) {
        for (int i = processo.getInicio(); i < processo.getInicio() + processo.getTamanho(); i++) {
            memoria[i] = 0;
        }
        atualizarQuickFit(processo.getInicio(), processo.getTamanho());
        System.out.println("Processo " + processo.getId() + " desalocado.");
        processo.desalocar();
    }

    private boolean isLivre(int inicio, int tamanho) {
        if (inicio + tamanho > tamanhoMemoria) {
            return false; // Evita acessar fora dos limites da memória
        }
        for (int i = inicio; i < inicio + tamanho; i++) {
            if (memoria[i] == 1) {
                return false;
            }
        }
        return true;
    }

    private int calcularBlocoLivre(int inicio) {
        int tamanho = 0;
        for (int i = inicio; i < tamanhoMemoria && memoria[i] == 0; i++) {
            tamanho++;
        }
        return tamanho;
    }

    private int calcularFragmentacaoExterna(int menorTamanhoProcesso) {
        int fragmentacao = 0;
        int tamanhoAtual = 0;

        for (int i = 0; i < tamanhoMemoria; i++) {
            if (memoria[i] == 0) {
                tamanhoAtual++;
            } else {
                if (tamanhoAtual > 0 && tamanhoAtual < menorTamanhoProcesso) {
                    fragmentacao += tamanhoAtual;
                }
                tamanhoAtual = 0;
            }
        }

        if (tamanhoAtual > 0 && tamanhoAtual < menorTamanhoProcesso) {
            fragmentacao += tamanhoAtual;
        }

        return fragmentacao;
    }

    private void alocar(Processo processo, int inicio) {
        for (int i = inicio; i < inicio + processo.getTamanho(); i++) {
            memoria[i] = 1;
        }
        processo.alocar(inicio);
        System.out.println("Processo " + processo.getId() + " alocado em " + inicio);
    }

    private void exibirMemoria() {
        System.out.println(Arrays.toString(memoria));
    }

    private void inicializarQuickFit() {
        quickFitLists.clear();
        for (int i = 1; i <= tamanhoMemoria; i++) {
            quickFitLists.put(i, new ArrayList<>());
        }
    }

    private void atualizarQuickFit(int inicio, int tamanho) {
        quickFitLists.get(tamanho).add(inicio);
    }
}
