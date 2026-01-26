package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    public static boolean inviaEmail(String to, String oggetto, String testo) {
        String envUser = System.getenv("oikonaos@gmail.com");
        String envPass = System.getenv("llro ouyg bgsx myvd");

        final String mittente = (envUser != null) ? envUser : "oikonaos@gmail.com";
        final String password = (envPass != null) ? envPass : "llro ouyg bgsx myvd";

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
