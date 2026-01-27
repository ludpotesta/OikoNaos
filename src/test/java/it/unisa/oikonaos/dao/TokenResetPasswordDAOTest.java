package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.TokenResetPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenResetPasswordDAOTest {

    private TokenResetPasswordDAO tokenDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        tokenDAO = new TokenResetPasswordDAO();
    }

    // --- TEST CREAZIONE TOKEN (Il più complesso) ---
    @Test
    void testCreateToken_Success() throws Exception {
        // ARRANGE
        // Prepariamo il mock per DUE query diverse:
        // 1. DELETE (pulizia vecchi token)
        // 2. INSERT (nuovo token)
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        String tokenGenerato = tokenDAO.createToken(mockConnection, 100L);

        // ASSERT
        assertNotNull(tokenGenerato);
        assertTrue(tokenGenerato.length() > 20, "Il token deve essere lungo e complesso");

        // Verifica che siano state eseguite DUE operazioni di scrittura (delete + insert)
        verify(mockPreparedStatement, times(2)).executeUpdate();

        // Verifica avanzata: controlliamo che il token salvato nel DB sia quello ritornato
        ArgumentCaptor<String> captorToken = ArgumentCaptor.forClass(String.class);
        // Catturiamo il primo argomento (Token) passato alla INSERT
        // Nota: Poiché setString viene chiamato anche per la DELETE, dobbiamo fare attenzione.
        // Ma nel INSERT il token è il parametro 1.
        verify(mockPreparedStatement, atLeastOnce()).setString(eq(1), captorToken.capture());

        // Controlliamo se uno dei valori catturati corrisponde al token generato
        assertTrue(captorToken.getAllValues().contains(tokenGenerato));
    }

    // --- TEST RECUPERO UTENTE DA TOKEN (Validità) ---
    @Test
    void testGetIdUtenteByToken_Valid() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("ID_Utente")).thenReturn(50L);

        // ACT
        Long id = tokenDAO.getIdUtenteByToken(mockConnection, "TOKEN_VALIDO");

        // ASSERT
        assertNotNull(id);
        assertEquals(50L, id);
    }

    @Test
    void testGetIdUtenteByToken_ExpiredOrInvalid() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo che la query (che contiene 'DataScadenza > NOW()') non ritorni nulla
        // Questo copre sia il caso "Token non trovato" sia "Token Scaduto"
        when(mockResultSet.next()).thenReturn(false);

        // ACT
        Long id = tokenDAO.getIdUtenteByToken(mockConnection, "TOKEN_SCADUTO");

        // ASSERT
        assertNull(id, "Deve restituire null se il token è scaduto o inesistente");
    }

    // --- TEST FIND BY TOKEN ---
    @Test
    void testFindByToken_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        // Mappatura completa dei campi
        when(mockResultSet.getLong("ID_Token")).thenReturn(1L);
        when(mockResultSet.getString("Token")).thenReturn("abc-123");
        when(mockResultSet.getTimestamp("DataScadenza")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(mockResultSet.getLong("ID_Utente")).thenReturn(10L);

        // ACT
        TokenResetPassword t = tokenDAO.findByToken(mockConnection, "abc-123");

        // ASSERT
        assertNotNull(t);
        assertEquals("abc-123", t.getToken());
        assertEquals(10L, t.getIdUtente());
    }

    // --- TEST INVALIDATE TOKEN ---
    @Test
    void testInvalidateToken() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        tokenDAO.invalidateToken(mockConnection, "TOKEN_USATO");

        // ASSERT
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).setString(1, "TOKEN_USATO");
    }
}