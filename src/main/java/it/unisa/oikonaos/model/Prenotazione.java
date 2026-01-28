package it.unisa.oikonaos.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class Prenotazione implements Serializable {
    private long idPrenotazione;
    private Date data;
    private String stato;
    private long idUtente; //
    private long idPostazione; //
    private long idFasciaOraria; // [cite: 321]
    private Time orarioInizio;
    private Time orarioFine;
    private String nomeAmbiente;
    private String numeroPostazione;

    public Prenotazione() {}


    public long getIdPrenotazione() { return idPrenotazione; }
    public void setIdPrenotazione(long idPrenotazione) { this.idPrenotazione = idPrenotazione; }
    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public long getIdUtente() { return idUtente; }
    public void setIdUtente(long idUtente) { this.idUtente = idUtente; }
    public long getIdPostazione() { return idPostazione; }
    public void setIdPostazione(long idPostazione) { this.idPostazione = idPostazione; }
    public long getIdFasciaOraria() { return idFasciaOraria; }
    public void setIdFasciaOraria(long idFasciaOraria) { this.idFasciaOraria = idFasciaOraria; }
    public Time getOrarioInizio() { return orarioInizio; }
    public void setOrarioInizio(Time orarioInizio) { this.orarioInizio = orarioInizio; }
    public Time getOrarioFine() { return orarioFine; }
    public void setOrarioFine(Time orarioFine) { this.orarioFine = orarioFine; }
    public String getNomeAmbiente() { return nomeAmbiente; }
    public void setNomeAmbiente(String nomeAmbiente) { this.nomeAmbiente = nomeAmbiente; }
    public String getNumeroPostazione() { return numeroPostazione; }
    public void setNumeroPostazione(String numeroPostazione) { this.numeroPostazione = numeroPostazione; }

}
