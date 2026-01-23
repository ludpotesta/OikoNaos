package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.TokenResetPassword;
import util.database;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Base64;

public class TokenResetPasswordDAO {

    public String createToken(long idUtente) throws Exception {
        deleteByUtente(idUtente);

        String token = generateSecureToken();

        LocalDateTime scadenza = LocalDateTime.now().plusMinutes(30);

        String sql = """
            INSERT INTO tokenresetpassword (Token, DataScadenza, ID_Utente)
            VALUES (?, ?, ?)
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, token);
            ps.setTimestamp(2, Timestamp.valueOf(scadenza));
            ps.setLong(3, idUtente);

            ps.executeUpdate();
        }

        return token;
    }

    public TokenResetPassword findByToken(String token) throws Exception {

        String sql = """
            SELECT ID_Token, Token, DataScadenza, ID_Utente
            FROM tokenresetpassword
            WHERE Token = ?
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            }

            TokenResetPassword t = new TokenResetPassword();
            t.setIdToken(rs.getLong("ID_Token"));
            t.setToken(rs.getString("Token"));
            t.setDataScadenza(
                    rs.getTimestamp("DataScadenza").toLocalDateTime()
            );
            t.setIdUtente(rs.getLong("ID_Utente"));

            return t;
        }
    }

    public void deleteByToken(String token) throws Exception {

        String sql = "DELETE FROM tokenresetpassword WHERE Token = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, token);
            ps.executeUpdate();
        }
    }

    public void deleteByUtente(long idUtente) throws Exception {

        String sql = "DELETE FROM tokenresetpassword WHERE ID_Utente = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ps.executeUpdate();
        }
    }

    private String generateSecureToken() {

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public Long getIdUtenteByToken(String token) throws Exception {

        String sql = """
        SELECT ID_Utente
        FROM tokenresetpassword
        WHERE Token = ?
          AND DataScadenza > NOW()
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getLong("ID_Utente");
            }
        }
        return null;
    }

    public void invalidateToken(String token) throws Exception {

        String sql = "DELETE FROM tokenresetpassword WHERE Token = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, token);
            ps.executeUpdate();
        }
    }
}

