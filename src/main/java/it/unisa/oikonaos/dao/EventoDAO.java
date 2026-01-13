package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.dto.EventoBachecaDTO;
import util.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public List<EventoBachecaDTO> getEventiBacheca(long idUtente) throws Exception {

        String sql = """
        SELECT 
            e.ID_Evento,
            e.Titolo,
            e.Descrizione,
            e.Luogo,
            e.DataInizio,
            e.DataFine,
            e.PostiDisponibili,
            CASE 
                WHEN ie.ID_Iscrizione IS NULL THEN false
                ELSE true
            END AS Iscritto
        FROM evento e
        LEFT JOIN iscrizioneevento ie
            ON e.ID_Evento = ie.ID_Evento
           AND ie.ID_Utente = ?
        ORDER BY e.DataInizio ASC
    """;

        List<EventoBachecaDTO> eventi = new ArrayList<>();

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente); // ✔ SOLO UNO

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EventoBachecaDTO e = new EventoBachecaDTO();

                e.setIdEvento(rs.getLong("ID_Evento"));
                e.setTitolo(rs.getString("Titolo"));
                e.setDescrizione(rs.getString("Descrizione"));
                e.setLuogo(rs.getString("Luogo"));
                e.setDataInizio(rs.getTimestamp("DataInizio").toLocalDateTime());
                e.setDataFine(rs.getTimestamp("DataFine").toLocalDateTime());
                e.setPostiDisponibili(rs.getInt("PostiDisponibili"));
                e.setIscritto(rs.getBoolean("Iscritto"));

                eventi.add(e);
            }
        }

        return eventi;
    }

    public void iscriviUtenteEvento(long idUtente, long idEvento) throws Exception {

        String checkSql = """
        SELECT PostiDisponibili
        FROM evento
        WHERE ID_Evento = ?
        FOR UPDATE
    """;

        String insertSql = """
        INSERT INTO iscrizioneevento (ID_Utente, ID_Evento)
        VALUES (?, ?)
    """;

        String updateSql = """
        UPDATE evento
        SET PostiDisponibili = PostiDisponibili - 1
        WHERE ID_Evento = ?
    """;

        try (Connection con = database.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement check = con.prepareStatement(checkSql)) {
                check.setLong(1, idEvento);
                ResultSet rs = check.executeQuery();

                if (!rs.next() || rs.getInt("PostiDisponibili") <= 0) {
                    throw new IllegalStateException("Posti esauriti");
                }
            }

            try (PreparedStatement ins = con.prepareStatement(insertSql)) {
                ins.setLong(1, idUtente);
                ins.setLong(2, idEvento);
                ins.executeUpdate();
            }

            try (PreparedStatement upd = con.prepareStatement(updateSql)) {
                upd.setLong(1, idEvento);
                upd.executeUpdate();
            }

            con.commit();
        }
    }

    public EventoBachecaDTO getEventoById(long idEvento, long idUtente) throws Exception {

        String sql = """
        SELECT e.ID_Evento, e.Titolo, e.Descrizione, e.Luogo,
               e.DataInizio, e.PostiDisponibili,
               CASE WHEN ie.ID_Iscrizione IS NULL THEN false ELSE true END AS Iscritto
        FROM evento e
        LEFT JOIN iscrizioneevento ie
               ON e.ID_Evento = ie.ID_Evento AND ie.ID_Utente = ?
        WHERE e.ID_Evento = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ps.setLong(2, idEvento);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            EventoBachecaDTO e = new EventoBachecaDTO();
            e.setIdEvento(rs.getLong("ID_Evento"));
            e.setTitolo(rs.getString("Titolo"));
            e.setDescrizione(rs.getString("Descrizione"));
            e.setLuogo(rs.getString("Luogo"));
            e.setDataInizio(rs.getTimestamp("DataInizio").toLocalDateTime());
            e.setPostiDisponibili(rs.getInt("PostiDisponibili"));
            e.setIscritto(rs.getBoolean("Iscritto"));

            return e;
        }
    }

    public void disiscriviUtenteDaEvento(long idEvento, long idUtente) throws Exception {

        String deleteSql = """
        DELETE FROM iscrizioneevento
        WHERE ID_Evento = ? AND ID_Utente = ?
    """;

        String updateSql = """
        UPDATE evento
        SET PostiDisponibili = PostiDisponibili + 1
        WHERE ID_Evento = ?
    """;

        try (Connection con = database.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement psDelete = con.prepareStatement(deleteSql);
                 PreparedStatement psUpdate = con.prepareStatement(updateSql)) {

                psDelete.setLong(1, idEvento);
                psDelete.setLong(2, idUtente);
                psDelete.executeUpdate();

                psUpdate.setLong(1, idEvento);
                psUpdate.executeUpdate();

                con.commit();
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        }
    }
}
