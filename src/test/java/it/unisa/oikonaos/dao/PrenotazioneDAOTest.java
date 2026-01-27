package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Prenotazione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrenotazioneDAOTest {

    private PrenotazioneDAO prenotazioneDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        prenotazioneDAO = new PrenotazioneDAO();
    }

    @Test
    void testCreaPrenotazione_Success() throws Exception {
        // ARRANGE
        Prenotazione p = new Prenotazione();
        p.setData(Date.valueOf("2026-01-20"));
        p.setStato("ATTIVA");
        p.setIdUtente(10L);
        p.setIdPostazione(5L);
        p.setIdFasciaOraria(1L);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        prenotazioneDAO.creaPrenotazione(mockConnection, p);

        // ASSERT
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).setLong(3, 10L); // ID Utente
    }

    // Verifica che il sistema rilevi se una postazione è già occupata
    @Test
    void testVerificaConflitto_True() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1); // Trovata 1 prenotazione sovrapposta

        // ACT
        boolean conflitto = prenotazioneDAO.verificaConflitto(
                mockConnection, Date.valueOf("2026-01-20"), 5L, 1L
        );

        // ASSERT
        assertTrue(conflitto, "Deve restituire true se c'è un conflitto");
    }

    @Test
    void testVerificaConflitto_False() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0); // Trovate 0 prenotazioni

        // ACT
        boolean conflitto = prenotazioneDAO.verificaConflitto(
                mockConnection, Date.valueOf("2026-01-20"), 5L, 1L
        );

        // ASSERT
        assertFalse(conflitto);
    }

    // Verifica che un utente non possa cancellare la prenotazione di un altro
    @Test
    void testDoDelete_UnauthorizedFail() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Simuliamo 0 righe cancellate perché l'ID utente non corrispondeva
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // ACT
        boolean risultato = prenotazioneDAO.doDelete(mockConnection, 100L, 99L);

        // ASSERT
        assertFalse(risultato, "Deve restituire false se l'utente non è il proprietario");
    }

    @Test
    void testDoDelete_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Successo

        // ACT
        boolean risultato = prenotazioneDAO.doDelete(mockConnection, 100L, 10L);

        // ASSERT
        assertTrue(risultato);
    }

    // --- TEST RETRIEVE BY UTENTE ---
    @Test
    void testDoRetrieveByUtente_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo che il ResultSet trovi 1 riga (true) e poi finisca (false)
        when(mockResultSet.next()).thenReturn(true, false);

        when(mockResultSet.getLong("ID_Prenotazione")).thenReturn(50L);
        when(mockResultSet.getDate("DataPrenotazione")).thenReturn(Date.valueOf("2026-01-20"));
        when(mockResultSet.getString("Stato")).thenReturn("ATTIVA"); // <--- Ecco quello che mancava!
        when(mockResultSet.getString("NumeroPostazione")).thenReturn("A01");
        when(mockResultSet.getString("NomeAmbiente")).thenReturn("Aula Studio");
        when(mockResultSet.getTime("OraInizio")).thenReturn(Time.valueOf("09:00:00"));
        when(mockResultSet.getTime("OraFine")).thenReturn(Time.valueOf("13:00:00"));

        // ACT
        List<Prenotazione> lista = prenotazioneDAO.doRetrieveByUtente(mockConnection, 10L);

        // ASSERT
        assertNotNull(lista);
        assertEquals(1, lista.size());

        Prenotazione p = lista.get(0);
        assertEquals(50L, p.getIdPrenotazione());
        assertEquals("Aula Studio", p.getNomeAmbiente());
        assertEquals("ATTIVA", p.getStato());
        assertEquals("A01", p.getNumeroPostazione());
    }
}