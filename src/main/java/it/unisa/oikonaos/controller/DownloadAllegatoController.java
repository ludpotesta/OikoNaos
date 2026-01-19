package it.unisa.oikonaos.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;

public class DownloadAllegatoController extends HttpServlet {

    private static final String UPLOAD_DIR = "C:/OikoNaos/uploads";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String fileName = request.getParameter("file");

        if (fileName == null || fileName.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        File file = new File(UPLOAD_DIR, fileName);

        if (!file.exists() || file.isDirectory()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setContentLengthLong(file.length());
        response.setHeader(
                "Content-Disposition",
                "inline; filename=\"" + file.getName() + "\""
        );

        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            in.transferTo(out);
        }
    }
}
