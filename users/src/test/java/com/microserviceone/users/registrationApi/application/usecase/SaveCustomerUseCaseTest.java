package com.microserviceone.users.registrationApi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.exception.UserAlreadyRegisteredException;
import com.microserviceone.users.registrationApi.domain.exception.AgeIllegalException;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;

@ExtendWith(MockitoExtension.class)
public class SaveCustomerUseCaseTest {
    
    @Mock
    private ValidateUniqueEmailUseCase validateUniqueEmailUseCase;

    @Mock
    private PasswordEncripterUseCase passwordEncripterUseCase;

    @Mock
    private IRegisterRepository registerRepository; //aca si se puede porque esto si se simula

    @Mock
    private LoggingService loggingService;

    @Mock
    private ValidateAgeUseCase validateAgeUseCase;

    @InjectMocks
    private SaveCustomerUseCase saveCustomerUseCase;

    private Customer validCustomer;
    private Customer savedCustomer;

    @BeforeEach
    void setUp(){
        validCustomer = new Customer();
        validCustomer.setName("John Doe");
        validCustomer.setLastName("Doe");
        validCustomer.setEmail("john.doe@gmail.com");
        validCustomer.setPassword("Password123!");
        validCustomer.setPhone("3127147814");
        validCustomer.setBirthDate(LocalDate.now().minusYears(25));

        savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName(validCustomer.getName());
        savedCustomer.setLastName(validCustomer.getLastName());
        savedCustomer.setEmail(validCustomer.getEmail());
        savedCustomer.setPhone(validCustomer.getPhone());
        savedCustomer.setBirthDate(validCustomer.getBirthDate());
        savedCustomer.setPassword("hashedPassword"); // Simulated encrypted password
        savedCustomer.setVerifiedCode(false);
        savedCustomer.setVerifiedTerm(false);
    }

    @Test
    void shouldSaveCustomerSuccessfully() {
        String passwordActual = validCustomer.getPassword();

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(validCustomer.getPassword(), validCustomer.getEmail())).thenReturn(savedCustomer.getPassword());
        when(registerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = saveCustomerUseCase.save(validCustomer);

        assertNotNull(result);
        assertEquals(validCustomer.getName(), result.getName());
        assertEquals(1L, result.getId());
        assertEquals(validCustomer.getEmail(), result.getEmail());
        assertEquals(validCustomer.getAge(), result.getAge());
        assertEquals(validCustomer.getBirthDate(), result.getBirthDate());
        assertEquals(validCustomer.getLastName(), result.getLastName());
        assertEquals(savedCustomer.getPassword(), result.getPassword());
        assertEquals(validCustomer.getPhone(), result.getPhone());
        assertEquals(savedCustomer.getId(), result.getId());
        assertNotEquals(passwordActual, result.getPassword());
        

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verify(validateAgeUseCase).validate(validCustomer);
        verify(passwordEncripterUseCase).encripter(passwordActual, validCustomer.getEmail());
        verify(registerRepository).save(any(Customer.class));

        verify(loggingService).logInfo("Iniciando registro de cliente con email: {} y edad: {}", 
                validCustomer.getEmail(), validCustomer.getAge());

        verify(loggingService).logDebug("Validando unicidad de email: {}", validCustomer.getEmail());
        verify(loggingService).logDebug("Email validado como único: {}", validCustomer.getEmail());

        verify(loggingService).logDebug("Validando edad del cliente: {} años", validCustomer.getAge());
        verify(loggingService).logDebug("Edad validada correctamente: {} años", validCustomer.getAge());

        verify(loggingService).logDebug("Encriptando contraseña para cliente: {}", validCustomer.getEmail());

        verify(loggingService).logDebug("Guardando cliente en base de datos: {}", validCustomer.getEmail());

        verify(loggingService).logInfo("Cliente registrado exitosamente con ID: {}, email: {} y edad: {}", 
                savedCustomer.getId(), savedCustomer.getEmail(), savedCustomer.getAge());
    }

    @Test
    void shouldThrowAgeIllegalExceptionWhenCustomerIsUnderage(){
        validCustomer.setBirthDate(LocalDate.now().minusYears(16));

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());

        AgeIllegalException ageIllegalException = new AgeIllegalException(validCustomer.getAge());

        doThrow(ageIllegalException).when(validateAgeUseCase).validate(validCustomer);

        AgeIllegalException thrownException = assertThrows(AgeIllegalException.class, () -> {
            saveCustomerUseCase.save(validCustomer);
        });

        assertEquals(validCustomer.getAge(), thrownException.getAge());

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verify(validateAgeUseCase).validate(validCustomer);
        verifyNoInteractions(passwordEncripterUseCase, registerRepository);

        verify(loggingService).logError("Error al registrar cliente con email: {}", validCustomer.getEmail(), ageIllegalException);

    }

    @Test
    void shouldHandleCustomerWithMinimumValidAge(){

        String passwordActual = validCustomer.getPassword();

        validCustomer.setBirthDate(LocalDate.now().minusYears(18));
        savedCustomer.setBirthDate(validCustomer.getBirthDate());

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(validCustomer.getPassword(), validCustomer.getEmail())).thenReturn(savedCustomer.getPassword());
        when(registerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = saveCustomerUseCase.save(validCustomer);

        assertNotNull(result);
        assertEquals(validCustomer.getAge(), result.getAge());
        assertEquals(1l, result.getId());

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verify(validateAgeUseCase).validate(validCustomer);
        verify(passwordEncripterUseCase).encripter(passwordActual, validCustomer.getEmail());
        verify(registerRepository).save(any(Customer.class));

        verify(loggingService).logInfo("Iniciando registro de cliente con email: {} y edad: {}", 
        validCustomer.getEmail(), validCustomer.getAge());

        verify(loggingService).logDebug("Validando unicidad de email: {}", validCustomer.getEmail());
        verify(loggingService).logDebug("Email validado como único: {}", validCustomer.getEmail());

        verify(loggingService).logDebug("Validando edad del cliente: {} años", validCustomer.getAge());
        verify(loggingService).logDebug("Edad validada correctamente: {} años", validCustomer.getAge());

        verify(loggingService).logDebug("Encriptando contraseña para cliente: {}", validCustomer.getEmail());

        verify(loggingService).logDebug("Guardando cliente en base de datos: {}", validCustomer.getEmail());

        verify(loggingService).logInfo("Cliente registrado exitosamente con ID: {}, email: {} y edad: {}", 
                savedCustomer.getId(), savedCustomer.getEmail(), savedCustomer.getAge());

    }

    @Test
    void shouldHandleCustomerWithVeryOldAge(){

        String passwordActual = validCustomer.getPassword();

        validCustomer.setBirthDate(LocalDate.now().minusYears(120));
        savedCustomer.setBirthDate(validCustomer.getBirthDate());

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(validCustomer.getPassword(), validCustomer.getEmail())).thenReturn(savedCustomer.getPassword());
        when(registerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = saveCustomerUseCase.save(validCustomer);

        assertNotNull(result);
        assertEquals(validCustomer.getAge(), result.getAge());
        assertEquals(1L, result.getId());

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verify(validateAgeUseCase).validate(validCustomer);
        verify(passwordEncripterUseCase).encripter(passwordActual, validCustomer.getEmail());
        verify(registerRepository).save(any(Customer.class));

        verify(loggingService).logInfo("Iniciando registro de cliente con email: {} y edad: {}", 
                validCustomer.getEmail(), validCustomer.getAge());

        verify(loggingService).logDebug("Validando unicidad de email: {}", validCustomer.getEmail());
        verify(loggingService).logDebug("Email validado como único: {}", validCustomer.getEmail());

        verify(loggingService).logDebug("Validando edad del cliente: {} años", validCustomer.getAge());
        verify(loggingService).logDebug("Edad validada correctamente: {} años", validCustomer.getAge());

        verify(loggingService).logDebug("Encriptando contraseña para cliente: {}", validCustomer.getEmail());

        verify(loggingService).logDebug("Guardando cliente en base de datos: {}", validCustomer.getEmail());

        verify(loggingService).logInfo("Cliente registrado exitosamente con ID: {}, email: {} y edad: {}", 
                savedCustomer.getId(), savedCustomer.getEmail(), savedCustomer.getAge());
    }

    @Test
    void shouldThrowUserAlreadyRegisteredExceptionWhenEmailAlreadyExists() {
        UserAlreadyRegisteredException exception = new UserAlreadyRegisteredException(validCustomer.getEmail());
        doThrow(exception).when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());

        UserAlreadyRegisteredException thrownException = assertThrows(UserAlreadyRegisteredException.class, () -> {
            saveCustomerUseCase.save(validCustomer);
        });

        assertNotNull(thrownException);
        assertEquals(validCustomer.getEmail(), thrownException.getEmail());

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verifyNoInteractions(validateAgeUseCase, passwordEncripterUseCase, registerRepository);

        verify(loggingService).logError("Error al registrar cliente con email: {}", validCustomer.getEmail(), exception);
    }

    @Test
    void shouldThrowExceptionWhenPasswordEncryptionFails(){

        String passwordActual = validCustomer.getPassword();

        RuntimeException encryptionException = new RuntimeException("Encryption failed");

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(validCustomer.getPassword(), validCustomer.getEmail())).thenThrow(encryptionException);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            saveCustomerUseCase.save(validCustomer);
        });

        assertNotNull(thrownException);
        assertEquals("Encryption failed", thrownException.getMessage());

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verify(validateAgeUseCase).validate(validCustomer);
        verify(passwordEncripterUseCase).encripter(passwordActual, validCustomer.getEmail());
        verifyNoInteractions(registerRepository);

        verify(loggingService).logError("Error al registrar cliente con email: {}", validCustomer.getEmail(), thrownException);
    }

    @Test
    void shouldThrowExceptionWhenRepositorySaveFails(){
        String passwordActual = validCustomer.getPassword();

        RuntimeException repositoryException = new RuntimeException("Repository save failed");

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(validCustomer.getPassword(), validCustomer.getEmail())).thenReturn(savedCustomer.getPassword());
        when(registerRepository.save(any(Customer.class))).thenThrow(repositoryException);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            saveCustomerUseCase.save(validCustomer);
        });

        assertNotNull(thrownException);
        assertEquals("Repository save failed", thrownException.getMessage());

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verify(validateAgeUseCase).validate(validCustomer);
        verify(passwordEncripterUseCase).encripter(passwordActual, validCustomer.getEmail());
        verify(registerRepository).save(any(Customer.class));

        verify(loggingService).logError("Error al registrar cliente con email: {}", validCustomer.getEmail(), thrownException);
    }

    @Test
    void shouldVerifyPasswordIsEncryptedBeforeSaving(){
        String passwordActual = validCustomer.getPassword();

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(validCustomer.getPassword(), validCustomer.getEmail())).thenReturn(savedCustomer.getPassword());
        when(registerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = saveCustomerUseCase.save(validCustomer);

        assertNotNull(result);
        assertEquals(savedCustomer.getPassword(), result.getPassword());
        assertNotEquals(passwordActual, result.getPassword());

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verify(validateAgeUseCase).validate(validCustomer);
        verify(passwordEncripterUseCase).encripter(passwordActual, validCustomer.getEmail());
        verify(registerRepository).save(argThat(customer -> 
            customer.getPassword().equals(savedCustomer.getPassword()) &&
            !customer.getPassword().equals(passwordActual)
        ));

        verify(loggingService).logInfo("Iniciando registro de cliente con email: {} y edad: {}", 
                validCustomer.getEmail(), validCustomer.getAge());

        verify(loggingService).logDebug("Validando unicidad de email: {}", validCustomer.getEmail());
        verify(loggingService).logDebug("Email validado como único: {}", validCustomer.getEmail());

        verify(loggingService).logDebug("Validando edad del cliente: {} años", validCustomer.getAge());
        verify(loggingService).logDebug("Edad validada correctamente: {} años", validCustomer.getAge());

        verify(loggingService).logDebug("Encriptando contraseña para cliente: {}", validCustomer.getEmail());

        verify(loggingService).logDebug("Guardando cliente en base de datos: {}", validCustomer.getEmail());

        verify(loggingService).logInfo("Cliente registrado exitosamente con ID: {}, email: {} y edad: {}", 
                savedCustomer.getId(), savedCustomer.getEmail(), savedCustomer.getAge());

        
    }

    @Test
    void saveCustomer_DatabaseError() {
        String passwordActual = validCustomer.getPassword();

        RuntimeException databaseException = new RuntimeException("Database error");

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(validCustomer.getPassword(), validCustomer.getEmail())).thenReturn(savedCustomer.getPassword());
        when(registerRepository.save(any(Customer.class))).thenThrow(databaseException);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            saveCustomerUseCase.save(validCustomer);
        });

        assertNotNull(thrownException);
        assertEquals("Database error", thrownException.getMessage());

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verify(validateAgeUseCase).validate(validCustomer);
        verify(passwordEncripterUseCase).encripter(passwordActual, validCustomer.getEmail());
        verify(registerRepository).save(any(Customer.class));

        verify(loggingService).logError("Error al registrar cliente con email: {}", validCustomer.getEmail(), thrownException);
    }

    @Test
    void shouldHandleCustomerWithDifferentEmailFormats() {
        String[] testEmails = {
            "customer@company.com",
            "customer.user@company.com", 
            "customer+tag@company.com",
            "customer@subdomain.company.com",
            "customer123@gmail.com"
        };
    
        String originalPassword = "Password123!";
    
        for (String email : testEmails) {
            Customer inputCustomer = new Customer();
            inputCustomer.setName("John");
            inputCustomer.setLastName("Doe");
            inputCustomer.setEmail(email);
            inputCustomer.setPassword(originalPassword);
            inputCustomer.setPhone("3127147814");
            inputCustomer.setBirthDate(LocalDate.now().minusYears(25));
    
            String encryptedPassword = "hashed_" + email;
    
            // Setup mocks
            doNothing().when(validateUniqueEmailUseCase).validate(email);
            doNothing().when(validateAgeUseCase).validate(inputCustomer);
            when(passwordEncripterUseCase.encripter(originalPassword, email)).thenReturn(encryptedPassword);
            
            when(registerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
                Customer c = invocation.getArgument(0);
                Customer saved = new Customer();
                saved.setId(1L);
                saved.setName(c.getName());
                saved.setLastName(c.getLastName());
                saved.setEmail(c.getEmail());
                saved.setPhone(c.getPhone());
                saved.setBirthDate(c.getBirthDate());
                saved.setPassword(c.getPassword());
                return saved;
            });
    
            // Act
            Customer result = saveCustomerUseCase.save(inputCustomer);
    
            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(email, result.getEmail());
            assertEquals(encryptedPassword, result.getPassword());
            assertNotEquals(originalPassword, result.getPassword());
    
            // Verify interactions
            verify(validateUniqueEmailUseCase).validate(email);
            verify(validateAgeUseCase).validate(inputCustomer);
            verify(passwordEncripterUseCase).encripter(originalPassword, email);
            verify(registerRepository).save(any(Customer.class));
    
            // Verify logging
            verify(loggingService).logInfo("Iniciando registro de cliente con email: {} y edad: {}", 
                    email, inputCustomer.getAge());
            verify(loggingService).logDebug("Validando unicidad de email: {}", email);
            verify(loggingService).logDebug("Email validado como único: {}", email);
            verify(loggingService).logDebug("Validando edad del cliente: {} años", inputCustomer.getAge());
            verify(loggingService).logDebug("Edad validada correctamente: {} años", inputCustomer.getAge());
            verify(loggingService).logDebug("Encriptando contraseña para cliente: {}", email);
            verify(loggingService).logDebug("Guardando cliente en base de datos: {}", email);
            verify(loggingService).logInfo("Cliente registrado exitosamente con ID: {}, email: {} y edad: {}", 
                    1L, email, inputCustomer.getAge());
    
            // Reset mocks for next iteration
            reset(validateUniqueEmailUseCase, validateAgeUseCase, passwordEncripterUseCase, registerRepository, loggingService);
        }
    }

    @Test
    void shouldHandleCustomerWithSpecialCharactersInPassword() {
        validCustomer.setPassword("P@ssw0rd!@#$%^&*()_+-={}[]|\\:;\"'<>?,./");
        String originalPassword = validCustomer.getPassword();

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(originalPassword, validCustomer.getEmail())).thenReturn(savedCustomer.getPassword());
        when(registerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = saveCustomerUseCase.save(validCustomer);

        assertNotNull(result);
        assertEquals(savedCustomer.getPassword(), result.getPassword());
        assertNotEquals(originalPassword, result.getPassword());

        verify(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        verify(validateAgeUseCase).validate(validCustomer);
        verify(passwordEncripterUseCase).encripter(originalPassword, validCustomer.getEmail());
        verify(registerRepository).save(any(Customer.class));

        // Verify logging
        verify(loggingService).logInfo("Iniciando registro de cliente con email: {} y edad: {}", 
                validCustomer.getEmail(), validCustomer.getAge());
        verify(loggingService).logDebug("Validando unicidad de email: {}", validCustomer.getEmail());
        verify(loggingService).logDebug("Email validado como único: {}", validCustomer.getEmail());
        verify(loggingService).logDebug("Validando edad del cliente: {} años", validCustomer.getAge());
        verify(loggingService).logDebug("Edad validada correctamente: {} años", validCustomer.getAge());
        verify(loggingService).logDebug("Encriptando contraseña para cliente: {}", validCustomer.getEmail());
        verify(loggingService).logDebug("Guardando cliente en base de datos: {}", validCustomer.getEmail());
        verify(loggingService).logInfo("Cliente registrado exitosamente con ID: {}, email: {} y edad: {}", 
                savedCustomer.getId(), savedCustomer.getEmail(), savedCustomer.getAge());
    }

    @Test
    void shouldHandleCustomerWithEmptyPassword() {
        validCustomer.setPassword("");


        RuntimeException emptyException = new RuntimeException("Password cannot be null or empty");

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(validCustomer.getPassword(), validCustomer.getEmail()))
            .thenThrow(emptyException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            saveCustomerUseCase.save(validCustomer);
        });

        assertNotNull(exception);
        assertEquals(emptyException.getMessage(), exception.getMessage());
        
    }

    @Test
    void shouldHandleCustomerWithNullPassword() {
        
        validCustomer.setPassword(null);


        RuntimeException nullException = new RuntimeException("Password cannot be null or empty");

        doNothing().when(validateUniqueEmailUseCase).validate(validCustomer.getEmail());
        doNothing().when(validateAgeUseCase).validate(validCustomer);
        when(passwordEncripterUseCase.encripter(validCustomer.getPassword(), validCustomer.getEmail()))
            .thenThrow(nullException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            saveCustomerUseCase.save(validCustomer);
        });

        assertNotNull(exception);
        assertEquals(nullException.getMessage(), exception.getMessage());
    }
}