package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.AggiornamentoTicket;
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
class AggiornamentoTicketDAOTest {

    private AggiornamentoTicketDAO aggiornamentoDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        aggiornamentoDAO = new AggiornamentoTicketDAO();
    }

    // --- TEST CREAZIONE ---
    @Test
    void testCreaAggiornamento_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        aggiornamentoDAO.creaAggiornamento(mockConnection, 100L, 5L, "Problema risolto?");

        // ASSERT
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).setString(1, "Problema risolto?"); // Messaggio
        verify(mockPreparedStatement).setLong(2, 100L); // ID Ticket
        verify(mockPreparedStatement).setLong(3, 5L);   // ID Autore
    }

    // --- TEST RETRIEVE BY TICKET (JOIN) ---
    @Test
    void testDoRetrieveByTicket_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulazione: 2 aggiornamenti trovati
        when(mockResultSet.next()).thenReturn(true, true, false);

        // Riga 1 (Messaggio più recente)
        when(mockResultSet.getString("Messaggio")).thenReturn("Sì, funziona", "Ho ancora problemi");
        when(mockResultSet.getTimestamp("DataAggiornamento")).thenReturn(
                Timestamp.valueOf("2026-05-20 10:30:00"),
                Timestamp.valueOf("2026-05-20 10:00:00")
        );
        // Dati Utente (dalla JOIN)
        when(mockResultSet.getString("Nome")).thenReturn("Mario");
        when(mockResultSet.getString("Cognome")).thenReturn("Rossi");

        // ACT
        List<AggiornamentoTicket> result = aggiornamentoDAO.doRetrieveByTicket(mockConnection, 100L);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verifica contenuto primo messaggio
        assertEquals("Sì, funziona", result.get(0).getMessaggio());
        assertEquals("Mario", result.get(0).getNomeUtente());

        // Verifica che la query contenga l'ordinamento corretto


    }

    @Test
    void testDoRetrieveByTicket_Empty() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Nessun messaggio

        // ACT
        List<AggiornamentoTicket> result = aggiornamentoDAO.doRetrieveByTicket(mockConnection, 999L);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- CASO PARTICOLARE: Messaggio Vuoto ---

    @Test
    void testCreaAggiornamento_EmptyString() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        aggiornamentoDAO.creaAggiornamento(mockConnection, 100L, 5L, "");

        // ASSERT
        verify(mockPreparedStatement).setString(1, "");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testCreaAggiornamento_SqlInjection() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Stringa malevola che prova a cancellare la tabella
        String messaggioPericoloso = "'); DROP TABLE utente; --";

        // ACT
        aggiornamentoDAO.creaAggiornamento(mockConnection, 100L, 5L, messaggioPericoloso);

        // ASSERT
        // Verifichiamo che il DAO passi la stringa *esattamente così com'è* al driver,

        verify(mockPreparedStatement).setString(1, messaggioPericoloso);
        verify(mockPreparedStatement).executeUpdate();
    }
    @Test
    void testCreaAggiornamento_TicketNonTrovato() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Simuliamo l'errore del DB: "Cannot add or update a child row: a foreign key constraint fails"
        doThrow(new SQLException("Foreign key constraint fails"))
                .when(mockPreparedStatement).executeUpdate();

        // ACT & ASSERT
        // Ci aspettiamo che l'eccezione risalga
        assertThrows(SQLException.class, () -> {
            aggiornamentoDAO.creaAggiornamento(mockConnection, 99999L, 5L, "Messaggio orfano");
        });
    }
}