package it.unisa.oikonaos.dao;

import util.database;
import java.sql.*;

public class CredenzialiDAO {

    public boolean usernameEsistente(String username) throws Exception {
        try (Connection con = database.getConnection()) {
            return usernameEsistente(con, username);
        }
    }
    public boolean usernameEsistente(Connection con, String username) throws Exception {

        String sql = "SELECT 1 FROM credenziali WHERE Username = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeQuery().next();
        }
    }

    public Long getIdUtenteByEmail(String email) throws Exception {
        try (Connection con = database.getConnection()) {
            return getIdUtenteByEmail(con, email);
        }
    }
    public Long getIdUtenteByEmail(Connection con, String email) throws Exception {

        String sql = """
            SELECT ID_Utente
            FROM utente
            WHERE Email = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getLong("ID_Utente") : null;
        }
    }

    public Long getIdUtenteByUsername(String username) throws Exception {
        try (Connection con = database.getConnection()) {
            return getIdUtenteByUsername(con, username);
        }
    }
    public Long getIdUtenteByUsername(Connection con, String username) throws Exception {

        String sql = """
            SELECT ID_Utente
            FROM credenziali
            WHERE Username = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getLong("ID_Utente") : null;
        }
    }

    public String getPasswordHashByUtente(long idUtente) throws Exception {
        try (Connection con = database.getConnection()) {
            return getPasswordHashByUtente(con, idUtente);
        }
    }
    public String getPasswordHashByUtente(Connection con, long idUtente) throws Exception {

        String sql = """
            SELECT PasswordHash
            FROM credenziali
            WHERE ID_Utente = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("PasswordHash") : null;
        }
    }

    public boolean updateUsername(long idUtente, String nuovoUsername) throws Exception {
        try (Connection con = database.getConnection()) {
            return updateUsername(con, idUtente, nuovoUsername);
        }
    }
    public boolean updateUsername(Connection con, long idUtente, String nuovoUsername) throws Exception {

        String sql = """
            UPDATE credenziali
            SET Username = ?
            WHERE ID_Utente = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuovoUsername);
            ps.setLong(2, idUtente);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(long idUtente, String nuovaPasswordHash) throws Exception {
        try (Connection con = database.getConnection()) {
            return updatePassword(con, idUtente, nuovaPasswordHash);
        }
    }
    public boolean updatePassword(Connection con, long idUtente, String nuovaPasswordHash) throws Exception {

        String sql = """
            UPDATE credenziali
            SET PasswordHash = ?
            WHERE ID_Utente = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuovaPasswordHash);
            ps.setLong(2, idUtente);
            return ps.executeUpdate() == 1;
        }
    }
}
