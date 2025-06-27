package com.microserviceone.users.registrationApi.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.exception.AgeIllegalException;
import com.microserviceone.users.registrationApi.domain.model.Customer;

@ExtendWith(MockitoExtension.class)
public class ValidateAgeUseCaseTest {
    
    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private ValidateAgeUseCase validateAgeUseCase;

    private Customer validCustomer;

    @BeforeEach
    void setUp() {
        validCustomer = new Customer();

        validCustomer.setName("John Doe");
        validCustomer.setLastName("Doe");
        validCustomer.setEmail("john.doe@gmail.com");
        validCustomer.setPhone("3127147814");
        validCustomer.setPassword("Password123!");
        validCustomer.setBirthDate(LocalDate.now().minusYears(20)); // 20 years old
    }

    @Test
    void shouldValidateCustomerWithValidAgeSuccessfully(){
        assertDoesNotThrow(() -> validateAgeUseCase.validate(validCustomer));

        verify(loggingService).logDebug("Validando edad del cliente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logDebug("Edad validada correctamente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verifyNoMoreInteractions(loggingService);
    }

    @Test
    void shouldValidateCustomerWithExactLegalAgeSuccessfully(){
        validCustomer.setBirthDate(LocalDate.now().minusYears(18)); // 18 years old

        assertDoesNotThrow(() -> validateAgeUseCase.validate(validCustomer));

        verify(loggingService).logDebug("Validando edad del cliente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logDebug("Edad validada correctamente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verifyNoMoreInteractions(loggingService);
    }

    @Test
    void shouldThrowAgeIllegalExceptionForUnderageCustomer(){
        validCustomer.setBirthDate(LocalDate.now().minusYears(15)); // 15 years old

        AgeIllegalException exception = assertThrows(AgeIllegalException.class, () -> {
            validateAgeUseCase.validate(validCustomer);
        });

        assertNotNull(exception);
        assertEquals(validCustomer.getAge(), exception.getAge());

        verify(loggingService).logDebug("Validando edad del cliente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logWarning("Cliente rechazado por edad insuficiente: {} años (email: {})", 
                    validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logError("Excepción de edad ilegal para cliente con email: {}", 
        validCustomer.getEmail(), exception);
        verifyNoMoreInteractions(loggingService);    
    }


    @Test
    void shouldThrowAgeIllegalExceptionForCustomerWithFutureBirthDate(){
        validCustomer.setBirthDate(LocalDate.now().plusYears(1)); // Future date

        AgeIllegalException exception = assertThrows(AgeIllegalException.class, () -> {
            validateAgeUseCase.validate(validCustomer);
        });

        assertNotNull(exception);
        assertEquals(validCustomer.getAge(), exception.getAge());
        assertEquals(-1, exception.getAge()); 

        verify(loggingService).logDebug("Validando edad del cliente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logWarning("Cliente rechazado por edad insuficiente: {} años (email: {})", 
                    validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logError("Excepción de edad ilegal para cliente con email: {}", 
        validCustomer.getEmail(), exception);
        verifyNoMoreInteractions(loggingService);
    }

    @Test
    void shouldThrowAgeIllegalExceptionForCustomerWith17Years(){
        validCustomer.setBirthDate(LocalDate.now().minusYears(17)); // 17 years old

        AgeIllegalException exception = assertThrows(AgeIllegalException.class, () -> {
            validateAgeUseCase.validate(validCustomer);
        });

        assertNotNull(exception);
        assertEquals(validCustomer.getAge(), exception.getAge());

        verify(loggingService).logDebug("Validando edad del cliente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logWarning("Cliente rechazado por edad insuficiente: {} años (email: {})", 
                    validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logError("Excepción de edad ilegal para cliente con email: {}", 
        validCustomer.getEmail(), exception);
        verifyNoMoreInteractions(loggingService);
    }

    @Test
    void shouldHandleCustomerWithVeryYoungAge(){
        validCustomer.setBirthDate(LocalDate.now().minusYears(5)); // 5 years old

        AgeIllegalException exception = assertThrows(AgeIllegalException.class, () -> {
            validateAgeUseCase.validate(validCustomer);
        });

        assertNotNull(exception);
        assertEquals(validCustomer.getAge(), exception.getAge());

        verify(loggingService).logDebug("Validando edad del cliente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logWarning("Cliente rechazado por edad insuficiente: {} años (email: {})", 
                    validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logError("Excepción de edad ilegal para cliente con email: {}", 
        validCustomer.getEmail(), exception);
        verifyNoMoreInteractions(loggingService);
    }

    @Test
    void shouldHandleCustomerWithVeryOldAge(){
        validCustomer.setBirthDate(LocalDate.now().minusYears(100)); // 100 years old

        assertDoesNotThrow(() -> validateAgeUseCase.validate(validCustomer));

        verify(loggingService).logDebug("Validando edad del cliente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verify(loggingService).logDebug("Edad validada correctamente: {} años (email: {})", validCustomer.getAge(), validCustomer.getEmail());
        verifyNoMoreInteractions(loggingService);
    }

    @Test
    void shouldHandleUnexpectedExceptionDuringValidation(){
        validCustomer.setBirthDate(null); // Null date to simulate unexpected exception
        
        Exception exception = assertThrows(Exception.class, () -> {
            validateAgeUseCase.validate(validCustomer);
        });

        assertNotNull(exception);

        verify(loggingService).logError("Error inesperado al validar edad para cliente con email: {}", validCustomer.getEmail(), exception);
    }
}