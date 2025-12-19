package it.unisa.oikonaos.model;

import util.database; // Importa l'utility di connessione del gruppo
import java.sql.*;

public class PrenotazioneDAO {

    // Metodo richiesto dall'ODD per registrare una prenotazione [cite: 223, 340]
    public void creaPrenotazione(Prenotazione p) throws Exception {
        String sql = "INSERT INTO Prenotazione (Data, ID_Utente, ID_Postazione, ID_FasciaOraria) VALUES (?, ?, ?, ?)";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, p.getData());
            ps.setLong(2, p.getIdUtente());
            ps.setLong(3, p.getIdPostazione());
            ps.setLong(4, p.getIdFasciaOraria());

            ps.executeUpdate();
        }
    }

    // Metodo richiesto dall'ODD per verificare conflitti temporali [cite: 223, 340]
    public boolean verificaConflitto(Date data, long idPostazione, long idFascia) throws Exception {
        String sql = "SELECT COUNT(*) FROM Prenotazione WHERE Data = ? AND ID_Postazione = ? AND ID_FasciaOraria = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, data);
            ps.setLong(2, idPostazione);
            ps.setLong(3, idFascia);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
