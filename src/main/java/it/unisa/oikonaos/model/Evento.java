package it.unisa.oikonaos.model;

import java.time.LocalDateTime;

public class Evento {

    private long idEvento;
    private String titolo;
    private String descrizione;
    private String luogo;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private int postiTotali;
    private int postiDisponibili;
    private long idOrganizzatore;

    /* ======================
       COSTRUTTORI
       ====================== */

    public Evento() {
    }

    public Evento(String titolo, String descrizione, String luogo,
                  LocalDateTime dataInizio, LocalDateTime dataFine,
                  int postiTotali, int postiDisponibili, long idOrganizzatore) {

        this.titolo = titolo;
        this.descrizione = descrizione;
        this.luogo = luogo;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.postiTotali = postiTotali;
        this.postiDisponibili = postiDisponibili;
        this.idOrganizzatore = idOrganizzatore;
    }

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

    public int getPostiTotali() {
        return postiTotali;
    }

    public void setPostiTotali(int postiTotali) {
        this.postiTotali = postiTotali;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }

    public long getIdOrganizzatore() {
        return idOrganizzatore;
    }

    public void setIdOrganizzatore(long idOrganizzatore) {
        this.idOrganizzatore = idOrganizzatore;
    }
}
