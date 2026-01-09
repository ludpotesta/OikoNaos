package it.unisa.oikonaos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pagamento {

    public static final String METODO_ONLINE = "ONLINE";

    private long idPagamento;
    private long idUtente;
    private long idTassa;

    private BigDecimal importoPagato;
    private LocalDateTime dataPagamento;
    private String metodoPagamento;

    // Campi DERIVATI (non DB)
    private String periodo; // es. "Q1 2025"

    public Pagamento() {
    }

    public long getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(long idPagamento) {
        this.idPagamento = idPagamento;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

    public long getIdTassa() {
        return idTassa;
    }

    public void setIdTassa(long idTassa) {
        this.idTassa = idTassa;
    }

    public BigDecimal getImportoPagato() {
        return importoPagato;
    }

    public void setImportoPagato(BigDecimal importoPagato) {
        this.importoPagato = importoPagato;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    /* CAMPI DERIVATI*/
    public boolean isPagato() {
        return dataPagamento != null;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
}
