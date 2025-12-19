package it.unisa.oikonaos.dao;

import util.database;
import java.sql.*;

public class TicketDAO {

    public long creaTicket(
            String titolo,
            String descrizione,
            String categoria,
            String priorita,
            long idAutore
    ) throws Exception {

        String sql = "INSERT INTO Ticket (Titolo, Descrizione, Categoria, Priorita, ID_Autore) VALUES (?, ?, ?, ?, ?)";

        //RETURN_GENERATED_KEYS Serve per conoscere gli ID creati in automatico dal DB
        Connection con = database.getConnection();
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        try {
            ps.setString(1, titolo);
            ps.setString(2, descrizione);
            ps.setString(3, categoria);
            ps.setString(4, priorita);
            ps.setLong(5, idAutore);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getLong(1);

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.close();
        }
    }
}

