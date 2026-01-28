package it.unisa.oikonaos.integration;

import it.unisa.oikonaos.controller.PrenotazioneController;
import it.unisa.oikonaos.dao.PrenotazioneDAO;
import it.unisa.oikonaos.model.Prenotazione;
import it.unisa.oikonaos.model.Utente;
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

        // === Controller reale (INTEGRAZIONE) ===
        PrenotazioneController controller = new PrenotazioneController();

        // === Ambiente simulato ===
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        // === Utente in sessione ===
        Utente u = new Utente();
        u.setIdUtente(10L);
        u.setRuolo("COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        // === Parametri request ===
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("data")).thenReturn("2026-01-28");
        when(request.getParameter("idPostazione")).thenReturn("3");
        when(request.getParameter("idFascia")).thenReturn("2");

        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        // === Intercettiamo new PrenotazioneDAO() ===
        try (MockedConstruction<PrenotazioneDAO> mocked =
                     mockConstruction(PrenotazioneDAO.class,
                             (mock, context) -> {
                                 when(mock.verificaConflitto(
                                         any(Date.class),
                                         anyLong(),
                                         anyLong()
                                 )).thenReturn(false);
                             })) {

            // === Simuliamo il container ===
            controller.service(request, response);

            PrenotazioneDAO daoMock = mocked.constructed().get(0);

            // === Verifica integrazione DAO ===
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

            // === Redirect corretto ===
            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/PrenotazioneController?action=list"
            );
        }
    }
}
