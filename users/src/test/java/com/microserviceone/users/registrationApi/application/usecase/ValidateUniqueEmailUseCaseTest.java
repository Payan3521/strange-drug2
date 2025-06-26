package com.microserviceone.users.registrationApi.application.usecase;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.exception.UserAlreadyRegisteredException;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;

@ExtendWith(MockitoExtension.class)
public class ValidateUniqueEmailUseCaseTest {
    
    @Mock
    private IRegisterRepository registerRepository;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private ValidateUniqueEmailUseCase validateUniqueEmailUseCase;

    private static final String VALID_EMAIL = "test@gmail.com";
    private static final String DUPLICATE_EMAIL = "existing@gmail.com";
    private static final String EMPTY_EMAIL = "";
    private static final String NULL_EMAIL = null;

    @BeforeEach
    void setUp() {
        // No configurar comportamiento por defecto aquí para evitar UnnecessaryStubbingException
    }

    @Test
    void shouldValidateUniqueEmailSuccessfully(){
        assertDoesNotThrow(() -> validateUniqueEmailUseCase.validate(VALID_EMAIL));

        verify(registerRepository).existsByEmail(VALID_EMAIL);
        verify(loggingService).logDebug("Validando unicidad de email: {}", VALID_EMAIL);
        verify(loggingService).logDebug("Email validado como único: {}", VALID_EMAIL);
        verifyNoMoreInteractions(loggingService);
    }

    @Test
    void shouldThrowUserAlreadyRegisteredExceptionForDuplicateEmail(){
        when(registerRepository.existsByEmail(DUPLICATE_EMAIL)).thenReturn(true);

        UserAlreadyRegisteredException exception = assertThrows(UserAlreadyRegisteredException.class, () -> {
            validateUniqueEmailUseCase.validate(DUPLICATE_EMAIL);
        });

        assertNotNull(exception);
        assertEquals(DUPLICATE_EMAIL, exception.getEmail());

        verify(registerRepository).existsByEmail(DUPLICATE_EMAIL);
        verify(loggingService).logDebug("Validando unicidad de email: {}", DUPLICATE_EMAIL);
        verify(loggingService).logWarning("Email ya registrado en el sistema: {}", DUPLICATE_EMAIL);
        verify(loggingService).logError("Excepción de email duplicado: {}", DUPLICATE_EMAIL, exception);
    }

    @Test
    void shouldHandleEmptyEmail(){
        IllegalArgumentException emptyException = new IllegalArgumentException("Email no puede ser nulo o vacío");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validateUniqueEmailUseCase.validate(EMPTY_EMAIL);
        });

        assertNotNull(exception);
        assertEquals(emptyException.getMessage(), exception.getMessage());

        verifyNoInteractions(registerRepository);
        verifyNoInteractions(loggingService);
    }

    @Test
    void shouldHandleNullEmail(){
        IllegalArgumentException nullException = new IllegalArgumentException("Email no puede ser nulo o vacío");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validateUniqueEmailUseCase.validate(NULL_EMAIL);
        });

        assertNotNull(exception);
        assertEquals(nullException.getMessage(), exception.getMessage());

        verifyNoInteractions(registerRepository);
        verifyNoInteractions(loggingService);
    }

    @Test
    void shouldHandleDifferentEmailFormats(){
        // Given
        String[] testEmails = {
            "user@domain.com",
            "user.name@domain.com",
            "user+tag@domain.com",
            "user@subdomain.domain.com",
            "user@domain.co.uk",
            "user123@domain.com"
        };

        for (String email : testEmails) {
            when(registerRepository.existsByEmail(email)).thenReturn(false);
        }

        // When & Then
        for (String email : testEmails) {
            assertDoesNotThrow(() -> validateUniqueEmailUseCase.validate(email));
            verify(registerRepository).existsByEmail(email);
            verify(loggingService).logDebug("Validando unicidad de email: {}", email);
            verify(loggingService).logDebug("Email validado como único: {}", email);
            verifyNoMoreInteractions(loggingService);
            verifyNoMoreInteractions(registerRepository);
        }
    }

    @Test
    void shouldHandleRepositoryException(){
        RuntimeException repositoryException = new RuntimeException("Error al acceder al repositorio");

        when(registerRepository.existsByEmail(anyString())).thenThrow(repositoryException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            validateUniqueEmailUseCase.validate(VALID_EMAIL);
        });

        assertNotNull(exception);
        assertEquals(repositoryException.getMessage(), exception.getMessage());

        verify(registerRepository).existsByEmail(VALID_EMAIL);
        verify(loggingService).logDebug("Validando unicidad de email: {}", VALID_EMAIL);
        verify(loggingService).logError("Error inesperado al validar email: {}", VALID_EMAIL, repositoryException);
        verifyNoMoreInteractions(loggingService);
        verifyNoMoreInteractions(registerRepository);

    }

    @Test
    void shouldHandleMultipleDuplicateEmails(){
                // Given
        String[] duplicateEmails = {
            "duplicate1@example.com",
            "duplicate2@example.com",
            "duplicate3@example.com"
        };

        for (String email : duplicateEmails) {
            when(registerRepository.existsByEmail(email)).thenReturn(true);
        }

        // When & Then
        for (String email : duplicateEmails) {
            UserAlreadyRegisteredException exception = assertThrows(UserAlreadyRegisteredException.class, () -> {
                validateUniqueEmailUseCase.validate(email);
            });

            assertNotNull(exception);
            assertEquals(email, exception.getEmail());

            verify(registerRepository).existsByEmail(email);
            verify(loggingService).logDebug("Validando unicidad de email: {}", email);
            verify(loggingService).logWarning("Email ya registrado en el sistema: {}", email);
            verify(loggingService).logError("Excepción de email duplicado: {}", email, exception);
            verifyNoMoreInteractions(loggingService);
            verifyNoMoreInteractions(registerRepository);
        }
    }
    
    @Test
    void shouldHandleCaseInsensitiveEmailValidation(){
        // Given
        String emailLower = "test@example.com";
        String emailUpper = "TEST@EXAMPLE.COM";
        String emailMixed = "Test@Example.com";

        when(registerRepository.existsByEmail(emailLower)).thenReturn(false);
        when(registerRepository.existsByEmail(emailUpper)).thenReturn(false);
        when(registerRepository.existsByEmail(emailMixed)).thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> validateUniqueEmailUseCase.validate(emailLower));
        assertDoesNotThrow(() -> validateUniqueEmailUseCase.validate(emailUpper));
        assertDoesNotThrow(() -> validateUniqueEmailUseCase.validate(emailMixed));

        verify(registerRepository).existsByEmail(emailLower);
        verify(registerRepository).existsByEmail(emailUpper);
        verify(registerRepository).existsByEmail(emailMixed);
        verify(loggingService).logDebug("Validando unicidad de email: {}", emailLower);
        verify(loggingService).logDebug("Validando unicidad de email: {}", emailUpper);
        verify(loggingService).logDebug("Validando unicidad de email: {}", emailMixed);
        verify(loggingService).logDebug("Email validado como único: {}", emailLower);
        verify(loggingService).logDebug("Email validado como único: {}", emailUpper);
        verify(loggingService).logDebug("Email validado como único: {}", emailMixed);
        verifyNoMoreInteractions(loggingService);
        verifyNoMoreInteractions(registerRepository);
    }

    @Test
    void shouldHandleSpecialCharactersInEmail(){
        String emailWithSpecialChars = "user.name+tag@domain-name.com";
        when(registerRepository.existsByEmail(emailWithSpecialChars)).thenReturn(false);

        assertDoesNotThrow(() -> validateUniqueEmailUseCase.validate(emailWithSpecialChars));
        verify(registerRepository).existsByEmail(emailWithSpecialChars);
        verify(loggingService).logDebug("Validando unicidad de email: {}", emailWithSpecialChars);
        verify(loggingService).logDebug("Email validado como único: {}", emailWithSpecialChars);
        verifyNoMoreInteractions(loggingService);
        verifyNoMoreInteractions(registerRepository);
    }

    @Test
    void shouldHandleVeryLongEmail(){
        String longEmail = "a".repeat(255) + "@domain.com"; // Email length exceeds typical limits
        when(registerRepository.existsByEmail(longEmail)).thenReturn(false);
        assertDoesNotThrow(() -> validateUniqueEmailUseCase.validate(longEmail));
        verify(registerRepository).existsByEmail(longEmail);
        verify(loggingService).logDebug("Validando unicidad de email: {}", longEmail);
        verify(loggingService).logDebug("Email validado como único: {}", longEmail);
        verifyNoMoreInteractions(loggingService);
        verifyNoMoreInteractions(registerRepository);
    }
}