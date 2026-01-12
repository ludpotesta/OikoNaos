package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Utente;
import it.unisa.oikonaos.model.Credenziali;
import util.database;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class CredenzialiDAO {

    public boolean usernameEsistente(String username) throws Exception {

        String sql = "SELECT 1 FROM Credenziali WHERE Username = ?";

        try (Connection con = database.getConnection();
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

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getLong("ID_Utente");
            }
            return null;
        }
    }

    public String getPasswordHashByUtente(long idUtente) throws Exception {
        String sql = """
        SELECT PasswordHash
        FROM Credenziali
        WHERE ID_Utente = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return rs.getString("PasswordHash");
        }
    }
    public String getUsernameByUtente(long idUtente) throws Exception {
        String sql = """
        SELECT Username
        FROM Credenziali
        WHERE ID_Utente = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return rs.getString("Username");
        }
    }

    public boolean updateUsername(long idUtente, String nuovoUsername) throws Exception {

        String sql = """
        UPDATE Credenziali
        SET Username = ?
        WHERE ID_Utente = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuovoUsername);
            ps.setLong(2, idUtente);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(long idUtente, String nuovaPasswordHash) throws Exception {

        String sql = """
        UPDATE Credenziali
        SET PasswordHash = ?
        WHERE ID_Utente = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuovaPasswordHash);
            ps.setLong(2, idUtente);

            return ps.executeUpdate() == 1;
        }
    }

    public Long getIdUtenteByUsername(String username) throws Exception {

        String sql = """
        SELECT ID_Utente
        FROM credenziali
        WHERE Username = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getLong("ID_Utente");
            }

            return null;
        }
    }
}
