package com.microserviceone.users.registrationApi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.exception.PasswordNullOrEmptyException;

@ExtendWith(MockitoExtension.class)
public class PasswordEncripterUseCaseTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private PasswordEncripterUseCase passwordEncripterUseCase;

    private static final String TEST_PASSWORD = "Password123!";
    private static final String TEST_EMAIL = "pablobedoya3521@gmail.com";
    private static final String ENCRYPTED_PASSWORD = "$2a$10$encryptedPasswordHash";

    @BeforeEach
    void setup(){
        // No configurar comportamiento por defecto aquí para evitar UnnecessaryStubbingException
    }

    @Test
    void encryptPassword_succes(){
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
        String result = passwordEncripterUseCase.encripter(TEST_PASSWORD, TEST_EMAIL);

        assertNotNull(result);
        assertEquals(ENCRYPTED_PASSWORD, result);
        assertNotEquals(TEST_PASSWORD, result); //verifica que no sea el password original

        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(loggingService).logInfo("Iniciando encriptación de contraseña para email: {}", TEST_EMAIL);
        verify(loggingService).logInfo("Contraseña encriptada exitosamente para email: {}", TEST_EMAIL);
        verify(loggingService).logDebug("Longitud de contraseña encriptada: {} caracteres", ENCRYPTED_PASSWORD.length());
    }

    @Test
    void encryptPassword_LongPassword_Success(){
        //arrange
        String longPassword = "ThisIsAVeryLongPasswordWithSpecialCharacters!@#$%^&*()1234567890";
        String encryptedPassword = "$2a$10$verylonghashedpasswordwithmanycharacters";

        when(passwordEncoder.encode(longPassword)).thenReturn(encryptedPassword);

        String result =  passwordEncripterUseCase.encripter(longPassword, TEST_EMAIL);

        assertNotNull(result);
        assertEquals(encryptedPassword, result);
        assertNotEquals(longPassword, result);

        verify(passwordEncoder).encode(longPassword);
        verify(loggingService).logInfo("Iniciando encriptación de contraseña para email: {}", TEST_EMAIL);
        verify(loggingService).logInfo("Contraseña encriptada exitosamente para email: {}", TEST_EMAIL);
        verify(loggingService).logDebug("Longitud de contraseña encriptada: {} caracteres", 
                encryptedPassword.length());
    }

    @Test
    void shouldHandlePasswordEncoderException(){
        RuntimeException encoderException = new RuntimeException("Encoder error");

        when(passwordEncoder.encode(anyString())).thenThrow(encoderException);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            passwordEncripterUseCase.encripter(TEST_PASSWORD, TEST_EMAIL);
        });

        assertEquals(encoderException, thrownException);

        verify(loggingService).logError("Error al encriptar contraseña para email: {}", TEST_EMAIL, encoderException);
    }

    @Test
    void encryptPassword_ShortPassword_Success(){
        //arrange
        String shortPassword = "12";
        String encryptedPassword = "$2a$10$shorthash";

        when(passwordEncoder.encode(shortPassword)).thenReturn(encryptedPassword);

        String result = passwordEncripterUseCase.encripter(shortPassword, TEST_EMAIL);

        assertNotNull(result);
        assertEquals(encryptedPassword, result);
        assertNotEquals(shortPassword, result);

        verify(passwordEncoder).encode(shortPassword);
        verify(loggingService).logInfo("Iniciando encriptación de contraseña para email: {}", TEST_EMAIL);
        verify(loggingService).logInfo("Contraseña encriptada exitosamente para email: {}", TEST_EMAIL);
        verify(loggingService).logDebug("Longitud de contraseña encriptada: {} caracteres", 
                encryptedPassword.length());
    }

    @Test
    void shouldEncryptDifferentPasswordsWithDifferentResults(){
        String password1 = "Password1!";
        String password2 = "Password2!";

        String encrypted1 = "$2a$10$hash1";
        String encrypted2 = "$2a$10$hash2";

        when(passwordEncoder.encode(password1)).thenReturn(encrypted1);
        when(passwordEncoder.encode(password2)).thenReturn(encrypted2);

        String result1 = passwordEncripterUseCase.encripter(password1, TEST_EMAIL);
        String result2 = passwordEncripterUseCase.encripter(password2, TEST_EMAIL);

        assertNotEquals(result1, result2);
        assertEquals(encrypted1, result1);
        assertEquals(encrypted2, result2);

        verify(passwordEncoder).encode(password1);
        verify(passwordEncoder).encode(password2);

        verify(loggingService, times(2)).logInfo("Iniciando encriptación de contraseña para email: {}", TEST_EMAIL);
        verify(loggingService, times(2)).logInfo("Contraseña encriptada exitosamente para email: {}", TEST_EMAIL);

        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(loggingService, times(2))
            .logDebug(eq("Longitud de contraseña encriptada: {} caracteres"), lengthCaptor.capture());

        List<Integer> capturedLengths = lengthCaptor.getAllValues();
        assertTrue(capturedLengths.contains(encrypted1.length()));
        assertTrue(capturedLengths.contains(encrypted2.length()));
    }

    @Test
    void shouldHandleEmptyPassword(){
        String emptyPassword = "";

        PasswordNullOrEmptyException emptyException = new PasswordNullOrEmptyException();


        PasswordNullOrEmptyException exception = assertThrows(PasswordNullOrEmptyException.class, () ->{
            passwordEncripterUseCase.encripter(emptyPassword, TEST_EMAIL);
        });

        assertNotNull(exception);
        assertEquals(emptyException.getMessage(), exception.getMessage());
    }

    @Test
    void shouldHandleNullPassword(){

        String nullPassword = null;
       
        PasswordNullOrEmptyException nullException = new PasswordNullOrEmptyException();

        PasswordNullOrEmptyException exception = assertThrows(PasswordNullOrEmptyException.class,()->{
            passwordEncripterUseCase.encripter(nullPassword, TEST_EMAIL);
        });

        assertNotNull(exception);
        assertEquals(nullException.getMessage(), exception.getMessage());

    }
    
}