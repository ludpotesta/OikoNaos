package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Risorsa;
import it.unisa.oikonaos.model.Utente;
import util.database;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import java.util.ArrayList;
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
                INSERT INTO utente (Nome, Cognome, Email, Telefono, Ruolo)
                VALUES (?, ?, ?, ?, 'COINQUILINO')
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
            INSERT INTO credenziali (Username, PasswordHash, ID_Utente)
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
            FROM utente u
            JOIN credenziali c ON u.ID_Utente = c.ID_Utente
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
            UPDATE utente
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

    public List<Utente> doRetrieveAllCoinquilini() throws Exception {
        List<Utente> lista = new ArrayList<>();

        String sql = """
            SELECT ID_Utente, Nome, Cognome, Email, Telefono, Ruolo
            FROM utente
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Utente u = new Utente();
                u.setIdUtente(rs.getLong("ID_Utente"));
                u.setNome(rs.getString("Nome"));
                u.setCognome(rs.getString("Cognome"));
                u.setEmail(rs.getString("Email"));
                u.setTelefono(rs.getString("Telefono"));
                u.setRuolo(rs.getString("Ruolo"));
                lista.add(u);
            }
        }
        return lista;
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

    public List<Utente> doRetrieveCoinquiliniEscluso(long idDaEscludere)
            throws Exception {

        List<Utente> utenti = new ArrayList<>();

        String sql = """
        SELECT ID_Utente, Nome, Cognome
        FROM utente
        WHERE Ruolo = 'COINQUILINO'
          AND ID_Utente <> ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idDaEscludere);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Utente u = new Utente();
                    u.setIdUtente(rs.getLong("ID_Utente"));
                    u.setNome(rs.getString("Nome"));
                    u.setCognome(rs.getString("Cognome"));
                    utenti.add(u);
                }
            }
        }

        return utenti;
    }

    public Utente doRetrieveByEmail(String email) throws Exception {
        String sql = """
            SELECT ID_Utente, Nome, Cognome, Email, Telefono, Ruolo
            FROM utente
            WHERE Email = ?
        """;
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
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

    public static boolean aggiornaUtenteConPassword(Utente utente, String nuovaPassword) {
        String hashed = BCrypt.hashpw(nuovaPassword, BCrypt.gensalt(12));
        String sql = "UPDATE credenziali SET PasswordHash = ? WHERE ID_Utente = ?";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hashed);
            ps.setLong(2, utente.getIdUtente());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean aggiornaPasswordByEmail(String email, String hashed) {
        String sqlSelect = "SELECT ID_Utente FROM utente WHERE Email = ?";
        String sqlUpdate = "UPDATE credenziali SET PasswordHash = ? WHERE ID_Utente = ?";

        try (Connection con = database.getConnection()) {
            long idUtente = -1;
            try (PreparedStatement ps = con.prepareStatement(sqlSelect)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idUtente = rs.getLong("ID_Utente");
                } else {
                    return false;
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setString(1, hashed);
                ps.setLong(2, idUtente);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
