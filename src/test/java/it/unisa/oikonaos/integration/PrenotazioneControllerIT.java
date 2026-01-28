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

class PrenotazioneControllerIT {

    //IT_PREN_01
    @Test
    void creazionePrenotazioneValida() throws Exception {
        PrenotazioneController controller = new PrenotazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(10L);
        u.setRuolo("COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("data")).thenReturn("2026-01-28");
        when(request.getParameter("idPostazione")).thenReturn("3");
        when(request.getParameter("idFascia")).thenReturn("2");

        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        try (MockedConstruction<PrenotazioneDAO> mocked =
                     mockConstruction(PrenotazioneDAO.class,
                             (mock, context) -> {
                                 when(mock.verificaConflitto(
                                         any(Date.class),
                                         anyLong(),
                                         anyLong()
                                 )).thenReturn(false);
                             })) {

            controller.service(request, response);

            PrenotazioneDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).verificaConflitto(
                    Date.valueOf("2026-01-28"),
                    3L,
                    2L
            );

            ArgumentCaptor<Prenotazione> captor =
                    ArgumentCaptor.forClass(Prenotazione.class);

            verify(daoMock).creaPrenotazione(captor.capture());

            Prenotazione p = captor.getValue();
            assertEquals(Date.valueOf("2026-01-28"), p.getData());
            assertEquals("ATTIVA", p.getStato());
            assertEquals(10L, p.getIdUtente());
            assertEquals(3L, p.getIdPostazione());
            assertEquals(2L, p.getIdFasciaOraria());
            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/PrenotazioneController?action=list"
            );
        }
    }

    //IT_PREN_02
    @Test
    void conflitto_temporale_bloccaCreazione() throws Exception {

        PrenotazioneController controller = new PrenotazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(10L);
        u.setRuolo("COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("data")).thenReturn("2026-01-28");
        when(request.getParameter("idPostazione")).thenReturn("3");
        when(request.getParameter("idFascia")).thenReturn("2");

        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        try (MockedConstruction<PrenotazioneDAO> mocked = mockConstruction(PrenotazioneDAO.class,
                                 (mock, context) -> {
                                     when(mock.verificaConflitto(
                                             any(Date.class),
                                             anyLong(),
                                             anyLong()
                                     )).thenReturn(true);
                                 })) {

            controller.service(request, response);

            PrenotazioneDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).verificaConflitto(Date.valueOf("2026-01-28"), 3L, 2L);
            verify(daoMock, never()).creaPrenotazione(any());

            verify(response).sendRedirect("/OikoNaos_war_exploded/PrenotazioneController?action=new&error=conflitto");
        }
    }

    // IT_PREN_03
    @Test
    void dataNonValida_lanciaServletException() {
        PrenotazioneController controller = new PrenotazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(10L);
        u.setRuolo("COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("data")).thenReturn("28-01-2026");
        when(request.getParameter("idPostazione")).thenReturn("3");
        when(request.getParameter("idFascia")).thenReturn("2");

        try (MockedConstruction<PrenotazioneDAO> mocked =
                     mockConstruction(PrenotazioneDAO.class)) {

            assertThrows(ServletException.class, () -> {
                controller.service(request, response);
            });

            PrenotazioneDAO daoMock = mocked.constructed().get(0);
            verifyNoInteractions(daoMock);
        }
    }
}
