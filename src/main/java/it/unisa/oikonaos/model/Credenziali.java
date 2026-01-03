package it.unisa.oikonaos.model;

public class Credenziali {
    private long idCredenziali;
    private String username;
    private String passwordHash;
    private long idUtente;

    public Credenziali() {
    }

    public long getIdCredenziali() { return idCredenziali; }
    public void setIdCredenziali(long idCredenziali) { this.idCredenziali = idCredenziali; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public long getIdUtente() { return idUtente; }
}
