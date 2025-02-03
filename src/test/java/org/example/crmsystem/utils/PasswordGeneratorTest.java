package org.example.crmsystem.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private static PasswordGenerator passwordGenerator;

    @BeforeAll
    static void setUpBeforeClass() {
        passwordGenerator = new PasswordGenerator();
    }

    @Test
    void testGenerateUserPassword_shouldGeneratePasswordWithCorrectLength() {
        String password = passwordGenerator.generateUserPassword();

        assertNotNull(password, "Password should not be null");
        assertEquals(10, password.length(), "Password length should be 10");
    }

    @RepeatedTest(10)
    void testGenerateUserPassword_shouldGenerateDifferentPasswords() {
        String password1 = passwordGenerator.generateUserPassword();
        String password2 = passwordGenerator.generateUserPassword();

        assertNotNull(password1);
        assertNotNull(password2);
        assertNotEquals(password1, password2, "Passwords should be different");
    }

    @RepeatedTest(10)
    void testGenerateUserPassword_shouldContainOnlyValidCharacters() {
        String password = passwordGenerator.generateUserPassword();
        assertTrue(password.matches("[A-Za-z0-9./*$%^!@#~]+"), "Password should contain only valid characters");
    }

    @Test
    void testGenerateUserPassword_shouldHaveHighProbabilityOfUniqueness() {
        Set<String> generatedPasswords = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            String password = passwordGenerator.generateUserPassword();
            assertTrue(generatedPasswords.add(password), "Generated password should be unique");
        }
    }
}
