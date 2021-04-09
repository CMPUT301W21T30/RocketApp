package com.example.rocketapp;
import com.example.rocketapp.model.users.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class UserUnitTest {
    private User user;

    public UserUnitTest() {
        user = createMockUser();
    }

    private User createMockUser() {
        User user = new User("Marty");
        user.setEmail("marty@gmail.com");
        user.setPhoneNumber("123-456-7890");
        return user;
    }

    @Test
    public void checkSetEmail() {
        assertEquals(user.getEmail(), "marty@gmail.com");
        user.setEmail("marty63@gmail.com");
        assertEquals(user.getEmail(), "marty63@gmail.com");
    }

    @Test
    public void checkSetName() {
        user.setName("Morty");
        assertEquals(user.getName(), "Morty");
    }

    @Test
    public void checkSetPhoneNumber() {
        user.setPhoneNumber("1234567890");
        assertEquals(user.getPhoneNumber(), "1234567890");
    }

    @Test
    public void checkIsValid() {
        assertFalse(user.isValid());
    }

}
