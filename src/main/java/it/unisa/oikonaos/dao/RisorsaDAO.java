package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Risorsa;
import util.database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RisorsaDAO {

    public List<Risorsa> findAllDisponibili() throws Exception {
        String sql = " SELECT * FROM Risorsa WHERE Disponibile = true ";

        List<Risorsa> lista = new ArrayList<>();
        
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Risorsa r = new Risorsa();
                r.setId(rs.getLong("ID_Risorsa"));
                r.setNome(rs.getString("Nome"));
                r.setDescrizione(rs.getString("Descrizione"));
                r.setDisponibile(rs.getBoolean("Disponibile"));
                lista.add(r);
            }
        }
        return lista;
    }

    public void aggiornaDisponibilita(long idRisorsa, boolean disponibile) throws Exception {
        String sql = " UPDATE Risorsa SET Disponibile = ? WHERE ID_Risorsa = ? ";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, disponibile);
            ps.setLong(2, idRisorsa);
            ps.executeUpdate();
        }
    }
}

