package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Pagamento;
import util.database;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {

    public List<Pagamento> getPagamentiByUtente(long idUtente) {

        List<Pagamento> pagamenti = new ArrayList<>();

        String sql = "SELECT p.ID_Pagamento, p.ImportoPagato, p.DataPagamento, p.MetodoPagamento, p.ID_Tassa, t.Periodo FROM pagamento p JOIN tassa t ON p.ID_Tassa = t.ID_Tassa WHERE p.ID_Utente = ? ORDER BY t.Periodo";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Pagamento p = new Pagamento();

                p.setIdPagamento(rs.getLong("ID_Pagamento"));
                p.setIdUtente(idUtente);
                p.setIdTassa(rs.getLong("ID_Tassa"));
                p.setImportoPagato(rs.getBigDecimal("ImportoPagato"));
                p.setMetodoPagamento(rs.getString("MetodoPagamento"));

                Timestamp ts = rs.getTimestamp("DataPagamento");
                if (ts != null) {
                    p.setDataPagamento(ts.toLocalDateTime());
                }

                p.setPeriodo(rs.getString("Periodo"));

                pagamenti.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pagamenti;
    }

    public void registraPagamentoOnline(long idPagamento) {

        String sql = "UPDATE pagamento SET DataPagamento = CURRENT_TIMESTAMP, MetodoPagamento = ? WHERE ID_Pagamento = ? AND DataPagamento IS NULL";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, Pagamento.METODO_ONLINE);
            ps.setLong(2, idPagamento);

            int updated = ps.executeUpdate();

            // SOLO se il pagamento è avvenuto ora
            if (updated == 1) {
                // recupero importo
                String q = "SELECT ImportoPagato FROM pagamento WHERE ID_Pagamento = ?";
                try (PreparedStatement ps2 = con.prepareStatement(q)) {
                    ps2.setLong(1, idPagamento);
                    ResultSet rs = ps2.executeQuery();
                    if (rs.next()) {
                        double importo = rs.getDouble("ImportoPagato");
                        new RicevutaDAO().creaRicevuta(idPagamento, importo);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
