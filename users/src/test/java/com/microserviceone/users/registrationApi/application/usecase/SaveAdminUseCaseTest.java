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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.exception.UserAlreadyRegisteredException;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;


@ExtendWith(MockitoExtension.class)
public class SaveAdminUseCaseTest {

    @Mock
    private ValidateUniqueEmailUseCase validateUniqueEmailUseCase;

    @Mock
    private PasswordEncripterUseCase passwordEncripterUseCase;

    @Mock
    private IRegisterRepository registerRepository; //aca si se puede porque esto si se simula

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private SaveAdminUseCase saveAdmin; //no se pone capa de abstraccion ya que e sla clase en sí

    private Admin validAdmin;

    private Admin savedAdmin;

    @BeforeEach
    void setUp(){

        validAdmin = new Admin();
        validAdmin.setName("Pablo");
        validAdmin.setLastName("Bedoya");
        validAdmin.setEmail("pablobedoya3521@gmail.com");
        validAdmin.setPassword("Password123!");
        validAdmin.setPhone("3127147814");
        validAdmin.setArea("SISTEMAS");

        savedAdmin = new Admin();
        savedAdmin.setId(1L);
        savedAdmin.setName(validAdmin.getName());
        savedAdmin.setLastName(validAdmin.getLastName());
        savedAdmin.setEmail(validAdmin.getEmail());
        savedAdmin.setPhone(validAdmin.getPhone());
        savedAdmin.setArea(validAdmin.getArea());
        savedAdmin.setPassword("hashedPassword");

    }

    @Test
    void shouldSaveAdminSuccessfully(){

        String originalPassword = validAdmin.getPassword();

        doNothing().when(validateUniqueEmailUseCase).validate(validAdmin.getEmail());

        when(passwordEncripterUseCase.encripter(validAdmin.getPassword(), validAdmin.getEmail())).thenReturn(savedAdmin.getPassword());
        
        when(registerRepository.save(any(Admin.class))).thenReturn(savedAdmin);

        Admin result = saveAdmin.save(validAdmin);

        assertNotNull(result);
        assertEquals(savedAdmin.getId(), result.getId());
        assertEquals(savedAdmin.getName(), result.getName());
        assertEquals(savedAdmin.getLastName(), result.getLastName());
        assertEquals(savedAdmin.getEmail(), result.getEmail());
        assertEquals(savedAdmin.getPhone(), result.getPhone());
        assertEquals(savedAdmin.getArea(), result.getArea());
        assertEquals(savedAdmin.getPassword(), result.getPassword());
        assertNotEquals(originalPassword, result.getPassword());


        verify(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        verify(passwordEncripterUseCase).encripter(originalPassword, validAdmin.getEmail());
        verify(registerRepository).save(any(Admin.class));

        verify(loggingService).logInfo("Iniciando registro de administrador con email: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Validando unicidad de email: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Email validado como único: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Encriptando contraseña para admin: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Guardando administrador en base de datos: {}", validAdmin.getEmail());
        verify(loggingService).logInfo("Administrador registrado exitosamente con ID: {} y email: {}", 
                savedAdmin.getId(), savedAdmin.getEmail());
    }

    @Test
    void shouldThrowUserAlreadyRegisteredExceptionWhenEmailAlreadyExists(){
        UserAlreadyRegisteredException exception = new UserAlreadyRegisteredException(validAdmin.getEmail());
        doThrow(exception).when(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        
        UserAlreadyRegisteredException thrownException = assertThrows(UserAlreadyRegisteredException.class, () -> {
            saveAdmin.save(validAdmin);
        });

        assertNotNull(thrownException);
        assertEquals(validAdmin.getEmail(), thrownException.getEmail());

        verify(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        verifyNoInteractions(passwordEncripterUseCase, registerRepository);

        verify(loggingService).logError("Error al registrar administrador con email: {}", validAdmin.getEmail(), exception);
    }

    @Test
    void shouldThrowExceptionWhenPasswordEncryptionFails(){
        RuntimeException encryptionException = new RuntimeException("Encryption failed");
        
        doNothing().when(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        when(passwordEncripterUseCase.encripter(validAdmin.getPassword(), validAdmin.getEmail())).thenThrow(encryptionException);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            saveAdmin.save(validAdmin);
        });

        assertNotNull(thrownException);
        assertEquals(encryptionException.getMessage(), thrownException.getMessage());

        verify(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        verify(passwordEncripterUseCase).encripter(validAdmin.getPassword(), validAdmin.getEmail());
        verifyNoInteractions(registerRepository);

        verify(loggingService).logError("Error al registrar administrador con email: {}", validAdmin.getEmail(), encryptionException);
    }

    @Test
    void shouldThrowExceptionWhenRepositorySaveFails(){

        String originalPassword = validAdmin.getPassword();

        RuntimeException repositoryException = new RuntimeException("Database error");

        doNothing().when(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        when(passwordEncripterUseCase.encripter(validAdmin.getPassword(), validAdmin.getEmail())).thenReturn(savedAdmin.getPassword());
        when(registerRepository.save(any(Admin.class))).thenThrow(repositoryException);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            saveAdmin.save(validAdmin);
        });

        assertNotNull(thrownException);
        assertEquals(repositoryException.getMessage(), thrownException.getMessage());

        verify(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        verify(passwordEncripterUseCase).encripter(originalPassword, validAdmin.getEmail());
        verify(registerRepository).save(any(Admin.class));

        verify(loggingService).logError("Error al registrar administrador con email: {}", validAdmin.getEmail(), repositoryException);
    }

    @Test
    void shouldHandleAdminWithDifferentEmailFormats() {

        String[] testEmails = {
            "admin@company.com",
            "admin.user@company.com",
            "admin+tag@company.com",
            "admin@subdomain.company.com"
        };

        String originalPassword = "Password123!";

        for (String email : testEmails) {
            Admin inputAdmin = new Admin();
            inputAdmin.setName("Pablo");
            inputAdmin.setLastName("Bedoya");
            inputAdmin.setEmail(email);
            inputAdmin.setPassword(originalPassword);
            inputAdmin.setPhone("3127147814");
            inputAdmin.setArea("SISTEMAS");

            // Generar una contraseña única por correo (solo para diferenciarlas si quieres)
            String encryptedPassword = "hashed_" + email;

            // Stubbing correcto con valores dinámicos
            doNothing().when(validateUniqueEmailUseCase).validate(email);
            when(passwordEncripterUseCase.encripter(originalPassword, email))
                .thenReturn(encryptedPassword);

            // Simular que se guarda el admin y se le asigna un ID
            when(registerRepository.save(any(Admin.class))).thenAnswer(invocation -> {
                Admin a = invocation.getArgument(0);
                Admin saved = new Admin();
                saved.setId(a.getId() != null ? a.getId() : 1L); // Simular ID generado
                saved.setName(a.getName());
                saved.setLastName(a.getLastName());
                saved.setEmail(a.getEmail());
                saved.setPhone(a.getPhone());
                saved.setArea(a.getArea());
                saved.setPassword(a.getPassword()); // que ya está encriptada
                return saved;
            });

            // Act
            Admin result = saveAdmin.save(inputAdmin);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(email, result.getEmail());
            assertEquals(encryptedPassword, result.getPassword());
            assertNotEquals(originalPassword, result.getPassword());

            verify(validateUniqueEmailUseCase).validate(email);
            verify(passwordEncripterUseCase).encripter(originalPassword, email);
            verify(registerRepository).save(any(Admin.class));

            verify(loggingService).logInfo("Iniciando registro de administrador con email: {}", email);
            verify(loggingService).logDebug("Validando unicidad de email: {}", email);
            verify(loggingService).logDebug("Email validado como único: {}", email);
            verify(loggingService).logDebug("Encriptando contraseña para admin: {}", email);
            verify(loggingService).logDebug("Guardando administrador en base de datos: {}", email);
            verify(loggingService).logInfo("Administrador registrado exitosamente con ID: {} y email: {}", 1L, email);

            reset(registerRepository, passwordEncripterUseCase, validateUniqueEmailUseCase, loggingService); // Resetear mocks para la siguiente iteración
        }
    }

    @Test
    void shouldHandleAdminWithSpecialCharactersInPassword() {
        validAdmin.setPassword("P@ssw0rd!@#$%^&*()");

        String originalPassword = validAdmin.getPassword();
        
        doNothing().when(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        when(passwordEncripterUseCase.encripter(validAdmin.getPassword(), validAdmin.getEmail())).thenReturn(savedAdmin.getPassword());
        when(registerRepository.save(any(Admin.class))).thenReturn(savedAdmin);

        Admin result = saveAdmin.save(validAdmin);

        assertNotNull(result);
        assertEquals(savedAdmin.getPassword(), result.getPassword());
        assertNotEquals(originalPassword, result.getPassword());

        verify(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        verify(passwordEncripterUseCase).encripter(originalPassword, validAdmin.getEmail());
        verify(registerRepository).save(any(Admin.class));

        verify(loggingService).logInfo("Iniciando registro de administrador con email: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Validando unicidad de email: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Email validado como único: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Encriptando contraseña para admin: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Guardando administrador en base de datos: {}", validAdmin.getEmail());
        verify(loggingService).logInfo("Administrador registrado exitosamente con ID: {} y email: {}", 
                savedAdmin.getId(), savedAdmin.getEmail());
    }

    @Test
    void shouldVerifyPasswordIsEncryptedBeforeSaving(){

        String originalPassword = validAdmin.getPassword();

        doNothing().when(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        when(passwordEncripterUseCase.encripter(validAdmin.getPassword(), validAdmin.getEmail())).thenReturn(savedAdmin.getPassword());
        when(registerRepository.save(any(Admin.class))).thenReturn(savedAdmin);

        saveAdmin.save(validAdmin);

        assertNotNull(savedAdmin.getPassword());
        assertEquals(savedAdmin.getPassword(), validAdmin.getPassword());
        assertNotEquals(originalPassword, savedAdmin.getPassword());


        verify(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        verify(passwordEncripterUseCase).encripter(originalPassword, validAdmin.getEmail());
        verify(registerRepository).save(argThat(admin ->
            admin.getPassword().equals(savedAdmin.getPassword())&&
            !admin.getPassword().equals(originalPassword)
        ));

        verify(loggingService).logInfo("Iniciando registro de administrador con email: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Validando unicidad de email: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Email validado como único: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Encriptando contraseña para admin: {}", validAdmin.getEmail());
        verify(loggingService).logDebug("Guardando administrador en base de datos: {}", validAdmin.getEmail());
        verify(loggingService).logInfo("Administrador registrado exitosamente con ID: {} y email: {}", 
                savedAdmin.getId(), savedAdmin.getEmail());
    }

    @Test
    void shouldHandleAdminWithEmptyPassword(){ 

        validAdmin.setPassword("");

        IllegalArgumentException emptyException = new IllegalArgumentException("Password cannot be null or empty");

        doNothing().when(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        // No mockees el encripter, deja que lance la excepción real
        when(passwordEncripterUseCase.encripter(validAdmin.getPassword(), validAdmin.getEmail()))
            .thenThrow(emptyException);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            saveAdmin.save(validAdmin);
        });

        assertNotNull(exception);
        assertEquals(emptyException.getMessage(), exception.getMessage());

    }

    @Test
    void shouldHandleAdminWithNullPassword(){ 
        validAdmin.setPassword(null);

        IllegalArgumentException nullException = new IllegalArgumentException("Password cannot be null or empty");

        doNothing().when(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        
        when(passwordEncripterUseCase.encripter(null, validAdmin.getEmail()))
            .thenThrow(nullException);
    
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            saveAdmin.save(validAdmin);
        });
    
        assertNotNull(exception);
        assertEquals(nullException.getMessage(), exception.getMessage());
    }

    @Test
    void saveAdmin_DatabaseError(){

        String originalPassword = validAdmin.getPassword();

        RuntimeException databaseError = new RuntimeException("Database error");

        doNothing().when(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        when(passwordEncripterUseCase.encripter(validAdmin.getPassword(), validAdmin.getEmail())).thenReturn(savedAdmin.getPassword());
        when(registerRepository.save(any(Admin.class))).thenThrow(databaseError);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            saveAdmin.save(validAdmin);
        });

        assertNotNull(thrownException);
        assertEquals(databaseError.getMessage(), thrownException.getMessage());

        verify(validateUniqueEmailUseCase).validate(validAdmin.getEmail());
        verify(passwordEncripterUseCase).encripter(originalPassword, validAdmin.getEmail());
        verify(registerRepository).save(any(Admin.class));
        verify(loggingService).logError("Error al registrar administrador con email: {}", validAdmin.getEmail(), databaseError);
    }

}