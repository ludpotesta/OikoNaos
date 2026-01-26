package it.unisa.oikonaos.model;

import java.io.Serializable;

public class Utente implements Serializable {
    private long idUtente;
    private String nome;
    private String cognome;
    private String email;
    private String telefono;
    private String ruolo;
    private String password;

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

    public String getTelefono() {  return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " (" + ruolo + ")";
    }
}
