package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    private UserDAO userDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString())) // per insertCredenziali
                .thenReturn(mockPreparedStatement);

        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);

        // ACT
        long resultId = userDAO.registerUser(
                mockConnection, "Mario", "Rossi", "test@email.it", "333", "user", "pass"
        );

        // ASSERT
        assertEquals(1L, resultId);

        verify(mockPreparedStatement).setString(1, "Mario");
        verify(mockPreparedStatement).setString(2, "Rossi");
        verify(mockPreparedStatement).setString(3, "test@email.it");
    }

    @Test
    void testLogin_Success() throws Exception {
        // ARRANGE
        String realPassword = "passwordSicura";
        String hashedPassword = BCrypt.hashpw(realPassword, BCrypt.gensalt());

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo che il ResultSet trovi una riga
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getString("PasswordHash")).thenReturn(hashedPassword);

        when(mockResultSet.getLong("ID_Utente")).thenReturn(10L);

        when(mockResultSet.getString("Nome")).thenReturn("Luigi");
        when(mockResultSet.getString("Cognome")).thenReturn("Verdi");
        when(mockResultSet.getString("Email")).thenReturn("l.verdi@test.it");
        when(mockResultSet.getString("Telefono")).thenReturn("3331234567");
        when(mockResultSet.getString("Ruolo")).thenReturn("COINQUILINO");

        // ACT
        // Chiamiamo il metodo passando la connessione mockata
        Utente loggedUser = userDAO.login(mockConnection, "luigiUser", realPassword);

        // ASSERT
        assertNotNull(loggedUser);
        assertEquals("Luigi", loggedUser.getNome());
        assertEquals("Verdi", loggedUser.getCognome()); // Ora puoi testare anche questo
        assertEquals("COINQUILINO", loggedUser.getRuolo());
    }

    @Test
    void testLogin_WrongPassword() throws Exception {
        // ARRANGE
        String storedHash = BCrypt.hashpw("passwordGiusta", BCrypt.gensalt());

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("PasswordHash")).thenReturn(storedHash);

        // ACT & ASSERT
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            // Chiamiamo la versione con mockConnection
            userDAO.login(mockConnection, "user", "passwordSbagliata");
        });

        assertEquals("Username o password errati", exception.getMessage());
    }

    @Test
    void testGetUtenteById_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("ID_Utente")).thenReturn(5L);
        when(mockResultSet.getString("Nome")).thenReturn("Giulia");

        // ACT
        Utente result = userDAO.getUtenteById(mockConnection, 5L);

        // ASSERT
        assertNotNull(result);
        assertEquals("Giulia", result.getNome());
    }

    @Test
    void testUpdateProfilo_Success() throws Exception {
        // ARRANGE
        // Prepariamo l'utente con i nuovi dati
        Utente u = new Utente();
        u.setIdUtente(10L);
        u.setNome("MarioModificato");
        u.setCognome("RossiModificato");
        u.setEmail("nuova@email.it");
        u.setTelefono("999999999");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        userDAO.updateProfilo(mockConnection, u);

        // ASSERT
        // Verifichiamo che la query venga eseguita
        verify(mockPreparedStatement).executeUpdate();

        // Verifichiamo che i parametri siano passati nell'ordine giusto
        // Ordine SQL: Nome(1), Cognome(2), Email(3), Telefono(4), ID(5)
        verify(mockPreparedStatement).setString(1, "MarioModificato");
        verify(mockPreparedStatement).setString(2, "RossiModificato");
        verify(mockPreparedStatement).setString(3, "nuova@email.it");
        verify(mockPreparedStatement).setString(4, "999999999");
        verify(mockPreparedStatement).setLong(5, 10L);
    }

    @Test
    void testDoRetrieveCoinquiliniEscluso_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo il ciclo while(rs.next()):

        when(mockResultSet.next()).thenReturn(true, true, false);

        // Definizione dei dati per il primo giro
        when(mockResultSet.getLong("ID_Utente")).thenReturn(1L, 2L);
        when(mockResultSet.getString("Nome")).thenReturn("Anna", "Marco");
        when(mockResultSet.getString("Cognome")).thenReturn("Bianchi", "Neri");

        // ACT
        // Chiediamo di escludere l'ID 5 (non influisce sul mock, ma serve alla chiamata)
        List<Utente> risultati = userDAO.doRetrieveCoinquiliniEscluso(mockConnection, 5L);

        // ASSERT
        assertNotNull(risultati);
        assertEquals(2, risultati.size(), "Deve trovare 2 coinquilini");

        // Controllo primo elemento
        assertEquals("Anna", risultati.get(0).getNome());
        assertEquals("Bianchi", risultati.get(0).getCognome());

        // Controllo secondo elemento
        assertEquals("Marco", risultati.get(1).getNome());

        // Verifichiamo che l'ID da escludere sia stato settato nel PreparedStatement
        verify(mockPreparedStatement).setLong(1, 5L);
    }

    // EDGE CASES (Casi negativi estremi o particolari)
    @Test
    void testRegisterUser_DuplicateEmail() throws Exception {
        // ARRANGE
        // Simuliamo che la prepareStatement funzioni
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);

        // Quando proviamo a fare l'inserimento, il DB restituisce errore
        doThrow(new SQLException("Duplicate entry 'mario@test.it' for key 'email'"))
                .when(mockPreparedStatement).executeUpdate();

        // ACT & ASSERT
        // Ci aspettiamo che il metodo fallisca lanciando SQLException
        SQLException exception = assertThrows(SQLException.class, () -> {
            userDAO.registerUser(
                    mockConnection, "Mario", "Rossi", "mario@test.it",
                    "333", "mario", "pass"
            );
        });

        // Verifichiamo che il messaggio sia quello giusto
        assertTrue(exception.getMessage().contains("Duplicate entry"));
    }

    @Test
    void testLogin_DatabaseError() throws Exception {
        // ARRANGE
        // Simuliamo che appena proviamo a creare la query, il DB dia errore
        when(mockConnection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Connection timed out"));

        // ACT & ASSERT
        assertThrows(SQLException.class, () -> {
            userDAO.login(mockConnection, "user", "pass");
        });
    }

    @Test
    void testGetUtenteById_NotFound() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo che il ResultSet sia vuoto (nessun utente trovato con quell'ID)
        when(mockResultSet.next()).thenReturn(false);

        // ACT
        Utente result = userDAO.getUtenteById(mockConnection, 9999L); // ID inesistente

        // ASSERT
        // Deve restituire null, non deve lanciare eccezioni
        assertNull(result, "Se l'utente non esiste, il DAO deve restituire null");
    }

}