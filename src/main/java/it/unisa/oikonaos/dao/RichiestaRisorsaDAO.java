package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.RichiestaRisorsa;
import util.database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RichiestaRisorsaDAO {

    public void creaRichiesta(RichiestaRisorsa r) throws Exception {
        String sql = """
            INSERT INTO RichiestaRisorsa
            (ID_Risorsa, ID_Utente, Stato, Note)
            VALUES (?, ?, 'IN_ATTESA', ?)
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, r.getIdRisorsa());
            ps.setLong(2, r.getIdUtente());
            ps.setString(3, r.getNote());
            ps.executeUpdate();
        }
    }

    public List<RichiestaRisorsa> findByUtente(long idUtente) throws Exception {
        String sql = """
            SELECT *
            FROM RichiestaRisorsa
            WHERE ID_Utente = ?
            ORDER BY DataRichiesta DESC
        """;

        List<RichiestaRisorsa> lista = new ArrayList<>();

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        }
        return lista;
    }

    public List<RichiestaRisorsa> findInAttesa() throws Exception {
        String sql = """
            SELECT *
            FROM RichiestaRisorsa
            WHERE Stato = 'IN_ATTESA'
            ORDER BY DataRichiesta
        """;

        List<RichiestaRisorsa> lista = new ArrayList<>();

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    public void aggiornaStato(long idRichiesta, String stato, long idSupervisore) throws Exception {
        String sql = """
            UPDATE RichiestaRisorsa
            SET Stato = ?, ID_Supervisore = ?
            WHERE ID_Richiesta = ?
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, stato);
            ps.setLong(2, idSupervisore);
            ps.setLong(3, idRichiesta);
            ps.executeUpdate();
        }
    }

    private RichiestaRisorsa map(ResultSet rs) throws SQLException {
        RichiestaRisorsa r = new RichiestaRisorsa();
        r.setId(rs.getLong("ID_Richiesta"));
        r.setDataRichiesta(rs.getTimestamp("DataRichiesta"));
        r.setStato(rs.getString("Stato"));
        r.setNote(rs.getString("Note"));
        r.setIdRisorsa(rs.getLong("ID_Risorsa"));
        r.setIdUtente(rs.getLong("ID_Utente"));
        r.setIdSupervisore(rs.getObject("ID_Supervisore", Long.class));
        return r;
    }
}

