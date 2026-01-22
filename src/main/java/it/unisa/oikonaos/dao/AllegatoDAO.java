package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.dto.AllegatoDTO;
import util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AllegatoDAO {

    public void salva(String nomeFile,
                      String pathFile,
                      String tipoFile,
                      long idTicket) throws Exception {

        String sql = "INSERT INTO allegato (NomeFile, PathFile, TipoFile, ID_Ticket) VALUES (?, ?, ?, ?)";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nomeFile);
            ps.setString(2, pathFile);
            ps.setString(3, tipoFile);
            ps.setLong(4, idTicket);

            ps.executeUpdate();
        }
    }

    public List<AllegatoDTO> doRetrieveByTicket(long idTicket) {

        List<AllegatoDTO> allegati = new ArrayList<>();

        String sql = """
        SELECT NomeFile, PathFile, TipoFile
        FROM allegato
        WHERE ID_Ticket = ?
    """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return allegati;
    }
}

