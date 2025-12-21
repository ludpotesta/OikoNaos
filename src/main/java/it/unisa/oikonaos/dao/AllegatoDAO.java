package it.unisa.oikonaos.dao;

import util.database;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AllegatoDAO {

    public void salva(String nomeFile,
                      String pathFile,
                      String tipoFile,
                      long idTicket) throws Exception {

        String sql = "INSERT INTO allegato (NomeFile, PathFile, TipoFile, ID_Ticket) VALUES (?, ?, ?, ?)";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nomeFile);
            ps.setString(2, pathFile);
            ps.setString(3, tipoFile);
            ps.setLong(4, idTicket);

            ps.executeUpdate();
        }
    }
}

