package it.unisa.oikonaos.model;

import java.io.Serializable;

public class Utente implements Serializable {
    private long idUtente;
    private String nome;
    private String cognome;
    private String email;
    private String ruolo;
    private long idComunita;

    // Costruttore vuoto (OBBLIGATORIO)
    public Utente() {
    }

    public long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public long getIdComunita() {
        return idComunita;
    }

    public void setIdComunita(long idComunita) {
        this.idComunita = idComunita;
    }
}
