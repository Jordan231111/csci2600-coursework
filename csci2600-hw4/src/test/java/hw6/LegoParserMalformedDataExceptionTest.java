package hw6;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 * Dedicated test class for the LegoParser.MalformedDataException inner class.
 * This ensures 100% code coverage for this exception class.
 */
public class LegoParserMalformedDataExceptionTest {
    
    @Test
    public void testConstructorWithMessage() {
        String errorMessage = "Test error message";
        LegoParser.MalformedDataException exception = new LegoParser.MalformedDataException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage(), "Exception message should match the provided message");
        assertNotNull(exception, "Exception should not be null");
    }
    
    @Test
    public void testConstructorWithMessageAndCause() {
        String errorMessage = "Test error with cause";
        Exception cause = new IOException("The IO cause");
        
        LegoParser.MalformedDataException exception = new LegoParser.MalformedDataException(errorMessage, cause);
        
        assertEquals(errorMessage, exception.getMessage(), "Exception message should match the provided message");
        assertEquals(cause, exception.getCause(), "Exception cause should match the provided cause");
        assertEquals("The IO cause", exception.getCause().getMessage(), "Cause message should match");
    }
    
    /**
     * Test additional exception functionality to ensure complete coverage.
     */
    @Test
    public void testExceptionUsage() {
        LegoParser.MalformedDataException ex1 = new LegoParser.MalformedDataException("Test inner");
        LegoParser.MalformedDataException ex2 = new LegoParser.MalformedDataException("Inner with cause", new IOException());
        
        // Ensure we can use these exceptions in a try-catch block
        try {
            throw ex1;
        } catch (IOException e) {
            assertEquals("Test inner", e.getMessage());
        }
        
        try {
            throw ex2;
        } catch (IOException e) {
            assertEquals("Inner with cause", e.getMessage());
            assertNotNull(e.getCause());
        }
    }
} 