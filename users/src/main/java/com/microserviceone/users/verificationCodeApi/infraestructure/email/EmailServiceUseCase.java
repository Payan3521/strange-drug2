package com.microserviceone.users.verificationCodeApi.infraestructure.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.microserviceone.users.verificationCodeApi.domain.port.out.IEmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceUseCase implements IEmailService{
    
    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationCode(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Código de Verificación");

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>"
                    + "<h2 style='color: #2c3e50;'>Verificación de Cuenta</h2>"
                    + "<p>Hola,</p>"
                    + "<p>Utiliza el siguiente código para completar tu verificación:</p>"
                    + "<div style='font-size: 26px; font-weight: bold; color: #007bff; text-align: center; margin: 20px 0;'>"
                    + code
                    + "</div>"
                    + "<p>Este código expirará en <strong>5 minutos</strong>.</p>"
                    + "<p>Si no solicitaste este código, puedes ignorar este mensaje.</p>"
                    + "<p style='margin-top: 30px;'>Atentamente,<br><strong>Equipo de Soporte</strong><br>STRANGE-DRUG</p>"
                    + "</div>";

            helper.setText(htmlContent, true); 

            mailSender.send(message);
            log.info("Código de verificación enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.error("Error al enviar el código de verificación a: {}. Error: {}", email, e.getMessage());
            throw new RuntimeException("Error al enviar el código de verificación: " + e.getMessage());
        }
    }

}