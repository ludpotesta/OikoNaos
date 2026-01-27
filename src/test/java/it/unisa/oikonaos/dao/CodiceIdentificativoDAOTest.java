package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.CodiceIdentificativo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodiceIdentificativoDAOTest {

    private CodiceIdentificativoDAO codiceDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        codiceDAO = new CodiceIdentificativoDAO();
    }

    @Test
    void testGetCodiceValidoForUpdate_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("Codice")).thenReturn("CODICE_123");
        when(mockResultSet.getString("Stato")).thenReturn("ATTIVO");

        // ACT
        CodiceIdentificativo result = codiceDAO.getCodiceValidoForUpdate(mockConnection, "CODICE_123");

        // ASSERT
        assertNotNull(result);
        assertEquals("CODICE_123", result.getCodice());
        assertEquals("ATTIVO", result.getStato());
        verify(mockPreparedStatement).setString(1, "CODICE_123");
    }

    @Test
    void testGetCodiceValidoForUpdate_NotFound() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Non trovato o non attivo

        // ACT
        CodiceIdentificativo result = codiceDAO.getCodiceValidoForUpdate(mockConnection, "CODICE_INESISTENTE");

        // ASSERT
        assertNull(result, "Deve ritornare null se il codice non esiste o non è attivo");
    }

    //
    @Test
    void testMarcaComeUsato_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        codiceDAO.marcaComeUsato(mockConnection, "CODICE_123", 55L);

        // ASSERT
        verify(mockPreparedStatement).executeUpdate();
        // Verifica ordine parametri: 1=ID Utente, 2=Codice
        verify(mockPreparedStatement).setLong(1, 55L);
        verify(mockPreparedStatement).setString(2, "CODICE_123");
    }

    // --- TEST 3: VERIFICA ESISTENZA ---
    @Test
    void testCodiceEsisteEdAttivo_True() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Trovato!

        // ACT
        boolean esiste = codiceDAO.codiceEsisteEdAttivo(mockConnection, "CODICE_VALIDO");

        // ASSERT
        assertTrue(esiste);
    }

    @Test
    void testCodiceEsisteEdAttivo_False() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // ACT
        boolean esiste = codiceDAO.codiceEsisteEdAttivo(mockConnection, "CODICE_NON_VALIDO");

        // ASSERT
        assertFalse(esiste);
    }

    // Simuliamo un errore di lock sul database (es. deadlock o timeout)
    @Test
    void testGetCodiceValidoForUpdate_LockError() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Simuliamo che il DB lanci un errore quando proviamo a fare la query
        doThrow(new SQLException("Lock wait timeout exceeded"))
                .when(mockPreparedStatement).executeQuery();

        // ACT & ASSERT
        SQLException ex = assertThrows(SQLException.class, () -> {
            codiceDAO.getCodiceValidoForUpdate(mockConnection, "CODICE_BLOCCATO");
        });

        assertTrue(ex.getMessage().contains("Lock wait timeout"));
    }

    // --- TEST DI SICUREZZA: PREVENZIONE RIUTILIZZO ---

    @Test
    void testMarcaComeUsato_GiaUsato_Fail() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Simuliamo che il Database modifichi 0 righe usando un codice già usato
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // ACT
        boolean risultato = codiceDAO.marcaComeUsato(mockConnection, "CODICE_GIA_USATO", 10L);

        // ASSERT
        assertFalse(risultato, "Il metodo deve restituire false se il codice non era ATTIVO");

        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testMarcaComeUsato_CodiceInesistente() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Anche qui 0 righe modificate
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // ACT
        boolean risultato = codiceDAO.marcaComeUsato(mockConnection, "CODICE_FANTASMA", 10L);

        // ASSERT
        assertFalse(risultato, "Deve fallire se il codice non esiste proprio");
    }

    @Test
    void testMarcaComeUsato_Successo() throws Exception {
        // ARRANGE (Happy Path)
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Qui invece simuliamo una riga modificata
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // ACT
        boolean risultato = codiceDAO.marcaComeUsato(mockConnection, "CODICE_NUOVO", 55L);

        // ASSERT
        assertTrue(risultato, "Deve restituire true se il codice era ATTIVO ed è stato aggiornato");
    }
}