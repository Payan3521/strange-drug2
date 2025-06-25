package com.microserviceone.users.registrationApi.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.exception.UserAlreadyRegisteredException;
import com.microserviceone.users.registrationApi.application.service.RegistrationService;
import com.microserviceone.users.registrationApi.domain.exception.AgeIllegalException;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.web.dto.CustomerRequest;
import com.microserviceone.users.registrationApi.web.dto.UserResponse;
import com.microserviceone.users.registrationApi.web.webMapper.RegistrationWebMapper;

@SpringBootTest
@AutoConfigureMockMvc //para cargar todo el contexto del controller incluso el rate limiting, jwt
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoggingService loggingService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private RegistrationWebMapper registrationWebMapper;

    private CustomerRequest customerRequest;
    private Customer customer;
    private Customer savedCustomer;
    private UserResponse userResponse;

    @BeforeEach
    void setup(){

        customerRequest = new CustomerRequest();
        customerRequest.setName("Juan");
        customerRequest.setLastName("Pérez");
        customerRequest.setEmail("juan.perez@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147814");
        customerRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        customer = new Customer("Juan", "Pérez", "juan.perez@gmail.com", 
                               "Password123!", "3127147814", LocalDate.of(1990, 1, 1));
        
        savedCustomer = new Customer(1L, "Juan", "Pérez", "juan.perez@gmail.com", 
                                   "hashedPassword", "3127147814", LocalDate.of(1990, 1, 1), 
                                   false, false);


        userResponse = UserResponse.builder()
                .id(1L)
                .name("Juan")
                .lastName("Pérez")
                .email("juan.perez@gmail.com")
                .password("hashedPassword")
                .phone("3127147814")
                .rol("CUSTOMER")
                .birthDate(LocalDate.of(1990, 1, 1))
                .verifiedCode(true)
                .verifiedTerm(true)
                .build();
    }

    @Test
    void registerCustomer_success() throws Exception{
        //arrange 
        when(registrationWebMapper.toCustomer(any(CustomerRequest.class))).thenReturn(customer);
        when(registrationService.save(any(Customer.class))).thenReturn(savedCustomer);
        when(registrationWebMapper.toResponse(any(Customer.class))).thenReturn(userResponse);

        //act & assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customer registered successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Juan"))
                .andExpect(jsonPath("$.data.lastName").value("Pérez"))
                .andExpect(jsonPath("$.data.email").value("juan.perez@gmail.com"))
                .andExpect(jsonPath("$.data.password").exists())
                .andExpect(jsonPath("$.data.phone").value("3127147814"))
                .andExpect(jsonPath("$.data.rol").value("CUSTOMER"))
                .andExpect(jsonPath("$.data.birthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.data.area").value(Matchers.nullValue()))
                .andExpect(jsonPath( "$.data.verifiedCode").value(true))
                .andExpect(jsonPath("$.data.verifiedTerm").value(true)
        );

        //verify interactions
        verify(registrationWebMapper).toCustomer(any(CustomerRequest.class));
        verify(registrationService).save(any(Customer.class));
        verify(registrationWebMapper).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_EmailAlreadyExists() throws Exception{
        //when
        when(registrationWebMapper.toCustomer(any(CustomerRequest.class))).thenReturn(customer);
        when(registrationService.save(any(Customer.class))).thenThrow(new UserAlreadyRegisteredException(customerRequest.getEmail()));

        //act & assert 

        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isConflict());

        //verify interactions
        verify(registrationWebMapper).toCustomer(any(CustomerRequest.class));
        verify(registrationService).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_ValidateError() throws Exception{
        //arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("A"); // too short
        invalidRequest.setLastName(""); // empty lastname
        invalidRequest.setEmail("invalid-email"); // invalid email
        invalidRequest.setPassword("123"); //invalid password
        invalidRequest.setPhone("123"); //invalid phone
        invalidRequest.setBirthDate(LocalDate.now().plusDays(1)); // Future age

        //act & assert 
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_EmailEmpty() throws Exception{
        //arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("juan");
        invalidRequest.setLastName("bedoya");
        invalidRequest.setEmail(""); //email vacio
        invalidRequest.setPassword("password123");
        invalidRequest.setPhone("+1234567890");
        invalidRequest.setBirthDate(LocalDate.of(1999, 12, 2));

        //act & assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_EmailWithoutGmail() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@hotmail.com"); // No es @gmail.com
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_EmailInvalid() throws Exception{
        //arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("juan");
        invalidRequest.setLastName("bedoya");
        invalidRequest.setEmail("email-invalid"); //email invalido
        invalidRequest.setPassword("password123");
        invalidRequest.setPhone("+1234567890");
        invalidRequest.setBirthDate(LocalDate.of(1999, 12, 2));

        //act & assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_EmailTooShort() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("a@gmail.com"); // Email muy corto (menos de 5 caracteres)
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_EmailNull() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail(null); // Email nulo
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PasswordEmpty() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword(""); // Password vacío
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PasswordTooShort() throws Exception{
        //arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("juan");
        invalidRequest.setLastName("bedoya");
        invalidRequest.setEmail("juan.perez@example.com");
        invalidRequest.setPassword("123"); //contraseña muy corta
        invalidRequest.setPhone("+1234567890");
        invalidRequest.setBirthDate(LocalDate.of(1999, 12, 2));

        //act & assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PasswordNull() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword(null); // Password nulo
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PasswordWithoutUppercase() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("password123!"); // Sin mayúscula
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PasswordWithoutLowercase() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("PASSWORD123!"); // Sin minúscula
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PasswordWithoutNumber() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("Password!"); // Sin número
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PasswordWithoutSpecialCharacter() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("Password123"); // Sin carácter especial
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PhoneTooShoort() throws Exception{
        //arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("juan");
        invalidRequest.setLastName("bedoya");
        invalidRequest.setEmail("juan.perez@example.com");
        invalidRequest.setPassword("password123"); 
        invalidRequest.setPhone("123"); //telefono muy corto
        invalidRequest.setBirthDate(LocalDate.of(1999, 12, 2));

        //act & assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

     @Test
    void registerCustomer_PhoneEmpty() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone(""); // Teléfono vacío
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PhoneNull() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone(null); // Teléfono nulo
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PhoneWithCountryCode() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone("+573127147814"); // Con código de país
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PhoneNotStartingWith3() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone("2127147814"); // No empieza con 3
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_PhoneTooShort() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone("312714781"); // 9 dígitos en lugar de 10
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_FutureBirthDate() throws Exception {
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("juan");
        invalidRequest.setLastName("bedoya");
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("12345678");
        invalidRequest.setPhone("+573127147814");
        invalidRequest.setBirthDate(LocalDate.now().plusDays(1)); // Fecha futura

        //act & assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_BirthDateNull() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(null); // Fecha de nacimiento nula

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_BirthDateToday() throws Exception {
        // Arrange
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("Juan");
        invalidRequest.setLastName("Pérez");
        invalidRequest.setEmail("juan.perez@gmail.com");
        invalidRequest.setPassword("Password123!");
        invalidRequest.setPhone("3127147814");
        invalidRequest.setBirthDate(LocalDate.now()); // Fecha de hoy (no es pasada)

        // Act & Assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(registrationWebMapper, never()).toCustomer(any(CustomerRequest.class));
        verify(registrationService, never()).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_ServiceException() throws Exception {
        // Configurar mocks
        when(registrationWebMapper.toCustomer(any(CustomerRequest.class))).thenReturn(customer);
        when(registrationService.save(any(Customer.class)))
                .thenThrow(new RuntimeException("Error interno del servicio"));

        //act & assert
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isInternalServerError());

        // verify interactions
        verify(registrationWebMapper).toCustomer(any(CustomerRequest.class));
        verify(registrationService).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_AgeIllegal() throws Exception{
        //arange
        CustomerRequest underageRequest = new CustomerRequest();
        underageRequest.setName("maria");
        underageRequest.setLastName("gonzales");
        underageRequest.setEmail("mariaGonzales@gmail.com");
        underageRequest.setPassword("Password123!");
        underageRequest.setPhone("3127147814");
        underageRequest.setBirthDate(LocalDate.now().minusYears(16)); //years old

        Customer underageCustomer = new Customer();
        underageCustomer.setName("maria");
        underageCustomer.setLastName("gonzales");
        underageCustomer.setEmail("mariaGonzales@gmail.com");
        underageCustomer.setPassword("Password123!");
        underageCustomer.setPhone("3127147814");
        underageCustomer.setBirthDate(LocalDate.now().minusYears(16)); //years old

        when(registrationWebMapper.toCustomer(any(CustomerRequest.class))).thenReturn(underageCustomer);
        when(registrationService.save(any(Customer.class))).thenThrow(new AgeIllegalException(underageCustomer.getAge()));


        //act & assert 
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(underageRequest)))
                .andExpect(status().isBadRequest());

        // verify interations
        verify(registrationWebMapper).toCustomer(any(CustomerRequest.class));
        verify(registrationService).save(any(Customer.class));
        verify(registrationWebMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_MalformedJson() throws Exception {
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCustomer_MissingContentType() throws Exception {
        mockMvc.perform(post("/register/customer")
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void registerCustomer_EmptyRequestBody() throws Exception {
        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCustomer_VerifyPasswordIsHashed() throws Exception{
        //configurar mocks
        when(registrationWebMapper.toCustomer(any(CustomerRequest.class))).thenReturn(customer);
        when(registrationService.save(any(Customer.class))).thenReturn(savedCustomer);
        when(registrationWebMapper.toResponse(any(Customer.class))).thenReturn(userResponse);

        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.password").value("hashedPassword")) // Verificar que no sea el password original
                .andExpect(jsonPath("$.data.password").value(org.hamcrest.Matchers.not("Password123!"))); // Verificar que no sea el password original
    
        verify(registrationWebMapper).toCustomer(any(CustomerRequest.class));
        verify(registrationService).save(any(Customer.class));
        verify(registrationWebMapper).toResponse(any(Customer.class));
    }

    @Test
    void registerCustomer_MappingException() throws Exception {
        // Simular error en el mapeo de request a dominio
        when(registrationWebMapper.toCustomer(any(CustomerRequest.class)))
                .thenThrow(new RuntimeException("Error en mapeo"));

        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isInternalServerError());

        verify(registrationWebMapper).toCustomer(any(CustomerRequest.class));
    }

    @Test
    void registerCustomer_ResponseMappingException() throws Exception {
        // Configurar mocks - error en mapeo de respuesta
        when(registrationWebMapper.toCustomer(any(CustomerRequest.class))).thenReturn(customer);
        when(registrationService.save(any(Customer.class))).thenReturn(savedCustomer);
        when(registrationWebMapper.toResponse(any(Customer.class)))
                .thenThrow(new RuntimeException("Error en mapeo de respuesta"));

        mockMvc.perform(post("/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isInternalServerError());

        verify(registrationWebMapper).toCustomer(any(CustomerRequest.class));
        verify(registrationService).save(any(Customer.class));
        verify(registrationWebMapper).toResponse(any(Customer.class));
    }
}