package it.unisa.oikonaos.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "RegistrazioneController", value = "/RegistrazioneController")

public class RegistrazioneController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        String password = request.getParameter("password");
        String codice = request.getParameter("codiceID");
        try {


        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = URLEncoder.encode(e.getMessage(), "UTF-8");
        }
    }
}