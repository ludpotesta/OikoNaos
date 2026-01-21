package it.unisa.oikonaos.model;

import java.math.BigDecimal;

public class Risorsa {
    private long idRisorsa;
    private String nome;
    private String descrizione;
    private String regoleUso;
    private BigDecimal penale;

    public Risorsa() {
    }

    public long getIdRisorsa() { return idRisorsa; }
    public void setIdRisorsa(long id) { this.idRisorsa = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getRegoleUso() { return regoleUso; }
    public void setRegoleUso(String regoleUso) { this.regoleUso = regoleUso; }

    public BigDecimal getPenale() { return penale; }
    public void setPenale(BigDecimal penale) { this.penale = penale; }
}

