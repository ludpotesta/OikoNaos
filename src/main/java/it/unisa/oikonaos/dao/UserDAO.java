package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Utente;
import util.database;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDAO {
    public long registerUser(
            Connection con,
            String nome,
            String cognome,
            String email,
            String telefono,
            String username,
            String password
    ) throws Exception {

        // 1. Inserimento utente
        long idUtente = insertUtente(con, nome, cognome, email, telefono);

        // 2. Inserimento credenziali
        insertCredenziali(con, username, password, idUtente);

        return idUtente;
    }

    private long insertUtente(
            Connection con,
            String nome,
            String cognome,
            String email,
            String telefono
    ) throws Exception {

        String sql = """
            INSERT INTO Utente (Nome, Cognome, Email, Telefono, Ruolo)
            VALUES (?, ?, ?, ?, 'COINQUILINO', ?)
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.setString(4, telefono);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Errore creazione utente");
                }
                return rs.getLong(1);
            }
        }
    }

    private void insertCredenziali(
            Connection con,
            String username,
            String password,
            long idUtente
    ) throws Exception {

        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));

        String sql = """
            INSERT INTO Credenziali (Username, PasswordHash, ID_Utente)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setLong(3, idUtente);
            ps.executeUpdate();
        }
    }

    public Utente login(String username, String password) throws Exception {

        String sql = """
            SELECT u.ID_Utente, u.Nome, u.Cognome, u.Email,
                   u.Telefono, u.Ruolo, c.PasswordHash
            FROM Utente u
            JOIN Credenziali c ON u.ID_Utente = c.ID_Utente
            WHERE c.Username = ?
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new IllegalArgumentException("Username o password errati");
            }

            String hash = rs.getString("PasswordHash");
            if (!BCrypt.checkpw(password, hash)) {
                throw new IllegalArgumentException("Username o password errati");
            }

            Utente u = new Utente();
            u.setIdUtente(rs.getLong("ID_Utente"));
            u.setNome(rs.getString("Nome"));
            u.setCognome(rs.getString("Cognome"));
            u.setEmail(rs.getString("Email"));
            u.setTelefono(rs.getString("Telefono"));
            u.setRuolo(rs.getString("Ruolo"));

            return u;
        }
    }

    public void updateProfilo(Utente u) throws Exception {

        String sql = """
            UPDATE Utente
            SET Nome = ?, Cognome = ?, Email = ?, Telefono = ?
            WHERE ID_Utente = ?
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getCognome());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getTelefono());
            ps.setLong(5, u.getIdUtente());

            ps.executeUpdate();
        }
    }

    public Utente getUtenteById(long idUtente) throws Exception {

        String sql = """
        SELECT ID_Utente, Nome, Cognome, Email, Telefono, Ruolo
        FROM utente
        WHERE ID_Utente = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Utente u = new Utente();
                u.setIdUtente(rs.getLong("ID_Utente"));
                u.setNome(rs.getString("Nome"));
                u.setCognome(rs.getString("Cognome"));
                u.setEmail(rs.getString("Email"));
                u.setTelefono(rs.getString("Telefono"));
                u.setRuolo(rs.getString("Ruolo"));
                return u;
            }
        }
        return null;
    }

    public long getIdComunitaByUtente(long idUtente) throws Exception {

        String sql = """
        SELECT ID_Comunita
        FROM Utente
        WHERE ID_Utente = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getLong("ID_Comunita");
            } else {
                throw new IllegalStateException("Utente senza comunità");
            }
        }
    }

}
