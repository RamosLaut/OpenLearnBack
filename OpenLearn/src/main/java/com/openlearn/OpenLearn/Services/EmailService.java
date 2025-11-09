package com.openlearn.OpenLearn.Services; // Ajusta tu paquete

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    public boolean sendPasswordResetEmail(String userEmail, String resetCode) {
        Email from = new Email("lautiramos6@gmail.com", "OpenLearn Support");
        Email to = new Email(userEmail);

        String subject = "Tu código de recuperación de contraseña de OpenLearn";
        Content content = new Content("text/plain",
                "Hola,\n\nHas solicitado restablecer tu contraseña.\n\n" +
                        "Tu código de verificación es: " + resetCode + "\n\n" +
                        "Si no solicitaste esto, puedes ignorar este email.\n" +
                        "- El equipo de OpenLearn"
        );

        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("Email enviado a: " + userEmail + ". Status Code: " + response.getStatusCode());
                return true;
            } else {
                System.err.println("Error al enviar email: " + response.getBody());
                return false;
            }
        } catch (IOException ex) {
            System.err.println("Error de IOException al enviar email: " + ex.getMessage());
            return false;
        }
    }
}