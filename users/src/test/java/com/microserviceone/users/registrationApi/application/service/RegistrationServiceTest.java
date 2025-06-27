package com.microserviceone.users.registrationApi.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveAdmin;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveCustomer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {
    @Mock
    private ISaveAdmin saveAdmin;

    @Mock
    private ISaveCustomer saveCustomer;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private RegistrationService registrationService;

    private Customer customer;

    private Customer savedCustomer;

    private Admin admin;

    private Admin savedAdmin;

    @BeforeEach
    void setUp(){
        customer = new Customer();
        customer.setName("juan");
        customer.setLastName("perez");
        customer.setEmail("pablobedoya3521@gmail.com");
        customer.setPassword("Password123!");
        customer.setPhone("3127147814");
        customer.setBirthDate(LocalDate.of(1990, 11, 11));

        savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName("juan");
        savedCustomer.setLastName("perez");
        savedCustomer.setEmail("pablobedoya3521@gmail.com");
        savedCustomer.setPassword("hashedPassword");
        savedCustomer.setPhone("3127147814");
        savedCustomer.setBirthDate(LocalDate.of(1990, 11, 11));
        savedCustomer.setVerifiedCode(true);
        savedCustomer.setVerifiedTerm(true);

        admin = new Admin();
        admin.setName("admin");
        admin.setLastName("admin");
        admin.setEmail("admin123@gmail.com");
        admin.setPassword("Admin123!");
        admin.setPhone("3127147814");
        admin.setArea("SISTEMAS");


        savedAdmin = new Admin();
        savedAdmin.setId(1L);
        savedAdmin.setName("admin");
        savedAdmin.setLastName("admin");
        savedAdmin.setEmail("admin123@gmail.com");
        savedAdmin.setPassword("hashedPassword");
        savedAdmin.setPhone("3127147814");
        savedAdmin.setArea("SISTEMAS");
        savedAdmin.setVerifiedCode(true);
        savedAdmin.setVerifiedTerm(true);
    }

    @Test
    void saveCustomer_success(){

        String passwordOriginal = customer.getPassword();

        when(saveCustomer.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = registrationService.save(customer);

        // Assertions can be added here to verify the result
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(customer.getName(), result.getName());
        assertEquals(customer.getLastName(), result.getLastName());
        assertEquals(customer.getEmail(), result.getEmail());
        assertEquals(customer.getPhone(), result.getPhone());
        assertEquals(customer.getBirthDate(), result.getBirthDate());
        assertEquals(savedCustomer.getPassword(), result.getPassword());
        assertEquals(savedCustomer.isVerifiedCode(), result.isVerifiedCode());
        assertEquals(savedCustomer.isVerifiedTerm(), result.isVerifiedTerm());

        assertNotEquals(passwordOriginal, result.getPassword());

        // Verify that the saveCustomer method was called with the correct customer object
        verify(saveCustomer).save(customer);
        verify(loggingService).logInfo("RegistrationService: Procesando registro de cliente - Email: {}", customer.getEmail());
        verify(loggingService).logInfo("RegistrationService: Cliente registrado exitosamente - ID: {}, Email: {}", 
            result.getId(), result.getEmail());

    }

    @Test
    void saveCustomer_error(){
        RuntimeException exception = new RuntimeException("Error al guardar el cliente");

        when(saveCustomer.save(any(Customer.class))).thenThrow(exception);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            registrationService.save(customer);
        });

        assertNotNull(thrownException);
        assertEquals(exception.getMessage(), thrownException.getMessage());

        verify(saveCustomer).save(customer);
        verify(loggingService).logInfo("RegistrationService: Procesando registro de cliente - Email: {}", customer.getEmail());
        verify(loggingService).logError("RegistrationService: Error al registrar cliente - Email: {}", customer.getEmail(), exception);
    }

    @Test
    void saveAdmin_success(){

        String passwordOriginal = admin.getPassword();

        when(saveAdmin.save(any(Admin.class))).thenReturn(savedAdmin);

        Admin result = registrationService.save(admin);

        // Assertions can be added here to verify the result
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(admin.getName(), result.getName());
        assertEquals(admin.getLastName(), result.getLastName());
        assertEquals(admin.getEmail(), result.getEmail());
        assertEquals(admin.getPhone(), result.getPhone());
        assertEquals(admin.getArea(), result.getArea());
        assertEquals(savedAdmin.getPassword(), result.getPassword());
        assertEquals(savedAdmin.isVerifiedCode(), result.isVerifiedCode());
        assertEquals(savedAdmin.isVerifiedTerm(), result.isVerifiedTerm());
        assertNotEquals(passwordOriginal, result.getPassword());

        // Verify that the saveAdmin method was called with the correct admin object
        verify(saveAdmin).save(admin);
        verify(loggingService).logInfo("RegistrationService: Procesando registro de administrador - Email: {}", admin.getEmail());
        verify(loggingService).logInfo("RegistrationService: Administrador registrado exitosamente - ID: {}, Email: {}", 
            result.getId(), result.getEmail());
    }

    @Test
    void saveAdmin_error(){

        RuntimeException exception = new RuntimeException("Error al guardar el administrador");

        when(saveAdmin.save(any(Admin.class))).thenThrow(exception);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            registrationService.save(admin);
        });

        assertNotNull(thrownException);
        assertEquals(exception.getMessage(), thrownException.getMessage());

        verify(saveAdmin).save(admin);
        verify(loggingService).logInfo("RegistrationService: Procesando registro de administrador - Email: {}", admin.getEmail());
        verify(loggingService).logError("RegistrationService: Error al registrar administrador - Email: {}", admin.getEmail(), exception);
    }
}