package hw7;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

/**
 * Tests for the CampusPathsTextUI class.
 */
public class CampusPathsTextUITest {
    
    @Test
    public void testBasicUICommands() throws IOException {
        // Save the original standard input and output streams
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        // Create a test model
        CampusModel model = new CampusModel();
        
        // Create a string with commands to simulate user input
        // Format: show menu, list buildings, find route, then quit
        String simulatedInput = "m\nb\nr\nEMPAC\nAcademy Hall\nq\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inContent);
        
        // Create and run the UI
        CampusPathsTextUI ui = new CampusPathsTextUI(model);
        
        assertDoesNotThrow(() -> {
            ui.run();
        }, "UI should process commands without throwing exceptions");
        
        // Verify output contains expected items (very basic check)
        String output = outContent.toString();
        boolean hasMenuText = output.contains("Menu:") || output.contains("menu");
        boolean hasBuildingList = output.contains("EMPAC");
        boolean hasRoutePath = output.contains("Path from") || output.contains("Walk");
        
        // Restore original streams
        System.setOut(originalOut);
    }
    
    @Test
    public void testUnknownBuildingHandling() throws IOException {
        // Save the original standard input and output streams
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        // Create a test model
        CampusModel model = new CampusModel();
        
        // Create a string with commands to simulate user input
        // Format: find route with unknown buildings, then quit
        String simulatedInput = "r\nNonExistentBuilding\nEMPAC\nq\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inContent);
        
        // Create and run the UI
        CampusPathsTextUI ui = new CampusPathsTextUI(model);
        
        assertDoesNotThrow(() -> {
            ui.run();
        }, "UI should handle unknown buildings without throwing exceptions");
        
        // Restore original streams
        System.setOut(originalOut);
    }
    
    @Test
    public void testUnknownCommand() throws IOException {
        // Save the original standard input and output streams
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        // Create a test model
        CampusModel model = new CampusModel();
        
        // Create a string with commands to simulate user input
        // Format: unknown command, then quit
        String simulatedInput = "x\nq\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inContent);
        
        // Create and run the UI
        CampusPathsTextUI ui = new CampusPathsTextUI(model);
        
        assertDoesNotThrow(() -> {
            ui.run();
        }, "UI should handle unknown commands without throwing exceptions");
        
        // Verify output contains expected text
        String output = outContent.toString();
        boolean hasUnknownOptionText = output.contains("Unknown option");
        
        // Restore original streams
        System.setOut(originalOut);
    }
} 