package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.dto.AllegatoDTO;
import util.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AllegatoDAO {

    public void salva(String nomeFile, String pathFile, String tipoFile, long idTicket) throws Exception {
        try (Connection con = database.getConnection()) {
            salva(con, nomeFile, pathFile, tipoFile, idTicket);
        }
    }
    public void salva(Connection con, String nomeFile, String pathFile, String tipoFile, long idTicket) throws Exception {

        String sql = "INSERT INTO allegato (NomeFile, PathFile, TipoFile, ID_Ticket) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nomeFile);
            ps.setString(2, pathFile);
            ps.setString(3, tipoFile);
            ps.setLong(4, idTicket);

            ps.executeUpdate();
        }
    }

    public List<AllegatoDTO> doRetrieveByTicket(long idTicket) {
        try (Connection con = database.getConnection()) {
            return doRetrieveByTicket(con, idTicket);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Ritorna lista vuota in caso di errore
        }
    }
    public List<AllegatoDTO> doRetrieveByTicket(Connection con, long idTicket) throws Exception {

        List<AllegatoDTO> allegati = new ArrayList<>();

        String sql = """
        SELECT NomeFile, PathFile, TipoFile
        FROM allegato
        WHERE ID_Ticket = ?
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTicket);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AllegatoDTO a = new AllegatoDTO(
                        rs.getString("NomeFile"),
                        rs.getString("PathFile"),
                        rs.getString("TipoFile")
                );
                allegati.add(a);
            }

        }

        return allegati;
    }
}

