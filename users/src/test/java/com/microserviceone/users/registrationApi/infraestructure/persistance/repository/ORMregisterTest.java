package com.microserviceone.users.registrationApi.infraestructure.persistance.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.UserEntity;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ORMregisterTest {
    
    @Autowired
    private ORMregister ormRegister;

    @Autowired
    private TestEntityManager entityManager;

    private UserEntity userEntity;

    @BeforeEach
    void setUp(){
        userEntity = UserEntity.builder()
        .name("juan")
        .lastName("pereza")
        .email("juan.pereza@gmail.com")
        .password("hashedPassword")
        .phone("3127147814")
        .rol("CUSTOMER")
        .verifiedCode(true)
        .verifiedTerm(true)
        .build();

    }

    @Test
    void findByEmail_success(){

        entityManager.persistAndFlush(userEntity);

        Optional<UserEntity> result = ormRegister.findByEmail(userEntity.getEmail());

        assertTrue(result.isPresent());
        assertEquals(userEntity.getEmail(), result.get().getEmail());
        assertEquals(userEntity.getName(), result.get().getName());
        assertEquals(userEntity.getLastName(), result.get().getLastName());
        assertEquals(userEntity.getPhone(), result.get().getPhone());
        assertEquals(userEntity.getRol(), result.get().getRol());
        assertEquals(userEntity.isVerifiedCode(), result.get().isVerifiedCode());
        assertEquals(userEntity.isVerifiedTerm(), result.get().isVerifiedTerm());
        assertEquals(userEntity.getPassword(), result.get().getPassword());
    }

    @Test
    void findByEmail_notFound(){
        Optional<UserEntity> result = ormRegister.findByEmail("noneExist@gmail.com");
        assertTrue(result.isEmpty());
        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_caseSensitive(){
        Optional<UserEntity> result = ormRegister.findByEmail("JUAN.PEREZA@GMAIL.COM");
        assertTrue(result.isEmpty());
        assertFalse(result.isPresent());
    }

    @Test
    void existsByEmail_success(){

        entityManager.persistAndFlush(userEntity);

        boolean exists = ormRegister.existsByEmail(userEntity.getEmail());
        assertTrue(exists);
    }

    @Test
    void existsByEmail_notFound() {
        boolean exists = ormRegister.existsByEmail("noExiste@gmail.com");
        assertFalse(exists);
    }

    @Test
    void existsByEmail_caseSensitive() {
        boolean exists = ormRegister.existsByEmail("JUAN.PEREZA@GMAIL.COM");
        assertFalse(exists);
    }

    @Test
    void save_newUser_success(){

        UserEntity userSaved = ormRegister.save(userEntity);

        entityManager.flush();

        assertNotNull(userSaved);
        assertEquals(userEntity.getEmail(), userSaved.getEmail());
        assertEquals(userEntity.getName(), userSaved.getName());
        assertEquals(userEntity.getLastName(), userSaved.getLastName());
        assertEquals(userEntity.getPhone(), userSaved.getPhone());
        assertEquals(userEntity.getRol(), userSaved.getRol());
        assertEquals(userEntity.isVerifiedCode(), userSaved.isVerifiedCode());
        assertEquals(userEntity.isVerifiedTerm(), userSaved.isVerifiedTerm());
        assertEquals(userEntity.getPassword(), userSaved.getPassword());

        // Verify it's actually saved in database
        Optional<UserEntity> foundUser = ormRegister.findById(userSaved.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(userSaved.getEmail(), foundUser.get().getEmail());
        assertEquals(userSaved.getName(), foundUser.get().getName());
        assertEquals(userSaved.getLastName(), foundUser.get().getLastName());
        assertEquals(userSaved.getPhone(), foundUser.get().getPhone());
        assertEquals(userSaved.getRol(), foundUser.get().getRol()); 
        assertEquals(userSaved.isVerifiedCode(), foundUser.get().isVerifiedCode());
        assertEquals(userSaved.isVerifiedTerm(), foundUser.get().isVerifiedTerm());
        assertEquals(userSaved.getPassword(), foundUser.get().getPassword());
    }

    @Test
    void save_updateUser_success(){

        entityManager.persistAndFlush(userEntity);

        userEntity.setName("Updated Name");
        userEntity.setLastName("Updated LastName");

        UserEntity updatedUser = ormRegister.save(userEntity);

        entityManager.flush();

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("Updated LastName", updatedUser.getLastName());

        // Verify it's actually updated in database
        Optional<UserEntity> foundUser = ormRegister.findById(updatedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Updated Name", foundUser.get().getName());
        assertEquals("Updated LastName", foundUser.get().getLastName());
    }

    @Test
    void findById_success(){

        entityManager.persistAndFlush(userEntity);

        Optional<UserEntity> foundUser = ormRegister.findById(userEntity.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(userEntity.getEmail(), foundUser.get().getEmail());
        assertEquals(userEntity.getName(), foundUser.get().getName());
        assertEquals(userEntity.getLastName(), foundUser.get().getLastName());
        assertEquals(userEntity.getPhone(), foundUser.get().getPhone());
        assertEquals(userEntity.getRol(), foundUser.get().getRol());
        assertEquals(userEntity.isVerifiedCode(), foundUser.get().isVerifiedCode());
        assertEquals(userEntity.isVerifiedTerm(), foundUser.get().isVerifiedTerm());
        assertEquals(userEntity.getPassword(), foundUser.get().getPassword());
    }

    @Test
    void findById_notFound() {
        Optional<UserEntity> foundUser = ormRegister.findById(999L); // Assuming 999L is an ID that does not exist
        assertTrue(foundUser.isEmpty());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void deleteById_success() {

        entityManager.persistAndFlush(userEntity);

        ormRegister.deleteById(userEntity.getId());

        entityManager.flush();

        Optional<UserEntity> foundUser = ormRegister.findById(userEntity.getId());
        assertTrue(foundUser.isEmpty());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void deleteById_notFound() {
        ormRegister.deleteById(999L); // Assuming 999L is an ID that does not exist
        entityManager.flush(); // se pone para asegurar que la operación se complete, osea, que se intente eliminar un usuario que no existe
        // Verify that no user with ID 999L exists
        Optional<UserEntity> foundUser = ormRegister.findById(999L);
        assertTrue(foundUser.isEmpty());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findAll_success() {
        entityManager.persistAndFlush(userEntity);

        List<UserEntity> allUsers = ormRegister.findAll();

        assertNotNull(allUsers);
        assertTrue(allUsers.iterator().hasNext());
        assertEquals(1, allUsers.size(), "La lista de usuarios debería contener un usuario");

        UserEntity foundUser = allUsers.iterator().next();
        assertEquals(userEntity.getEmail(), foundUser.getEmail());
        assertEquals(userEntity.getName(), foundUser.getName());
        assertEquals(userEntity.getLastName(), foundUser.getLastName());
        assertEquals(userEntity.getPhone(), foundUser.getPhone());
        assertEquals(userEntity.getRol(), foundUser.getRol());
        assertEquals(userEntity.isVerifiedCode(), foundUser.isVerifiedCode());
        assertEquals(userEntity.isVerifiedTerm(), foundUser.isVerifiedTerm());
        assertEquals(userEntity.getPassword(), foundUser.getPassword());
    }

    @Test
    void findAll_EmptyDatabase_ReturnsEmptyList() {
        List<UserEntity> allUsers = ormRegister.findAll();
        assertNotNull(allUsers); //nunca va a ser null, siempre va a ser una lista vacía
        assertTrue(allUsers.isEmpty());
        assertEquals(0, allUsers.size(), "La lista de usuarios debería estar vacía");
        // Verificar que no hay usuarios en la base de datos
        Optional<UserEntity> foundUser = ormRegister.findById(1L); // Intentamos buscar un usuario con ID 1, que no debería existir
        assertTrue(foundUser.isEmpty(), "No debería haber usuarios en la base de datos");
        assertFalse(foundUser.isPresent(), "No debería haber usuarios en la base de datos");
    }

    @Test
    void multipleUsers_findByEmail_success(){
        UserEntity user2 = UserEntity.builder()
            .name("Maria")
            .lastName("Lopez")
            .email("ana.lopez@gmail.com")
            .password("hashedPassword2")
            .phone("3127147815")
            .rol("ADMIN")
            .verifiedCode(true)
            .verifiedTerm(true)
            .build();

        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(user2);

        Optional<UserEntity> result1 = ormRegister.findByEmail(userEntity.getEmail());
        Optional<UserEntity> result2 = ormRegister.findByEmail(user2.getEmail());

        assertTrue(result1.isPresent());
        assertEquals(userEntity.getEmail(), result1.get().getEmail());
        assertEquals(userEntity.getName(), result1.get().getName());
        assertEquals(userEntity.getLastName(), result1.get().getLastName());
        assertEquals(userEntity.getPhone(), result1.get().getPhone());
        assertEquals(userEntity.getRol(), result1.get().getRol());
        assertEquals(userEntity.isVerifiedCode(), result1.get().isVerifiedCode());
        assertEquals(userEntity.isVerifiedTerm(), result1.get().isVerifiedTerm());
        assertEquals(userEntity.getPassword(), result1.get().getPassword());
        assertTrue(result2.isPresent());
        assertEquals(user2.getEmail(), result2.get().getEmail());
        assertEquals(user2.getName(), result2.get().getName());
        assertEquals(user2.getLastName(), result2.get().getLastName());
        assertEquals(user2.getPhone(), result2.get().getPhone());
        assertEquals(user2.getRol(), result2.get().getRol());
        assertEquals(user2.isVerifiedCode(), result2.get().isVerifiedCode());
        assertEquals(user2.isVerifiedTerm(), result2.get().isVerifiedTerm());
        assertEquals(user2.getPassword(), result2.get().getPassword());
    }

    @Test
    void multipleUsers_findByEmail_notFound() {
        // Arrange (Opcional: Persistir algunos usuarios si quieres asegurarte que no se confunden con los que existen)
        // Aunque para este test, lo crucial es que los emails de búsqueda no existan.
        // Podrías persistir userEntity y user2 si quieres que la base de datos no esté completamente vacía,
        // pero los emails que buscas NO deben ser los de userEntity o user2.
        entityManager.persistAndFlush(userEntity); // Persistimos un usuario existente
        UserEntity user2 = UserEntity.builder()
            .name("Maria")
            .lastName("Lopez")
            .email("ana.lopez@gmail.com")
            .password("hashedPassword2")
            .phone("3127147815")
            .rol("ADMIN")
            .verifiedCode(true)
            .verifiedTerm(true)
            .build();
        entityManager.persistAndFlush(user2); // Persistimos otro usuario existente

        // Act
        Optional<UserEntity> result1 = ormRegister.findByEmail("email.inexistente1@test.com");
        Optional<UserEntity> result2 = ormRegister.findByEmail("email.inexistente2@test.com");
        Optional<UserEntity> result3 = ormRegister.findByEmail("otro.email@noexiste.com");

        // Assert
        // Verificamos que ninguna de las búsquedas encontró un usuario
        assertTrue(result1.isEmpty(), "El primer email no debería existir");
        assertFalse(result1.isPresent()); // Alternativa a isEmpty()

        assertTrue(result2.isEmpty(), "El segundo email no debería existir");
        assertFalse(result2.isPresent());

        assertTrue(result3.isEmpty(), "El tercer email no debería existir");
        assertFalse(result3.isPresent());
    }

    @Test
    void multipleUsers_existsByEmail_success(){
        UserEntity user2 = UserEntity.builder()
            .name("Maria")
            .lastName("Lopez")
            .email("maria.lopez@gmail.com")
            .password("hashedPassword2")
            .phone("3127147815")
            .rol("ADMIN")
            .verifiedCode(true)
            .verifiedTerm(true)
            .build();
        
        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(user2);

        boolean exists1 = ormRegister.existsByEmail(userEntity.getEmail());
        boolean exists2 = ormRegister.existsByEmail(user2.getEmail());
        boolean exists3 = ormRegister.existsByEmail("noexiste@gmail.com");
        // Verificar que los usuarios existen
        assertTrue(exists1);
        assertTrue(exists2);
        assertFalse(exists3);
        assertEquals(true, exists1);
        assertEquals(true, exists2);
        assertEquals(false, exists3);
    }

    @Test
    void deleteAll_MultipleUsers_DeletesAll(){
        UserEntity user2 = UserEntity.builder()
            .name("Maria")
            .lastName("Lopez")
            .email("marialopez@gmail.com")
            .password("hashedPassword2")
            .phone("3127147815")
            .rol("ADMIN")
            .verifiedCode(true)
            .verifiedTerm(true)
            .build();

        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(user2);   

        ormRegister.deleteAll();
        entityManager.flush();

        List<UserEntity> allUsers = ormRegister.findAll();
        assertNotNull(allUsers);
        assertTrue(allUsers.isEmpty(), "La lista de usuarios debería estar vacía después de eliminar todos los usuarios");
        assertEquals(0, allUsers.size(), "El tamaño de la lista de usuarios debería ser 0 después de eliminar todos los usuarios");
        // Verificar que no existen usuarios en la base de datos
        Optional<UserEntity> foundUser1 = ormRegister.findById(userEntity.getId());
        Optional<UserEntity> foundUser2 = ormRegister.findById(user2.getId());
        assertTrue(foundUser1.isEmpty(), "El usuario 1 debería haber sido eliminado");
        assertFalse(foundUser1.isPresent(), "El usuario 1 no debería existir después de eliminar todos los usuarios");
        assertTrue(foundUser2.isEmpty(), "El usuario 2 debería haber sido eliminado");
        assertFalse(foundUser2.isPresent(), "El usuario 2 no debería existir después de eliminar todos los usuarios");
    }

    @Test
    void deleteAll_EmptyDatabase_DoesNotThrow() {
        // Act
        ormRegister.deleteAll();
        entityManager.flush(); // Aseguramos que la operación se complete

        // Assert
        List<UserEntity> allUsers = ormRegister.findAll();
        assertNotNull(allUsers, "La lista de usuarios no debería ser null");
        assertTrue(allUsers.isEmpty(), "La lista de usuarios debería estar vacía después de eliminar todos los usuarios");
        assertEquals(0, allUsers.size(), "El tamaño de la lista de usuarios debería ser 0 después de eliminar todos los usuarios");
    }
}