package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveAdmin;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaveAdminUseCase implements ISaveAdmin{

    private final ValidateUniqueEmailUseCase validateUniqueEmailUseCase;
    private final PasswordEncripterUseCase passwordEncripterUseCase;
    private final IRegisterRepository registerRepository;
    private final LoggingService loggingService;

    @Override
    @Transactional
    public Admin save(Admin admin) {
        try {
            loggingService.logInfo("Iniciando registro de administrador con email: {}", admin.getEmail());
            
            // Validar email único
            loggingService.logDebug("Validando unicidad de email: {}", admin.getEmail());
            validateUniqueEmailUseCase.validate(admin.getEmail());
            loggingService.logDebug("Email validado como único: {}", admin.getEmail());
            
            // Encriptar contraseña
            loggingService.logDebug("Encriptando contraseña para admin: {}", admin.getEmail());
            admin.setPassword(passwordEncripterUseCase.encripter(admin.getPassword(), admin.getEmail()));
            
            // Guardar administrador
            loggingService.logDebug("Guardando administrador en base de datos: {}", admin.getEmail());
            Admin savedAdmin = (Admin) registerRepository.save(admin);
            
            loggingService.logInfo("Administrador registrado exitosamente con ID: {} y email: {}", 
                savedAdmin.getId(), savedAdmin.getEmail());
            
            return savedAdmin;
            
        } catch (Exception e) {
            loggingService.logError("Error al registrar administrador con email: {}", admin.getEmail(), e);
            throw e;
        }
    }

}