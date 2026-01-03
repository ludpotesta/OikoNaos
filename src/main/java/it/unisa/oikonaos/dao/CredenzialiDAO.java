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

    public boolean aggiornaUsername(long idUtente, String nuovoUsername) throws Exception {

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
}
