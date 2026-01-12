package it.unisa.oikonaos.model;

import java.time.LocalDateTime;

public class CodiceIdentificativo {

    private String codice;
    private String stato;
    private LocalDateTime dataCreazione;
    private long idComunita;
    private Long idUtenteUtilizzatore;

    public CodiceIdentificativo() {
    }

    public CodiceIdentificativo(String codice, String stato, long idComunita) {
        this.codice = codice;
        this.stato = stato;
        this.idComunita = idComunita;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public long getIdComunita() {
        return idComunita;
    }

    public void setIdComunita(long idComunita) {
        this.idComunita = idComunita;
    }

    public Long getIdUtenteUtilizzatore() {
        return idUtenteUtilizzatore;
    }

    public void setIdUtenteUtilizzatore(Long idUtenteUtilizzatore) {
        this.idUtenteUtilizzatore = idUtenteUtilizzatore;
    }

    public boolean isAttivo() {
        return "ATTIVO".equalsIgnoreCase(stato);
    }

    public boolean isUsato() {
        return "USATO".equalsIgnoreCase(stato);
    }
}
