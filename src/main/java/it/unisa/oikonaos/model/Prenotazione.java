package it.unisa.oikonaos.model;

import java.io.Serializable;
import java.sql.Date;

public class Prenotazione implements Serializable {
    private long idPrenotazione;
    private Date data;
    private long idUtente; //
    private long idPostazione; //
    private long idFasciaOraria; // [cite: 321]

    public Prenotazione() {}

    // Segue convenzione camelCase dell'ODD [cite: 113]
    public long getIdPrenotazione() { return idPrenotazione; }
    public void setIdPrenotazione(long idPrenotazione) { this.idPrenotazione = idPrenotazione; }
    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }
    public long getIdUtente() { return idUtente; }
    public void setIdUtente(long idUtente) { this.idUtente = idUtente; }
    public long getIdPostazione() { return idPostazione; }
    public void setIdPostazione(long idPostazione) { this.idPostazione = idPostazione; }
    public long getIdFasciaOraria() { return idFasciaOraria; }
    public void setIdFasciaOraria(long idFasciaOraria) { this.idFasciaOraria = idFasciaOraria; }
}
