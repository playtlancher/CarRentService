package com.venediktov.carservice.repositories;

import com.venediktov.carservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_Found() {
        User user = new User();
        user.setUsername("bob");
        user.setPassword("secret");
        user.setEmail("bob@example.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("bob");
        assertTrue(found.isPresent());
        assertEquals("bob@example.com", found.get().getEmail());
    }

    @Test
    void findByUsername_NotFound() {
        Optional<User> found = userRepository.findByUsername("ghost");
        assertFalse(found.isPresent());
    }
}
