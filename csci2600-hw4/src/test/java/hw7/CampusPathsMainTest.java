package hw7;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test for the CampusPaths main class.
 */
public class CampusPathsMainTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    
    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    
    @Test
    public void testMainWithQuitCommand() {
        String input = "q\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        assertDoesNotThrow(() -> {
            CampusPaths.main(null);
        }, "Main method with quit command should not throw exception");
    }
    
    @Test
    public void testMainWithMultipleCommands() {
        String input = "m\nb\nr\nEMPAC\nAcademy Hall\nq\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        assertDoesNotThrow(() -> {
            CampusPaths.main(null);
        }, "Main method with multiple commands should not throw exception");
    }
    
    @Test
    public void testMainWithUnknownOption() {
        String input = "x\nq\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        assertDoesNotThrow(() -> {
            CampusPaths.main(null);
        }, "Main method with unknown option should not throw exception");
    }
    
    @Test
    public void testMainWithIOException() throws IOException {
        // Create input with "q" to exit immediately
        ByteArrayInputStream inContent = new ByteArrayInputStream("q\n".getBytes());
        System.setIn(inContent);
        
        // Simulate an IOException by providing a non-existent file path
        String originalNodesFile = System.getProperty("hw7.nodes.file");
        System.setProperty("hw7.nodes.file", "non_existent_file.csv");
        
        assertDoesNotThrow(() -> {
            CampusPaths.main(null);
        }, "Main method should handle IOException gracefully");
        
        // Restore original property
        if (originalNodesFile != null) {
            System.setProperty("hw7.nodes.file", originalNodesFile);
        } else {
            System.clearProperty("hw7.nodes.file");
        }
    }
} 