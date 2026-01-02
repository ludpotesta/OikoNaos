package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Utente;
import util.database;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    public void registerUser(
            String nome,
            String cognome,
            String email,
            String telefono,
            String username,
            String password,
            String codiceID
    ) throws Exception {

        Connection con = database.getConnection();
        con.setAutoCommit(false);

        try {

            // 1. Inserimento utente con comunità
            long idUtente = insertUtente(con, nome, cognome, email, telefono);

            // 2. Inserimento credenziali (con hash)
            insertCredenziali(con, username, password, idUtente);

            // 3. Marca codice come usato
            markCodiceUsato(con, codiceID, idUtente);

            con.commit();

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    /* ==========================
       METODI DI SUPPORTO
       ========================== */

    private long insertUtente(
            Connection con,
            String nome,
            String cognome,
            String email,
            String telefono
    ) throws Exception {

        String sql = "INSERT INTO Utente (Nome, Cognome, Email, Telefono, Ruolo) VALUES (?, ?, ?, ?, 'COINQUILINO')";

        try (PreparedStatement ps =
                     con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.setString(4, telefono);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getLong(1);
        }
    }

    private void insertCredenziali(
            Connection con,
            String username,
            String password,
            long idUtente
    ) throws Exception {

        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));

        String sql = "INSERT INTO Credenziali (Username, PasswordHash, ID_Utente) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setLong(3, idUtente);
            ps.executeUpdate();
        }
    }

    private void markCodiceUsato(
            Connection con,
            String codiceID,
            long idUtente
    ) throws Exception {

        String sql = "UPDATE CodiceIdentificativo SET Stato = 'USATO',  ID_Utente_Utilizzatore = ? WHERE Codice = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            ps.setString(2, codiceID);
            ps.executeUpdate();
        }
    }
       //LOGIN
    public Utente login(String username, String password) throws Exception {

        String sql = "SELECT u.ID_Utente, u.Nome, u.Cognome, u.Email, u.Telefono, u.Ruolo, c.PasswordHash FROM Utente u JOIN Credenziali c ON u.ID_Utente = c.ID_Utente WHERE c.Username = ?";

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
}
