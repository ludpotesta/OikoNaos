package it.unisa.oikonaos.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class RichiestaRisorsa {
    private long idRichiesta;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String stato;
    private boolean accettazioneRegole;
    private long idUtente;
    private long idRisorsa;

    // campi utili per le JSP
    private String nomeRisorsa;
    private String nomeUtente;

    public RichiestaRisorsa() {
    }

    public long getIdRichiesta() { return idRichiesta; }
    public void setIdRichiesta(long id) { this.idRichiesta = id; }

    public LocalDateTime getDataInizio() { return dataInizio; }
    public void setDataInizio(Timestamp dataInizio) { this.dataInizio = dataInizio.toLocalDateTime(); }

    public LocalDateTime getDataFine() { return dataFine; }
    public void setDataFine(Timestamp dataFine) { this.dataFine = dataFine.toLocalDateTime(); }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public boolean getAccettazioneRegole() { return accettazioneRegole; }
    public void setAccettazioneRegole(boolean accetta) { this.accettazioneRegole = accetta; }

    public long getIdUtente() { return idUtente; }
    public void setIdUtente(long idUtente) { this.idUtente = idUtente; }

    public long getIdRisorsa() { return idRisorsa; }
    public void setIdRisorsa(long idRisorsa) { this.idRisorsa = this.idRisorsa; }

    public String getNomeRisorsa() { return nomeRisorsa; }
    public void setNomeRisorsa(String nomeRisorsa) { this.nomeRisorsa = this.nomeRisorsa; }

    public String getNomeUtente() { return nomeUtente; }
    public void setNomeUtente(String nomeUtente) { this.nomeUtente = this.nomeUtente; }


}
