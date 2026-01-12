package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.TassaTrimestrale;
import util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TassaDAO {

    public List<TassaTrimestrale> getTasseByUtente(long idUtente) {

        List<TassaTrimestrale> tasse = new ArrayList<>();

        String sql = "SELECT t.*, p.ID_Pagamento, r.ID_Ricevuta FROM tassatrimestrale t LEFT JOIN pagamento p ON t.ID_Tassa = p.ID_Tassa LEFT JOIN ricevuta r ON p.ID_Pagamento = r.ID_Pagamento WHERE t.ID_Utente = ?\n";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TassaTrimestrale t = new TassaTrimestrale();

                t.setIdTassa(rs.getLong("ID_Tassa"));
                t.setTrimestreRiferimento(rs.getString("TrimestreRiferimento"));
                t.setImportoDovuto(rs.getBigDecimal("ImportoDovuto"));
                t.setScadenza(rs.getDate("Scadenza").toLocalDate());
                t.setStato(rs.getString("Stato"));
                t.setIdPagamento(rs.getObject("ID_Pagamento", Long.class));
                t.setHasRicevuta(rs.getObject("ID_Ricevuta") != null);

                tasse.add(t);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return tasse;
    }

    public void marcaComePagata(long idTassa) {

        String sql = "UPDATE tassatrimestrale SET Stato = 'PAGATA' WHERE ID_Tassa = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTassa);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
