package it.unisa.oikonaos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ricevuta {

    private long idRicevuta;
    private long idPagamento;
    private BigDecimal importo;
    private LocalDateTime dataEmissione;
    private String codiceTransazione;

    public Ricevuta() {
    }

    public long getIdRicevuta() {
        return idRicevuta;
    }

    public void setIdRicevuta(long idRicevuta) {
        this.idRicevuta = idRicevuta;
    }

    public long getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(long idPagamento) {
        this.idPagamento = idPagamento;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public LocalDateTime getDataEmissione() {
        return dataEmissione;
    }

    public String getCodiceTransazione() {
        return codiceTransazione;
    }

    public void setCodiceTransazione(String codiceTransazione) {
        this.codiceTransazione = codiceTransazione;
    }

    public void setDataEmissione(LocalDateTime dataEmissione) {
        this.dataEmissione = dataEmissione;
    }
}

