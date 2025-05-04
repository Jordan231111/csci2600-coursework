package hw6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 * Dedicated test class for the MalformedDataException class.
 * This ensures 100% code coverage for this exception class.
 */
public class MalformedDataExceptionTest {
    
    @Test
    public void testConstructorWithMessage() {
        String errorMessage = "Test error message";
        MalformedDataException exception = new MalformedDataException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage(), "Exception message should match the provided message");
        assertNotNull(exception, "Exception should not be null");
    }
    
    @Test
    public void testConstructorWithMessageAndCause() {
        String errorMessage = "Test error with cause";
        Exception cause = new RuntimeException("The root cause");
        
        MalformedDataException exception = new MalformedDataException(errorMessage, cause);
        
        assertEquals(errorMessage, exception.getMessage(), "Exception message should match the provided message");
        assertEquals(cause, exception.getCause(), "Exception cause should match the provided cause");
        assertEquals("The root cause", exception.getCause().getMessage(), "Cause message should match");
    }
    
    /**
     * Test additional exception functionality to ensure complete coverage.
     */
    @Test
    public void testExceptionUsage() {
        MalformedDataException ex1 = new MalformedDataException("Test");
        MalformedDataException ex2 = new MalformedDataException("Test with cause", new IllegalArgumentException());
        
        // Ensure we can use these exceptions in a try-catch block
        try {
            throw ex1;
        } catch (MalformedDataException e) {
            assertEquals("Test", e.getMessage());
        }
        
        try {
            throw ex2;
        } catch (RuntimeException e) {
            assertEquals("Test with cause", e.getMessage());
            assertNotNull(e.getCause());
        }
    }
} 