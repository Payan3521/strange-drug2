package com.microserviceone.users.verificationCodeApi.infraestructure.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.microserviceone.users.verificationCodeApi.domain.port.out.IEmailService;
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
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Código de Verificación");
            message.setText("Tu código de verificación es: " + code + "\nEste código expirará en 5 minutos.");
            
            mailSender.send(message);
            log.info("Código de verificación enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.error("Error al enviar el código de verificación a: {}. Error: {}", email, e.getMessage());
            throw new RuntimeException("Error al enviar el código de verificación: " + e.getMessage());
        }
    }

}