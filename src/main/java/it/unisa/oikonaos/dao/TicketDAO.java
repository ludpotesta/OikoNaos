package it.unisa.oikonaos.dao;

import util.database;
import it.unisa.oikonaos.model.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    /**
     * Crea un nuovo ticket impostando lo stato predefinito a 'APERTO'
     */
    public long creaTicket(
            String titolo,
            String descrizione,
            String categoria,
            String priorita,
            long idAutore
    ) throws Exception {

        // Modifica: Aggiunto 'Stato' e il sesto parametro '?'
        String sql = "INSERT INTO Ticket (Titolo, Descrizione, Categoria, Priorita, ID_Autore, Stato) VALUES (?, ?, ?, ?, ?, ?)";

        Connection con = database.getConnection();
        // RETURN_GENERATED_KEYS serve per recuperare l'ID creato dal database
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        try {
            ps.setString(1, titolo);
            ps.setString(2, descrizione);
            ps.setString(3, categoria);
            ps.setString(4, priorita);
            ps.setLong(5, idAutore);
            ps.setString(6, "APERTO"); // Forza lo stato iniziale a APERTO

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;

        } catch (Exception e) {
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }

    /**
     * Recupera la lista dei ticket filtrata per autore (Persona 2)
     */
    public List<Ticket> doRetrieveByAutore(long idAutore) throws Exception {
        List<Ticket> lista = new ArrayList<>();
        String sql = "SELECT * FROM Ticket WHERE ID_Autore = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idAutore);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ticket t = new Ticket();
                t.setIdTicket(rs.getLong("ID_Ticket"));
                t.setTitolo(rs.getString("Titolo"));
                t.setDescrizione(rs.getString("Descrizione"));
                t.setCategoria(rs.getString("Categoria"));
                t.setPriorita(rs.getString("Priorita"));
                t.setStato(rs.getString("Stato"));
                // AGGIUNTA: Leggiamo anche la data di apertura
                t.setDataApertura(rs.getTimestamp("DataApertura"));
                lista.add(t);
            }
        }
        return lista;
    }
}