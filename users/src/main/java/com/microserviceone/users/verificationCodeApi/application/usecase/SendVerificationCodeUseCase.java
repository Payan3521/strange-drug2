package com.microserviceone.users.verificationCodeApi.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.verificationCodeApi.domain.model.VerificationCode;
import com.microserviceone.users.verificationCodeApi.domain.port.in.ISendVerificationCode;
import com.microserviceone.users.verificationCodeApi.domain.port.out.IEmailService;
import com.microserviceone.users.verificationCodeApi.domain.port.out.IVerificationCodeRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SendVerificationCodeUseCase implements ISendVerificationCode{

    private final CodeGeneratorService codeGeneratorService;
    private final IVerificationCodeRepository verificationCodeRepository;
    private final IEmailService emailService;
    private final LoggingService loggingService;

    @Override
    @Transactional
    public void sendCode(String email) {
        try {
            loggingService.logInfo("SendVerificationCodeUseCase: Iniciando envío de código de verificación - Email: {}", email);
            
            // Invalidar códigos anteriores
            loggingService.logDebug("SendVerificationCodeUseCase: Invalidando códigos anteriores - Email: {}", email);
            verificationCodeRepository.invalidatePreviousCodes(email);
            loggingService.logDebug("SendVerificationCodeUseCase: Códigos anteriores invalidados - Email: {}", email);
            
            // Generar y guardar nuevo código
            loggingService.logDebug("SendVerificationCodeUseCase: Generando nuevo código - Email: {}", email);
            String code = codeGeneratorService.generateCode();
            loggingService.logDebug("SendVerificationCodeUseCase: Código generado: {} para email: {}", code, email);
            
            VerificationCode verificationCode = new VerificationCode(email, code);
            loggingService.logDebug("SendVerificationCodeUseCase: Objeto VerificationCode creado - Email: {}, Código: {}", email, code);
            
            verificationCodeRepository.save(verificationCode);
            loggingService.logDebug("SendVerificationCodeUseCase: Código guardado en base de datos - Email: {}, Código: {}", email, code);

            // Enviar el nuevo código por email
            loggingService.logDebug("SendVerificationCodeUseCase: Enviando código por email - Email: {}, Código: {}", email, code);
            emailService.sendVerificationCode(email, code);
            
            loggingService.logInfo("SendVerificationCodeUseCase: Código de verificación enviado exitosamente - Email: {}, Código: {}", email, code);
            
        } catch (Exception e) {
            loggingService.logError("SendVerificationCodeUseCase: Error al enviar código de verificación - Email: {}", email, e);
            throw e;
        }
    }
    
}