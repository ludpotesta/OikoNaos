package it.unisa.oikonaos.model;

import java.sql.Timestamp;

public class AggiornamentoTicket {

    private long idAggiornamento;
    private long idTicket;
    private long idAutore;
    private String messaggio;
    private String stato;
    private Timestamp dataAggiornamento;
    private String nomeUtente;
    private String cognomeUtente;

    public long getIdAggiornamento() {
        return idAggiornamento;
    }

    public void setIdAggiornamento(long idAggiornamento) {
        this.idAggiornamento = idAggiornamento;
    }

    public long getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(long idTicket) {
        this.idTicket = idTicket;
    }

    public long getIdAutore() {
        return idAutore;
    }

    public void setIdAutore(long idAutore) {
        this.idAutore = idAutore;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public Timestamp getDataAggiornamento() {
        return dataAggiornamento;
    }

    public void setDataAggiornamento(Timestamp dataAggiornamento) {
        this.dataAggiornamento = dataAggiornamento;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getCognomeUtente() {
        return cognomeUtente;
    }

    public void setCognomeUtente(String cognomeUtente) {
        this.cognomeUtente = cognomeUtente;
    }
}
