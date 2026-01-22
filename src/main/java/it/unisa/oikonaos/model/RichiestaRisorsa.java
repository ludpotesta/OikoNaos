package it.unisa.oikonaos.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public void setIdRisorsa(long idRisorsa) { this.idRisorsa = idRisorsa; }

    public String getNomeRisorsa() { return nomeRisorsa; }
    public void setNomeRisorsa(String nomeRisorsa) { this.nomeRisorsa = nomeRisorsa; }

    public String getNomeUtente() { return nomeUtente; }
    public void setNomeUtente(String nomeUtente) { this.nomeUtente = nomeUtente; }

    public String getStatoTemporale() {
        LocalDate oggi = LocalDate.now();
        LocalDate data = dataInizio.toLocalDate();

        return data.isBefore(oggi) ? "PASSATA" : "FUTURA";
    }

    public boolean isScaduta() {
        return "PASSATA".equals(getStatoTemporale());
    }

    public String getDataInizioFormatted() {
        return dataInizio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDataFineFormatted() {
        return dataFine.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

}
