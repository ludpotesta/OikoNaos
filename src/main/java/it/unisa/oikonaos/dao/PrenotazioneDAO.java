package it.unisa.oikonaos.dao;

import util.database;
import it.unisa.oikonaos.model.Prenotazione;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrenotazioneDAO {

    public void creaPrenotazione(Prenotazione p) throws Exception {
        String sql = "INSERT INTO prenotazione (DataPrenotazione, ID_Utente, ID_Postazione, ID_Fascia) VALUES (?, ?, ?, ?)";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, p.getData());
            ps.setLong(2, p.getIdUtente());
            ps.setLong(3, p.getIdPostazione());
            ps.setLong(4, p.getIdFasciaOraria());
            ps.executeUpdate();
        }
    }

    public boolean verificaConflitto(Date data, long idPostazione, long idFascia) throws Exception {
        String sql = "SELECT COUNT(*) FROM prenotazione WHERE DataPrenotazione = ? AND ID_Postazione = ? AND ID_Fascia = ?";
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

    public List<Prenotazione> doRetrieveByUtente(long idUtente) throws Exception {
        List<Prenotazione> lista = new ArrayList<>();
        String sql = "SELECT * FROM prenotazione WHERE ID_Utente = ?";
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

    // NUOVO METODO PER PERSONA 3 (ADMIN)
    public List<Prenotazione> doRetrieveAll() throws Exception {
        List<Prenotazione> lista = new ArrayList<>();
        String sql = "SELECT * FROM prenotazione";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
        String sql = "DELETE FROM prenotazione WHERE ID_Prenotazione = ?";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idPrenotazione);
            ps.executeUpdate();
        }
    }
}