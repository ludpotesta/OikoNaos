package it.unisa.oikonaos.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TassaTrimestrale {

    private long idTassa;
    private String trimestreRiferimento;
    private BigDecimal importoDovuto;
    private LocalDate scadenza;
    private String stato; // PAGATA / NON_PAGATA
    private Long idPagamento;
    private boolean hasRicevuta;

    public boolean hasRicevuta() {
        return hasRicevuta;
    }

    public void setHasRicevuta(boolean hasRicevuta) {
        this.hasRicevuta = hasRicevuta;
    }

    public Long getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(Long idPagamento) {
        this.idPagamento = idPagamento;
    }

    public long getIdTassa() {
        return idTassa;
    }

    public void setIdTassa(long idTassa) {
        this.idTassa = idTassa;
    }

    public String getTrimestreRiferimento() {
        return trimestreRiferimento;
    }

    public void setTrimestreRiferimento(String trimestreRiferimento) {
        this.trimestreRiferimento = trimestreRiferimento;
    }

    public BigDecimal getImportoDovuto() {
        return importoDovuto;
    }

    public void setImportoDovuto(BigDecimal importoDovuto) {
        this.importoDovuto = importoDovuto;
    }

    public LocalDate getScadenza() {
        return scadenza;
    }

    public void setScadenza(LocalDate scadenza) {
        this.scadenza = scadenza;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public boolean isPagata() {
        return "PAGATA".equalsIgnoreCase(stato);
    }
}
