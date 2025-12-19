package it.unisa.oikonaos.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Ticket implements Serializable {

    private long idTicket;
    private String titolo;
    private String descrizione;
    private String categoria;
    private String priorita;
    private String stato;
    private Timestamp dataApertura;
    private Timestamp dataChiusura;
    private long idAutore;
    private long idSupervisore;

    public Ticket() {
    }

    public long getIdTicket() {
        return idTicket;
    }
    public void setIdTicket(long idTicket) {
        this.idTicket = idTicket;
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

    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPriorita() {
        return priorita;
    }
    public void setPriorita(String priorita) {
        this.priorita = priorita;
    }

    public String getStato() {
        return stato;
    }
    public void setStato(String stato) {
        this.stato = stato;
    }

    public Timestamp getDataApertura() {
        return dataApertura;
    }
    public void setDataApertura(Timestamp dataApertura) {
        this.dataApertura = dataApertura;
    }

    public Timestamp getDataChiusura() {
        return dataChiusura;
    }
    public void setDataChiusura(Timestamp dataChiusura) {
        this.dataChiusura = dataChiusura;
    }

    public long getIdAutore() {
        return idAutore;
    }
    public void setIdAutore(long idAutore) {
        this.idAutore = idAutore;
    }

    public long getIdSupervisor() {
        return idSupervisore;
    }
    public void setIdSupervisor(long idSupervisore) {
        this.idSupervisore = idSupervisore;
    }
}

