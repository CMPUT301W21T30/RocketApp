package com.example.rocketapp;
import com.example.rocketapp.helpers.Validate;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ValidateUnitTest {

    @Test
    public void testFloatInRange() {
        assertFalse(Validate.floatInRange("string", 0.5f, 8.4f));
        assertFalse(Validate.floatInRange("0.45", 0.5f, 8.4f));
        assertFalse(Validate.floatInRange("8.45", 0.5f, 8.4f));

        assertTrue(Validate.floatInRange("0.5", 0.5f, 8.4f));
        assertTrue(Validate.floatInRange("8.4", 0.5f, 8.4f));
        assertTrue(Validate.floatInRange("5", 0.5f, 8.4f));
    }

    @Test
    public void testIntInRange() {
        assertFalse(Validate.intInRange("string", 5, 12));
        assertFalse(Validate.intInRange("5.5", 5, 12));
        assertFalse(Validate.intInRange("4", 5, 12));
        assertFalse(Validate.intInRange("13", 5, 12));

        assertTrue(Validate.intInRange("5", 5, 12));
        assertTrue(Validate.intInRange("12", 5, 12));
        assertTrue(Validate.intInRange("8", 5, 12));
    }

    @Test
    public void testLengthRange() {
        assertFalse(Validate.lengthInRange("they", 5, 12));
        assertFalse(Validate.lengthInRange("hello world", 5, 10));

        assertTrue(Validate.lengthInRange("hello", 5, 10));
        assertTrue(Validate.lengthInRange("helloworld", 5, 10));
    }

}
