package it.unisa.oikonaos.dto;

public class AllegatoDTO {
    private String nomeFile;
    private String path;
    private String tipo;

    public AllegatoDTO() {}

    public AllegatoDTO(String nomeFile, String path, String tipo) {
        this.nomeFile = nomeFile;
        this.path = path;
        this.tipo = tipo;
    }

    public String getNomeFile() { return nomeFile; }
    public void setNomeFile(String nomeFile) { this.nomeFile = nomeFile; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
