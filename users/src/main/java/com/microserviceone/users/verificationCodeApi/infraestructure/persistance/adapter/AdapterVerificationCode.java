package com.microserviceone.users.verificationCodeApi.infraestructure.persistance.adapter;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.verificationCodeApi.domain.model.VerificationCode;
import com.microserviceone.users.verificationCodeApi.domain.port.out.IVerificationCodeRepository;
import com.microserviceone.users.verificationCodeApi.infraestructure.persistance.entity.VerificationCodeEntity;
import com.microserviceone.users.verificationCodeApi.infraestructure.persistance.mapper.VerificationCodeMapper;
import com.microserviceone.users.verificationCodeApi.infraestructure.persistance.repository.ORMverificationCode;
import com.microserviceone.users.verificationCodeApi.infraestructure.config.VerificationCodeConfig;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdapterVerificationCode implements IVerificationCodeRepository{

    private final ORMverificationCode ormVerificationCode;
    private final VerificationCodeMapper verificationCodeMapper;
    private final VerificationCodeConfig verificationCodeConfig;
    private final LoggingService loggingService;

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        try {
            loggingService.logDebug("AdapterVerificationCode: Guardando código de verificación - Email: {}, Código: {}", 
                verificationCode.getEmail(), verificationCode.getCode());
            
            VerificationCodeEntity entity = verificationCodeMapper.toEntity(verificationCode);
            loggingService.logDebug("AdapterVerificationCode: Código convertido a entidad - Email: {}, Código: {}", 
                verificationCode.getEmail(), verificationCode.getCode());
            
            VerificationCodeEntity savedEntity = ormVerificationCode.save(entity);
            loggingService.logDebug("AdapterVerificationCode: Código guardado en base de datos - ID: {}, Email: {}, Código: {}", 
                savedEntity.getId(), savedEntity.getEmail(), savedEntity.getCode());
            
            VerificationCode savedVerificationCode = verificationCodeMapper.toDomain(savedEntity);
            loggingService.logDebug("AdapterVerificationCode: Entidad convertida a dominio - ID: {}, Email: {}, Código: {}", 
                savedVerificationCode.getId(), savedVerificationCode.getEmail(), savedVerificationCode.getCode());
            
            return savedVerificationCode;
            
        } catch (Exception e) {
            loggingService.logError("AdapterVerificationCode: Error al guardar código de verificación - Email: {}, Código: {}", 
                verificationCode.getEmail(), verificationCode.getCode(), e);
            throw e;
        }
    }

    @Override
    public Optional<VerificationCode> findByEmailAndCode(String email, String code) {
        try {
            loggingService.logDebug("AdapterVerificationCode: Buscando código por email y código - Email: {}, Código: {}", email, code);
            
            Optional<VerificationCode> verificationCode = ormVerificationCode.findByEmailAndCode(email, code)
                .map(verificationCodeMapper::toDomain);
            
            if (verificationCode.isPresent()) {
                loggingService.logDebug("AdapterVerificationCode: Código encontrado - ID: {}, Email: {}, Código: {}", 
                    verificationCode.get().getId(), email, code);
            } else {
                loggingService.logDebug("AdapterVerificationCode: Código no encontrado - Email: {}, Código: {}", email, code);
            }
            
            return verificationCode;
            
        } catch (Exception e) {
            loggingService.logError("AdapterVerificationCode: Error al buscar código - Email: {}, Código: {}", email, code, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteExpiredCodes() {
        try {
            loggingService.logInfo("AdapterVerificationCode: Iniciando eliminación de códigos expirados");
            
            LocalDateTime expiredTime = LocalDateTime.now()
                .minusMinutes(verificationCodeConfig.getCodeExpirationMinutes());
            
            loggingService.logDebug("AdapterVerificationCode: Tiempo de expiración calculado: {} ({} minutos)", 
                expiredTime, verificationCodeConfig.getCodeExpirationMinutes());
            
            ormVerificationCode.deleteByGeneratedAtBefore(expiredTime);
            
            loggingService.logInfo("AdapterVerificationCode: Códigos expirados eliminados exitosamente");
            
        } catch (Exception e) {
            loggingService.logError("AdapterVerificationCode: Error al eliminar códigos expirados", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void invalidatePreviousCodes(String email) {
        try {
            loggingService.logDebug("AdapterVerificationCode: Invalidando códigos anteriores - Email: {}", email);
            
            ormVerificationCode.invalidatePreviousCodes(email);
            
            loggingService.logDebug("AdapterVerificationCode: Códigos anteriores invalidados exitosamente - Email: {}", email);
            
        } catch (Exception e) {
            loggingService.logError("AdapterVerificationCode: Error al invalidar códigos anteriores - Email: {}", email, e);
            throw e;
        }
    }

    @Override
    public boolean isEmailVerified(String email) {
        try {
            loggingService.logDebug("AdapterVerificationCode: Verificando si email está verificado - Email: {}", email);
            
            boolean isVerified = ormVerificationCode.existsByEmailAndVerifiedTrue(email);
            
            loggingService.logDebug("AdapterVerificationCode: Estado de verificación - Email: {}, Verificado: {}", 
                email, isVerified);
            
            return isVerified;
            
        } catch (Exception e) {
            loggingService.logError("AdapterVerificationCode: Error al verificar estado de email - Email: {}", email, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void markCodeAsVerified(String email, String code) {
        try {
            loggingService.logDebug("AdapterVerificationCode: Marcando código como verificado - Email: {}, Código: {}", email, code);
            
            ormVerificationCode.markAsVerified(email, code);
            
            loggingService.logDebug("AdapterVerificationCode: Código marcado como verificado exitosamente - Email: {}, Código: {}", email, code);
            
        } catch (Exception e) {
            loggingService.logError("AdapterVerificationCode: Error al marcar código como verificado - Email: {}, Código: {}", email, code, e);
            throw e;
        }
    }
}