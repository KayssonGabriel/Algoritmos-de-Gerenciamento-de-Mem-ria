public class Processo {
    private final String id;
    private final int tamanho;
    private boolean alocado;
    private int inicio;

    public Processo(String id, int tamanho) {
        this.id = id;
        this.tamanho = tamanho;
        this.alocado = false;
        this.inicio = -1;
    }

    public String getId() {
        return id;
    }

    public int getTamanho() {
        return tamanho;
    }

    public boolean isAlocado() {
        return alocado;
    }

    public int getInicio() {
        return inicio;
    }

    public void alocar(int inicio) {
        this.alocado = true;
        this.inicio = inicio;
    }

    public void desalocar() {
        this.alocado = false;
        this.inicio = -1;
    }
}
