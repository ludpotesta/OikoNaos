package it.unisa.oikonaos.model;

import util.database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrenotazioneDAO {

    // Modificato: Accetta esplicitamente java.sql.Date
    public boolean verificaConflitto(java.sql.Date data, long idPostazione, long idFascia) throws Exception {
        String sql = "SELECT COUNT(*) FROM Prenotazione WHERE Data = ? AND ID_Postazione = ? AND ID_FasciaOraria = ?";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, data);
            ps.setLong(2, idPostazione);
            ps.setLong(3, idFascia);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public void creaPrenotazione(Prenotazione p) throws Exception {
        String sql = "INSERT INTO Prenotazione (Data, ID_Utente, ID_Postazione, ID_FasciaOraria) VALUES (?, ?, ?, ?)";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, p.getData()); // p.getData() deve restituire java.sql.Date
            ps.setLong(2, p.getIdUtente());
            ps.setLong(3, p.getIdPostazione());
            ps.setLong(4, p.getIdFasciaOraria());
            ps.executeUpdate();
        }
    }

    public List<Prenotazione> doRetrieveByUtente(long idUtente) throws Exception {
        List<Prenotazione> lista = new ArrayList<>();
        String sql = "SELECT * FROM Prenotazione WHERE ID_Utente = ?";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Prenotazione p = new Prenotazione();
                p.setIdPrenotazione(rs.getLong("ID_Prenotazione"));
                p.setData(rs.getDate("Data"));
                p.setIdUtente(rs.getLong("ID_Utente"));
                p.setIdPostazione(rs.getLong("ID_Postazione"));
                p.setIdFasciaOraria(rs.getLong("ID_FasciaOraria"));
                lista.add(p);
            }
        }
        return lista;
    }

    public void doDelete(long idPrenotazione) throws Exception {
        String sql = "DELETE FROM Prenotazione WHERE ID_Prenotazione = ?";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idPrenotazione);
            ps.executeUpdate();
        }
    }
}