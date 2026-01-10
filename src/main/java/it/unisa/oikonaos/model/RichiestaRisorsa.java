package it.unisa.oikonaos.model;

import java.sql.Timestamp;

public class RichiestaRisorsa {
    private long id;
    private Timestamp dataRichiesta;
    private String stato;
    private String note;
    private long idRisorsa;
    private long idUtente;
    private Long idSupervisore;

    public RichiestaRisorsa() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Timestamp getDataRichiesta() { return dataRichiesta; }
    public void setDataRichiesta(Timestamp dataRichiesta) { this.dataRichiesta = dataRichiesta; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public long getIdRisorsa() { return idRisorsa; }
    public void setIdRisorsa(long idRisorsa) { this.idRisorsa = this.idRisorsa; }

    public long getIdUtente() { return idUtente; }
    public void setIdUtente(long idUtente) { this.idUtente = idUtente; }

    public Long getIdSupervisore() { return idSupervisore; }
    public void setIdSupervisore(Long idSupervisore) { this.idSupervisore = idSupervisore; }
}
