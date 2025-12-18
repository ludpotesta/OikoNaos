package it.unisa.oikonaos.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "AutenticazioneController", value = "/AutenticazioneController")

public class AutenticazioneController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        try {

        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = URLEncoder.encode(e.getMessage(), "UTF-8");
        }
    }
}

