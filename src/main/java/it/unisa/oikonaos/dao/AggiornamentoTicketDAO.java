package it.unisa.oikonaos.dao;

import util.database;
import java.sql.*;

public class AggiornamentoTicketDAO {

    public void creaAggiornamento(long idTicket, long idAutore, String msg) throws Exception {

        String sql = "INSERT INTO AggiornamentoTicket (Messaggio, ID_Ticket, ID_Autore) VALUES (?, ?, ?)";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, msg);
            ps.setLong(2, idTicket);
            ps.setLong(3, idAutore);

            ps.executeUpdate();
        }
    }
}
