package it.unisa.oikonaos.dao;

import util.Database;
import java.sql.*;

public class CredenzialiDAO {

    public boolean usernameEsistente(String username) throws Exception {

        String sql = "SELECT 1 FROM credenziali WHERE Username = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeQuery().next();
        }
    }

    public Long getIdUtenteByEmail(String email) throws Exception {

        String sql = """
            SELECT ID_Utente
            FROM utente
            WHERE Email = ?
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getLong("ID_Utente") : null;
        }
    }

    public Long getIdUtenteByUsername(String username) throws Exception {

        String sql = """
            SELECT ID_Utente
            FROM credenziali
            WHERE Username = ?
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getLong("ID_Utente") : null;
        }
    }

    public String getPasswordHashByUtente(long idUtente) throws Exception {

        String sql = """
            SELECT PasswordHash
            FROM credenziali
            WHERE ID_Utente = ?
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("PasswordHash") : null;
        }
    }

    public boolean updateUsername(long idUtente, String nuovoUsername) throws Exception {

        String sql = """
            UPDATE credenziali
            SET Username = ?
            WHERE ID_Utente = ?
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuovoUsername);
            ps.setLong(2, idUtente);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(long idUtente, String nuovaPasswordHash) throws Exception {

        String sql = """
            UPDATE credenziali
            SET PasswordHash = ?
            WHERE ID_Utente = ?
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuovaPasswordHash);
            ps.setLong(2, idUtente);
            return ps.executeUpdate() == 1;
        }
    }
}
