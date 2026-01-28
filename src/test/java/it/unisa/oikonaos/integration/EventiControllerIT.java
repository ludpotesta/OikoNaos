package it.unisa.oikonaos.integration;

import it.unisa.oikonaos.controller.BachecaEventiController;
import it.unisa.oikonaos.controller.ConfermaIscrizioneEventoController;
import it.unisa.oikonaos.controller.DisiscrizioneEventoController;
import it.unisa.oikonaos.controller.SupervisoreEventiController;
import it.unisa.oikonaos.dao.EventoDAO;
import it.unisa.oikonaos.dto.EventoBachecaDTO;
import it.unisa.oikonaos.model.Evento;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventiControllerIT {

    // IT-EVE-01
    @Test
    void creazioneEventoSupervisore() throws Exception {

        System.out.println("[IT-EVE-01] Avvio test");

        SupervisoreEventiController controller =
                new SupervisoreEventiController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente sup = new Utente();
        sup.setIdUtente(99L);
        sup.setRuolo("SUPERVISORE");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(sup);

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("titolo")).thenReturn("Evento IT");
        when(request.getParameter("descrizione")).thenReturn("Descrizione evento");
        when(request.getParameter("luogo")).thenReturn("Sala Comune");
        when(request.getParameter("posti")).thenReturn("10");

        LocalDateTime inizio = LocalDateTime.now().plusDays(3);
        LocalDateTime fine = inizio.plusHours(2);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        when(request.getParameter("dataInizio"))
                .thenReturn(inizio.format(formatter));
        when(request.getParameter("dataFine"))
                .thenReturn(fine.format(formatter));

        try (MockedConstruction<EventoDAO> mocked =
                     mockConstruction(EventoDAO.class,
                             (mock, ctx) ->
                                     doNothing().when(mock)
                                             .creaEvento(any(Evento.class)))) {

            controller.service(request, response);

            EventoDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).creaEvento(any(Evento.class));
            System.out.println("[IT-EVE-01] Verifica creaEvento OK");

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/SupervisoreEventiController"
            );
            System.out.println("[IT-EVE-01] Verifica redirect OK");
        }

        System.out.println("[IT-EVE-01] Test completato");
    }

    // IT-EVE-02
    @Test
    void iscrizioneEventoFuturo() throws Exception {

        System.out.println("[IT-EVE-02] Avvio test");

        ConfermaIscrizioneEventoController controller =
                new ConfermaIscrizioneEventoController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(30L);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        // parametri REALI
        when(request.getParameter("idEvento")).thenReturn("10");
        when(request.getParameter("action")).thenReturn("conferma");

        try (MockedConstruction<EventoDAO> mocked =
                     mockConstruction(EventoDAO.class,
                             (mock, ctx) ->
                                     doNothing().when(mock)
                                             .iscriviUtenteEvento(30L, 10L))) {

            controller.service(request, response);

            EventoDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).iscriviUtenteEvento(30L, 10L);
            System.out.println("[IT-EVE-02] Verifica iscriviUtenteEvento OK");

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/BachecaEventiController"
            );
            System.out.println("[IT-EVE-02] Verifica redirect OK");
        }

        System.out.println("[IT-EVE-02] Test completato");
    }

    // IT-EVE-03
    @Test
    void iscrizioneDuplicata() throws Exception {

        System.out.println("[IT-EVE-03] Avvio test");

        ConfermaIscrizioneEventoController controller =
                new ConfermaIscrizioneEventoController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(30L);
        u.setRuolo("COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("idEvento")).thenReturn("10");

        try (MockedConstruction<EventoDAO> mocked =
                     mockConstruction(EventoDAO.class,
                             (mock, ctx) ->
                                     doThrow(new IllegalStateException())
                                             .when(mock)
                                             .iscriviUtenteEvento(30L, 10L))) {

            controller.service(request, response);

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/BachecaEventiController?error=duplicata"
            );
        }

        System.out.println("[IT-EVE-03] Test completato");
    }

    // IT-EVE-04
    @Test
    void disiscrizioneEvento() throws Exception {

        System.out.println("[IT-EVE-04] Avvio test");

        DisiscrizioneEventoController controller =
                new DisiscrizioneEventoController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(30L);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("idEvento")).thenReturn("10");

        try (MockedConstruction<EventoDAO> mocked =
                     mockConstruction(EventoDAO.class,
                             (mock, ctx) ->
                                     doNothing().when(mock)
                                             .disiscriviUtenteDaEvento(10L, 30L))) {

            controller.service(request, response);

            EventoDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).disiscriviUtenteDaEvento(10L, 30L);
            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/BachecaEventiController"
            );
        }

        System.out.println("[IT-EVE-04] Test completato");
    }


    @Test
    void eventoPostiEsauriti_nonMostraIscriviti() throws Exception {

        System.out.println("[IT-EVE-05] Avvio test");

        BachecaEventiController controller = new BachecaEventiController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(30L);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        // DTO evento con posti esauriti
        EventoBachecaDTO evento = new EventoBachecaDTO();
        evento.setIdEvento(10L);
        evento.setTitolo("Evento pieno");
        evento.setPostiDisponibili(0);
        evento.setIscritto(false);

        try (MockedConstruction<EventoDAO> mocked =
                     mockConstruction(EventoDAO.class,
                             (mock, ctx) ->
                                     when(mock.getEventiBacheca(30L))
                                             .thenReturn(List.of(evento)))) {

            var dispatcher = mock(jakarta.servlet.RequestDispatcher.class);
            when(request.getRequestDispatcher(anyString()))
                    .thenReturn(dispatcher);

            controller.service(request, response);


            verify(dispatcher).forward(request, response);
            verify(response, never()).sendRedirect(anyString());
        }

        System.out.println("[IT-EVE-05] Test completato");
    }


    @Test
    void eventoConcluso_nonPermetteIscrizione() throws Exception {

        System.out.println("[IT-EVE-06] Avvio test");

        BachecaEventiController controller = new BachecaEventiController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(30L);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        EventoBachecaDTO evento = new EventoBachecaDTO();
        evento.setIdEvento(11L);
        evento.setTitolo("Evento passato");
        evento.setPostiDisponibili(5);
        evento.setIscritto(false);
        evento.setDataInizio(LocalDateTime.now().minusDays(5));
        evento.setDataFine(LocalDateTime.now().minusDays(3));

        try (MockedConstruction<EventoDAO> mocked =
                     mockConstruction(EventoDAO.class,
                             (mock, ctx) ->
                                     when(mock.getEventiBacheca(30L))
                                             .thenReturn(List.of(evento)))) {

            var dispatcher = mock(jakarta.servlet.RequestDispatcher.class);
            when(request.getRequestDispatcher(anyString()))
                    .thenReturn(dispatcher);

            controller.service(request, response);

            verify(dispatcher).forward(request, response);
            verify(response, never()).sendRedirect(anyString());
        }

        System.out.println("[IT-EVE-06] Test completato");
    }


    @Test
    void eliminazioneEventoSupervisore() throws Exception {

        System.out.println("[IT-EVE-07] Avvio test");

        SupervisoreEventiController controller =
                new SupervisoreEventiController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente supervisore = new Utente();
        supervisore.setRuolo("SUPERVISORE");

        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(supervisore);

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("idEvento")).thenReturn("10");
        when(request.getContextPath()).thenReturn("");

        try (MockedConstruction<EventoDAO> mocked =
                     mockConstruction(EventoDAO.class)) {

            controller.service(request, response);

            EventoDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).eliminaEvento(10L);
            verify(response).sendRedirect(
                    "/SupervisoreEventiController"
            );
        }

        System.out.println("[IT-EVE-07] Test completato");
    }
}
