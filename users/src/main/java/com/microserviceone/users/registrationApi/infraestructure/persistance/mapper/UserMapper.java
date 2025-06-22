package com.microserviceone.users.registrationApi.infraestructure.persistance.mapper;

import org.springframework.stereotype.Component;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.AdminEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.CustomerEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.UserEntity;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
    
    private final LoggingService loggingService;
    
    public UserEntity toEntity(User user){
        try {
            loggingService.logDebug("UserMapper: Convirtiendo dominio a entidad - Email: {}, Tipo: {}", 
                user.getEmail(), user.getClass().getSimpleName());
            
            if(user instanceof Customer){
                CustomerEntity entity = new CustomerEntity();
                mapCommonFields(user, entity);
                entity.setBirthDate(((Customer) user).getBirthDate());
                
                loggingService.logDebug("UserMapper: Customer convertido a CustomerEntity - Email: {}, Fecha nacimiento: {}", 
                    user.getEmail(), ((Customer) user).getBirthDate());
                return entity;
                
            }else if(user instanceof Admin){
                AdminEntity entity = new AdminEntity();
                mapCommonFields(user, entity);
                entity.setArea(((Admin) user).getArea());
                
                loggingService.logDebug("UserMapper: Admin convertido a AdminEntity - Email: {}, Área: {}", 
                    user.getEmail(), ((Admin) user).getArea());
                return entity;
            }
            
            loggingService.logError("UserMapper: Tipo de usuario desconocido - Email: {}, Tipo: {}", 
                user.getEmail(), user.getClass().getName());
            throw new IllegalArgumentException("Unknown user type");
            
        } catch (Exception e) {
            loggingService.logError("UserMapper: Error al convertir dominio a entidad - Email: {}", user.getEmail(), e);
            throw e;
        }
    }

    public User toDomain(UserEntity entity) {
        try {
            loggingService.logDebug("UserMapper: Convirtiendo entidad a dominio - ID: {}, Tipo: {}", 
                entity.getId(), entity.getClass().getSimpleName());
            
            if (entity instanceof CustomerEntity) {
                CustomerEntity customerEntity = (CustomerEntity) entity;
                Customer customer = new Customer(
                        customerEntity.getId(),
                        customerEntity.getName(),
                        customerEntity.getLastName(),
                        customerEntity.getEmail(),
                        customerEntity.getPassword(),
                        customerEntity.getPhone(),
                        customerEntity.getBirthDate()
                );
                
                loggingService.logDebug("UserMapper: CustomerEntity convertido a Customer - ID: {}, Email: {}, Fecha nacimiento: {}", 
                    customer.getId(), customer.getEmail(), customer.getBirthDate());
                return customer;
                
            } else if (entity instanceof AdminEntity) {
                AdminEntity adminEntity = (AdminEntity) entity;
                Admin admin = new Admin(
                        adminEntity.getId(),
                        adminEntity.getName(),
                        adminEntity.getLastName(),
                        adminEntity.getEmail(),
                        adminEntity.getPassword(),
                        adminEntity.getPhone(),
                        adminEntity.getArea()
                );
                
                loggingService.logDebug("UserMapper: AdminEntity convertido a Admin - ID: {}, Email: {}, Área: {}", 
                    admin.getId(), admin.getEmail(), admin.getArea());
                return admin;
            }
            
            loggingService.logError("UserMapper: Tipo de entidad desconocido - ID: {}, Tipo: {}", 
                entity.getId(), entity.getClass().getName());
            throw new IllegalArgumentException("Unknown entity type: " + entity.getClass().getName());
            
        } catch (Exception e) {
            loggingService.logError("UserMapper: Error al convertir entidad a dominio - ID: {}", entity.getId(), e);
            throw e;
        }
    }

    private void mapCommonFields(User domain, UserEntity entity) {
        try {
            loggingService.logDebug("UserMapper: Mapeando campos comunes - Email: {}", domain.getEmail());
            
            entity.setId(domain.getId());
            entity.setName(domain.getName());
            entity.setLastName(domain.getLastName());
            entity.setEmail(domain.getEmail());
            entity.setPassword(domain.getPassword());
            entity.setPhone(domain.getPhone());
            entity.setRol(domain.getRol().name());
            
            loggingService.logDebug("UserMapper: Campos comunes mapeados exitosamente - Email: {}, Rol: {}", 
                domain.getEmail(), domain.getRol().name());
            
        } catch (Exception e) {
            loggingService.logError("UserMapper: Error al mapear campos comunes - Email: {}", domain.getEmail(), e);
            throw e;
        }
    }

}