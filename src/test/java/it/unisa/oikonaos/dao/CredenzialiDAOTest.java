package it.unisa.oikonaos.dao;

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
class CredenzialiDAOTest {

    private CredenzialiDAO credenzialiDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        credenzialiDAO = new CredenzialiDAO();
    }

    // TEST USERNAME ESISTENTE
    @Test
    void testUsernameEsistente_True() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Trovato!

        // ACT
        boolean esiste = credenzialiDAO.usernameEsistente(mockConnection, "marioRossi");

        // ASSERT
        assertTrue(esiste, "Deve restituire true se lo username è nel DB");
        verify(mockPreparedStatement).setString(1, "marioRossi");
    }

    @Test
    void testUsernameEsistente_False() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Non trovato

        // ACT
        boolean esiste = credenzialiDAO.usernameEsistente(mockConnection, "fantasma");

        // ASSERT
        assertFalse(esiste, "Deve restituire false se lo username non esiste");
    }

    // --- TEST GET ID BY EMAIL ---
    @Test
    void testGetIdUtenteByEmail_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("ID_Utente")).thenReturn(55L);

        // ACT
        Long id = credenzialiDAO.getIdUtenteByEmail(mockConnection, "test@mail.it");

        // ASSERT
        assertNotNull(id);
        assertEquals(55L, id);
    }

    // --- TEST UPDATE PASSWORD ---
    @Test
    void testUpdatePassword_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Simuliamo che executeUpdate restituisca 1 (1 riga modificata)
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // ACT
        boolean result = credenzialiDAO.updatePassword(mockConnection, 10L, "newHash123");

        // ASSERT
        assertTrue(result, "L'update deve ritornare true se ha modificato una riga");

        // Verifica ordine parametri: 1=Hash, 2=ID
        verify(mockPreparedStatement).setString(1, "newHash123");
        verify(mockPreparedStatement).setLong(2, 10L);
    }

    @Test
    void testUpdatePassword_Fail() throws Exception {
        // ARRANGE: Simuliamo che l'ID non esista, quindi 0 righe modificate
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // ACT
        boolean result = credenzialiDAO.updatePassword(mockConnection, 999L, "newHash");

        // ASSERT
        assertFalse(result, "Deve ritornare false se nessuna riga è stata aggiornata");
    }

    // --- TEST GET HASH ---
    @Test
    void testGetPasswordHashByUtente_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("PasswordHash")).thenReturn("$2a$10$XyZ...");

        // ACT
        String hash = credenzialiDAO.getPasswordHashByUtente(mockConnection, 1L);

        // ASSERT
        assertEquals("$2a$10$XyZ...", hash);
    }

    // EDGE CASES (Casi negativi estremi o particolari)

    @Test
    void testUpdateUsername_DuplicateError() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Simuliamo l'errore del DB: "Duplicate entry 'mario' for key 'username'"
        doThrow(new SQLException("Duplicate entry 'mario' for key 'username_UNIQUE'"))
                .when(mockPreparedStatement).executeUpdate();

        // ACT & ASSERT
        // Ci aspettiamo che il metodo fallisca lanciando l'eccezione
        SQLException exception = assertThrows(SQLException.class, () -> {
            credenzialiDAO.updateUsername(mockConnection, 10L, "mario");
        });

        // Opzionale: verifichiamo che il messaggio contenga info utili
        assertTrue(exception.getMessage().contains("Duplicate entry"));
    }

    @Test
    void testUpdatePassword_UserNotFound() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Simuliamo che il database dica: "Ho modificato 0 righe"
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // ACT
        // Proviamo ad aggiornare la password dell'ID 9999 (inesistente)
        boolean esito = credenzialiDAO.updatePassword(mockConnection, 9999L, "nuovoHash");

        // ASSERT
        assertFalse(esito, "Se l'utente non esiste, l'aggiornamento deve fallire (false)");
    }

    @Test
    void testGetIdUtenteByUsername_NullInput() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Se cerco NULL, il DB ovviamente non troverà nulla
        when(mockResultSet.next()).thenReturn(false);

        // ACT
        Long id = credenzialiDAO.getIdUtenteByUsername(mockConnection, null);

        // ASSERT
        assertNull(id);

        // Verifichiamo che abbia passato effettivamente null al driver JDBC
        verify(mockPreparedStatement).setString(1, null);
    }
}