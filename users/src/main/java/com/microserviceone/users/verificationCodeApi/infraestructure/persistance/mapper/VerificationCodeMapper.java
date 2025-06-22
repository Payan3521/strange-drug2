package com.microserviceone.users.verificationCodeApi.infraestructure.persistance.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import com.microserviceone.users.verificationCodeApi.domain.model.VerificationCode;
import com.microserviceone.users.verificationCodeApi.infraestructure.persistance.entity.VerificationCodeEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VerificationCodeMapper {
    
    VerificationCodeEntity toEntity(VerificationCode verificationCode);
    
    VerificationCode toDomain(VerificationCodeEntity entity);
}