package it.unisa.oikonaos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventoBachecaDTO {

    private long idEvento;
    private String titolo;
    private String descrizione;
    private String luogo;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private int postiDisponibili;
    private boolean iscritto;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }

    public boolean isIscritto() {
        return iscritto;
    }

    public void setIscritto(boolean iscritto) {
        this.iscritto = iscritto;
    }

    public boolean isIscrivibile() {
        return !iscritto
                && postiDisponibili > 0
                && (dataFine == null || dataFine.isAfter(LocalDateTime.now()));
    }

    public boolean isDisiscrivibile() {
        return iscritto
                && (dataFine == null || dataFine.isAfter(LocalDateTime.now()));
    }

    public String getDataInizioFormatted() {
        return dataInizio != null
                ? dataInizio.format(FORMATTER)
                : "";
    }

    public String getDataFineFormatted() {
        return dataFine != null
                ? dataFine.format(FORMATTER)
                : "—";
    }
}
