package it.unisa.oikonaos.model;

import java.time.LocalDateTime;

public class TokenResetPassword {

    private long idToken;
    private String token;
    private LocalDateTime dataScadenza;
    private long idUtente;

    public TokenResetPassword() {}

    public long getIdToken() {
        return idToken;
    }

    public void setIdToken(long idToken) {
        this.idToken = idToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDateTime dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

    public boolean isScaduto() {
        return dataScadenza.isBefore(LocalDateTime.now());
    }
}

