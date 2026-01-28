package it.unisa.oikonaos.integration;

import it.unisa.oikonaos.controller.PrenotazioneController;
import it.unisa.oikonaos.dao.PrenotazioneDAO;
import it.unisa.oikonaos.model.Prenotazione;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrenotazioniControllerIT {

    // IT-PREN-01
    @Test
    void creazionePrenotazioneValida() throws Exception {

        System.out.println("[IT-PREN-01] Avvio test");

        PrenotazioneController controller = new PrenotazioneController();
        System.out.println("[IT-PREN-01] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-PREN-01] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(10L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-PREN-01] Utente coinquilino creato");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("data")).thenReturn("2026-01-28");
        when(request.getParameter("idPostazione")).thenReturn("3");
        when(request.getParameter("idFascia")).thenReturn("2");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        System.out.println("[IT-PREN-01] Parametri request impostati");

        try (MockedConstruction<PrenotazioneDAO> mocked =
                     mockConstruction(PrenotazioneDAO.class,
                             (mock, context) -> {
                                 when(mock.verificaConflitto(
                                         any(Date.class),
                                         anyLong(),
                                         anyLong()
                                 )).thenReturn(false);
                             })) {

            System.out.println("[IT-PREN-01] PrenotazioneDAO intercettato");

            controller.service(request, response);
            System.out.println("[IT-PREN-01] Controller eseguito");

            PrenotazioneDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).verificaConflitto(
                    Date.valueOf("2026-01-28"),
                    3L,
                    2L
            );
            System.out.println("[IT-PREN-01] Verifica conflitto OK");

            ArgumentCaptor<Prenotazione> captor =
                    ArgumentCaptor.forClass(Prenotazione.class);

            verify(daoMock).creaPrenotazione(captor.capture());
            System.out.println("[IT-PREN-01] Creazione prenotazione invocata");

            Prenotazione p = captor.getValue();
            assertEquals(Date.valueOf("2026-01-28"), p.getData());
            assertEquals("ATTIVA", p.getStato());
            assertEquals(10L, p.getIdUtente());
            assertEquals(3L, p.getIdPostazione());
            assertEquals(2L, p.getIdFasciaOraria());

            System.out.println("[IT-PREN-01] Dati prenotazione verificati");

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/PrenotazioneController?action=list"
            );
            System.out.println("[IT-PREN-01] Redirect finale OK");
        }

        System.out.println("[IT-PREN-01] Test completato");
    }

    // IT-PREN-02
    @Test
    void conflitto_temporale_bloccaCreazione() throws Exception {

        System.out.println("[IT-PREN-02] Avvio test");

        PrenotazioneController controller = new PrenotazioneController();
        System.out.println("[IT-PREN-02] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-PREN-02] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(10L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-PREN-02] Utente coinquilino creato");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("data")).thenReturn("2026-01-28");
        when(request.getParameter("idPostazione")).thenReturn("3");
        when(request.getParameter("idFascia")).thenReturn("2");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        System.out.println("[IT-PREN-02] Parametri request impostati");

        try (MockedConstruction<PrenotazioneDAO> mocked =
                     mockConstruction(PrenotazioneDAO.class,
                             (mock, context) -> {
                                 when(mock.verificaConflitto(
                                         any(Date.class),
                                         anyLong(),
                                         anyLong()
                                 )).thenReturn(true);
                             })) {

            System.out.println("[IT-PREN-02] PrenotazioneDAO intercettato");

            controller.service(request, response);
            System.out.println("[IT-PREN-02] Controller eseguito");

            PrenotazioneDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).verificaConflitto(
                    Date.valueOf("2026-01-28"),
                    3L,
                    2L
            );
            System.out.println("[IT-PREN-02] Conflitto rilevato correttamente");

            verify(daoMock, never()).creaPrenotazione(any());
            System.out.println("[IT-PREN-02] Creazione prenotazione bloccata");

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/PrenotazioneController?action=new&error=conflitto"
            );
            System.out.println("[IT-PREN-02] Redirect di errore OK");
        }

        System.out.println("[IT-PREN-02] Test completato");
    }

    // IT-PREN-03
    @Test
    void dataNonValida_lanciaServletException() {

        System.out.println("[IT-PREN-03] Avvio test");

        PrenotazioneController controller = new PrenotazioneController();
        System.out.println("[IT-PREN-03] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-PREN-03] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(10L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-PREN-03] Utente coinquilino creato");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("data")).thenReturn("28-01-2026");
        when(request.getParameter("idPostazione")).thenReturn("3");
        when(request.getParameter("idFascia")).thenReturn("2");

        System.out.println("[IT-PREN-03] Data non valida impostata");

        try (MockedConstruction<PrenotazioneDAO> mocked =
                     mockConstruction(PrenotazioneDAO.class)) {

            assertThrows(ServletException.class, () -> {
                controller.service(request, response);
            });
            System.out.println("[IT-PREN-03] ServletException correttamente sollevata");

            PrenotazioneDAO daoMock = mocked.constructed().get(0);
            verifyNoInteractions(daoMock);
            System.out.println("[IT-PREN-03] Nessuna interazione con DAO");
        }

        System.out.println("[IT-PREN-03] Test completato");
    }
}
