package com.microserviceone.users.registrationApi.integration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import com.microserviceone.users.registrationApi.application.service.RegistrationService;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.AdminEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.CustomerEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.UserEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.repository.ORMregister;
import com.microserviceone.users.registrationApi.web.dto.AdminRequest;
import com.microserviceone.users.registrationApi.web.dto.ApiResponse;
import com.microserviceone.users.registrationApi.web.dto.CustomerRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RegistrationIntegrationSistema {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ORMregister ormRegister;

    @Autowired
    private RegistrationService registrationService;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp(){
        baseUrl = "http://localhost:" + port + "/register";
        // Aquí puedes agregar más configuraciones necesarias antes de cada prueba
        ormRegister.deleteAll(); // Limpia la base de datos antes de cada prueba
    }

    @Test
    void registerCustomer_CompleteFlow_Success(){
        CustomerRequest customerRequest = new CustomerRequest();

        customerRequest.setName("John");
        customerRequest.setLastName("Doe");
        customerRequest.setEmail("pablobedoya3521@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147814");
        customerRequest.setBirthDate(LocalDate.of(2006, 10, 10));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        //Act

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        //asserts http response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Customer registered successfully", response.getBody().getMessage());

        //assert Database State
        Optional<UserEntity> savedUser = ormRegister.findByEmail(customerRequest.getEmail());
        assertTrue(savedUser.isPresent());
        assertEquals(customerRequest.getName(), savedUser.get().getName());
        assertEquals(customerRequest.getLastName(), savedUser.get().getLastName());
        assertEquals(customerRequest.getEmail(), savedUser.get().getEmail());
        assertEquals("CUSTOMER", savedUser.get().getRol());
        assertNotEquals(customerRequest.getPassword(), savedUser.get().getPassword());
        assertEquals(customerRequest.getPhone(), savedUser.get().getPhone());

        //verify it's CustomerEntity with birthDate
        assertTrue(savedUser.get() instanceof CustomerEntity);
        assertEquals(customerRequest.getBirthDate(), ((CustomerEntity) savedUser.get()).getBirthDate());
    }

    @Test
    void registerCustomer_DuplicateEmail_ReturnsConflict(){
        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setName("John");
        existingCustomer.setLastName("Doe");
        existingCustomer.setEmail("pablobedoya352@gmail.com");
        existingCustomer.setPassword("Password123!");
        existingCustomer.setPhone("3127147814");
        existingCustomer.setVerifiedCode(true);
        existingCustomer.setVerifiedTerm(true);
        existingCustomer.setRol("CUSTOMER");
        existingCustomer.setBirthDate(LocalDate.of(2000, 1, 1));


        ormRegister.save(existingCustomer);
        ormRegister.flush();

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("John");
        customerRequest.setLastName("Doe");
        customerRequest.setEmail("pablobedoya352@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147814");
        customerRequest.setBirthDate(LocalDate.of(2006, 10, 10));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("El usuario con email pablobedoya352@gmail.com ya está registrado", response.getBody().getMessage());
    }

    @Test
    void registerAdmin_CompleteFlow_Success() {

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("Alice");
        adminRequest.setLastName("Smith");
        adminRequest.setEmail("alice.smith@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147815");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Admin registered successfully", response.getBody().getMessage());
        // Puedes agregar más asserts para verificar en la base de datos si lo deseas
        //assert Database State
        Optional<UserEntity> savedUser = ormRegister.findByEmail(adminRequest.getEmail());
        assertTrue(savedUser.isPresent());
        assertEquals(adminRequest.getName(), savedUser.get().getName());
        assertEquals(adminRequest.getLastName(), savedUser.get().getLastName());
        assertEquals(adminRequest.getEmail(), savedUser.get().getEmail());
        assertEquals("CUSTOMER", savedUser.get().getRol());
        assertNotEquals(adminRequest.getPassword(), savedUser.get().getPassword());
        assertEquals(adminRequest.getPhone(), savedUser.get().getPhone());

        //verify it's CustomerEntity with birthDate
        assertTrue(savedUser.get() instanceof AdminEntity);
        assertEquals(adminRequest.getArea(), ((AdminEntity) savedUser.get()).getArea());
    }

    @Test
    void registerAdmin_DuplicateEmail_ReturnsConflict() {
        AdminEntity existingAdmin = new AdminEntity();
        existingAdmin.setName("Alice");
        existingAdmin.setLastName("Smith");
        existingAdmin.setEmail("alice.smith@gmail.com");
        existingAdmin.setPassword("Password123!");
        existingAdmin.setPhone("3127147815");
        existingAdmin.setArea("IT");
        existingAdmin.setRol("ADMIN");
        existingAdmin.setVerifiedCode(true);
        existingAdmin.setVerifiedTerm(true);

        ormRegister.save(existingAdmin);
        ormRegister.flush();

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("Alice");
        adminRequest.setLastName("Smith");
        adminRequest.setEmail("alice.smith@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147815");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("ya está registrado"));
    }

    @Test
    void registerCustomer_AgeNotAllowed_ReturnsBadRequest() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("Young");
        customerRequest.setLastName("User");
        customerRequest.setEmail("young.user@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147816");
        customerRequest.setBirthDate(LocalDate.now().minusYears(17)); // 17 años

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("edad"));
    }

    @Test
    void registerCustomer_Exact18Years_Success() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("Eighteen");
        customerRequest.setLastName("User");
        customerRequest.setEmail("eighteen.user@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147817");
        customerRequest.setBirthDate(LocalDate.now().minusYears(18));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    void registerCustomer_veryOldYears_success(){

    }

    @ParameterizedTest
    @ValueSource(strings = {"", "A", " "})
    void registerCustomer_InvalidName_ReturnsBadRequest(String invalidName) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName(invalidName);
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("test" + invalidName + "@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147819");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "312714781", "3127147819123", "+573127147814", "", " "})
    void registerCustomer_InvalidPhone_ReturnsBadRequest(String invalidPhone) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("phone" + invalidPhone.replaceAll("\\D", "") + "@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone(invalidPhone);
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @MethodSource("invalidBirthDates")
    void registerCustomer_InvalidBirthDate_ReturnsBadRequest(LocalDate invalidBirthDate) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("birth" + invalidBirthDate + "@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147819");
        customerRequest.setBirthDate(invalidBirthDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    static Stream<LocalDate> invalidBirthDates() {
        return Stream.of(
            null,
            LocalDate.now().plusDays(1) // Futuro
        );
    }



    //hacer pruebas para admin
    //pruebas de rollback
    //pruebas de performance
}