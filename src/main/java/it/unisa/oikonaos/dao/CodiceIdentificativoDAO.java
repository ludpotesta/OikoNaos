package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.CodiceIdentificativo;
import util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CodiceIdentificativoDAO {

    public CodiceIdentificativo getCodiceValidoForUpdate(
            Connection con, String codice) throws SQLException {

        String sql = """
            SELECT Codice, Stato
            FROM codiceidentificativo
            WHERE Codice = ?
              AND Stato = 'ATTIVO'
            FOR UPDATE
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codice);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CodiceIdentificativo c = new CodiceIdentificativo();
                    c.setCodice(rs.getString("Codice"));
                    c.setStato(rs.getString("Stato"));
                    return c;
                }
            }
        }
        return null;
    }

    public void marcaComeUsato(
            Connection con, String codice, long idUtente) throws SQLException {

        String sql = """
            UPDATE codiceidentificativo
            SET Stato = 'USATO',
                ID_Utente_Utilizzatore = ?
            WHERE Codice = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            ps.setString(2, codice);
            ps.executeUpdate();
        }
    }

    public boolean codiceEsisteEdAttivo(String codice) throws Exception {

        String sql = """
            SELECT 1
            FROM codiceidentificativo
            WHERE Codice = ?
              AND Stato = 'ATTIVO'
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codice);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
