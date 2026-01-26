package it.unisa.oikonaos.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    public static boolean inviaEmail(String to, String oggetto, String testo) {
        // SECURITY WARNING: Hardcoded credentials are for demonstration only.
        // In production, ensure EMAIL_USER and EMAIL_PASSWORD environment variables are set.
        String envUser = System.getenv("EMAIL_USER");
        String envPass = System.getenv("EMAIL_PASSWORD");

        final String mittente = (envUser != null) ? envUser : "beenaturalpw@gmail.com";
        final String password = (envPass != null) ? envPass : "yurk xuat crfq bfga";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mittente, password);
                }
            });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mittente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(oggetto);
            message.setText(testo);

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
