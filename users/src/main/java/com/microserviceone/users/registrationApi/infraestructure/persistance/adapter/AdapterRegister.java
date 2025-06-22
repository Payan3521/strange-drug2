package com.microserviceone.users.registrationApi.infraestructure.persistance.adapter;

import java.util.Optional;
import org.springframework.stereotype.Component;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.UserEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.mapper.UserMapper;
import com.microserviceone.users.registrationApi.infraestructure.persistance.repository.ORMregister;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdapterRegister implements IRegisterRepository{

    private final ORMregister ormRegister;
    private final UserMapper userMapper;
    private final LoggingService loggingService;

    @Override
    public User save(User user) {
        try {
            loggingService.logDebug("AdapterRegister: Guardando usuario en base de datos - Email: {}, Tipo: {}", 
                user.getEmail(), user.getClass().getSimpleName());
            
            UserEntity userEntity = userMapper.toEntity(user);
            loggingService.logDebug("AdapterRegister: Usuario convertido a entidad - Email: {}", user.getEmail());
            
            UserEntity savedUserEntity = ormRegister.save(userEntity);
            loggingService.logDebug("AdapterRegister: Usuario guardado en base de datos - ID: {}, Email: {}", 
                savedUserEntity.getId(), savedUserEntity.getEmail());
            
            User savedUser = userMapper.toDomain(savedUserEntity);
            loggingService.logDebug("AdapterRegister: Usuario convertido de entidad a dominio - ID: {}, Email: {}", 
                savedUser.getId(), savedUser.getEmail());
            
            return savedUser;
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al guardar usuario - Email: {}", user.getEmail(), e);
            throw e;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            loggingService.logDebug("AdapterRegister: Buscando usuario por email: {}", email);
            
            Optional<User> user = ormRegister.findByEmail(email)
                    .map(userMapper::toDomain);
            
            if (user.isPresent()) {
                loggingService.logDebug("AdapterRegister: Usuario encontrado - ID: {}, Email: {}", 
                    user.get().getId(), user.get().getEmail());
            } else {
                loggingService.logDebug("AdapterRegister: Usuario no encontrado - Email: {}", email);
            }
            
            return user;
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al buscar usuario por email: {}", email, e);
            throw e;
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            loggingService.logDebug("AdapterRegister: Verificando existencia de usuario por email: {}", email);
            
            boolean exists = ormRegister.existsByEmail(email);
            
            loggingService.logDebug("AdapterRegister: Usuario {} existe - Email: {}", 
                exists ? "SÍ" : "NO", email);
            
            return exists;
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al verificar existencia de usuario - Email: {}", email, e);
            throw e;
        }
    }
    
}