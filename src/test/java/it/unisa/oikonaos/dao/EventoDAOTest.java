package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.dto.EventoBachecaDTO;
import it.unisa.oikonaos.model.Evento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import util.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EventoDAOTest {

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPs;
    @Mock private ResultSet mockRs;

    private EventoDAO eventoDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventoDAO = new EventoDAO();
    }

    @Test
    void testGetEventiBacheca() throws Exception {
        long idUtente = 1L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            // Simuliamo due eventi trovati
            when(mockRs.next()).thenReturn(true).thenReturn(true).thenReturn(false);

            when(mockRs.getLong("ID_Evento")).thenReturn(10L).thenReturn(11L);
            when(mockRs.getString("Titolo")).thenReturn("Evento 1").thenReturn("Evento 2");
            when(mockRs.getTimestamp("DataInizio")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
            when(mockRs.getBoolean("Iscritto")).thenReturn(false).thenReturn(true);

            List<EventoBachecaDTO> risultati = eventoDAO.getEventiBacheca(idUtente);

            assertNotNull(risultati);
            assertEquals(2, risultati.size());
            assertEquals("Evento 1", risultati.get(0).getTitolo());
        }
    }

    @Test
    void testIscriviUtenteEvento_Successo() throws Exception {
        // Testiamo il metodo complesso con la transazione
        long idUtente = 5L;
        long idEvento = 10L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);

            // 1. SELECT (Controllo posti): Deve tornare 1 riga con posti > 0
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt("PostiDisponibili")).thenReturn(5); // Ci sono 5 posti

            // 2. INSERT e UPDATE: Restituiscono 1 (successo)
            when(mockPs.executeUpdate()).thenReturn(1);

            // Esecuzione
            eventoDAO.iscriviUtenteEvento(idUtente, idEvento);

            // Verifiche fondamentali per le transazioni
            verify(mockConnection).setAutoCommit(false); // Ha aperto la transazione
            verify(mockConnection).commit();             // Ha confermato la transazione
            verify(mockPs, atLeast(2)).executeUpdate();  // Ha fatto Insert e Update
        }
    }

    @Test
    void testIscriviUtenteEvento_Fallimento_PostiEsauriti() throws Exception {
        long idUtente = 5L;
        long idEvento = 10L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            // Simuliamo che i posti siano 0
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt("PostiDisponibili")).thenReturn(0);

            // Ci aspettiamo che lanci un'eccezione
            assertThrows(IllegalStateException.class, () -> {
                eventoDAO.iscriviUtenteEvento(idUtente, idEvento);
            });

            // Verifica che NON abbia fatto il commit
            verify(mockConnection, never()).commit();
        }
    }

    @Test
    void testCreaEvento() throws Exception {
        Evento nuovoEvento = new Evento();
        nuovoEvento.setTitolo("Nuovo Evento");
        nuovoEvento.setDescrizione("Descrizione");
        nuovoEvento.setLuogo("Aula 1");
        nuovoEvento.setDataInizio(LocalDateTime.now().plusDays(1));
        nuovoEvento.setPostiTotali(100);
        nuovoEvento.setPostiDisponibili(100);
        nuovoEvento.setIdOrganizzatore(1L);

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);

            eventoDAO.creaEvento(nuovoEvento);

            verify(mockPs).executeUpdate(); // Verifica che abbia eseguito l'insert
            verify(mockPs).setString(1, "Nuovo Evento");
        }
    }

    @Test
    void testEliminaEvento() throws Exception {
        long idEvento = 99L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);

            eventoDAO.eliminaEvento(idEvento);

            verify(mockPs).setLong(1, idEvento);
            verify(mockPs).executeUpdate();
        }
    }

    @Test
    void testDisiscriviUtenteDaEvento() throws Exception {
        long idEvento = 10L;
        long idUtente = 5L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);

            eventoDAO.disiscriviUtenteDaEvento(idEvento, idUtente);

            verify(mockConnection).setAutoCommit(false);
            verify(mockConnection).commit();
            // Deve fare due update (Delete iscrizione + Update posti)
            verify(mockPs, times(2)).executeUpdate();
        }
    }
}