package hw6;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the LegoPaths class that focus on the core functionality.
 */
public class LegoPathsTest {

    private static final String DATA_DIR = "data/";
    private LegoPaths legoPaths;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        legoPaths = new LegoPaths();
        System.setOut(new PrintStream(outContent));
        
        // Ensure data directory exists
        Path dataDir = Paths.get(DATA_DIR);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }
    
    /**
     * Helper method to create a test file.
     */
    private void createTestFile(String filename, String content) {
        Path filePath = Paths.get(DATA_DIR, filename);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
            writer.print(content);
        } catch (IOException e) {
            System.err.println("Failed to create test file: " + e.getMessage());
        }
    }

    /**
     * Test basic functionality: create graph and find path.
     */
    @Test
    public void testBasicFunctionality() {
        // Create a test file
        createTestFile("test_basic.csv", 
                "\"Part\",\"Set\"\n" +
                "\"Part1\",\"Set1\"\n" +
                "\"Part2\",\"Set1\"\n" +
                "\"Part2\",\"Set2\"\n" +
                "\"Part3\",\"Set2\"\n");
        
        // Load the graph
        legoPaths.createNewGraph(DATA_DIR + "test_basic.csv");
        
        // Verify the graph was loaded correctly
        Set<String> allParts = legoPaths.getAllParts();
        // Verify the expected parts are in the set, without checking exact count
        assertTrue(allParts.contains("Part1"));
        assertTrue(allParts.contains("Part2"));
        assertTrue(allParts.contains("Part3"));
        
        // Find a path
        String path = legoPaths.findPath("Part1", "Part3");
        
        // Check if path contains expected information
        assertTrue(path.contains("path from Part1 to Part3"));
        assertTrue(path.contains("Part1 to Part2"));
        assertTrue(path.contains("Part2 to Part3"));
        assertTrue(path.contains("total cost:"));
    }

    /**
     * Test handling multiple set sharing between parts.
     */
    @Test
    public void testMultipleSetSharing() {
        // Create a test file with parts sharing multiple sets
        createTestFile("test_multi_shared.csv", 
                "\"Part\",\"Set\"\n" +
                "\"Part1\",\"Set1\"\n" +
                "\"Part1\",\"Set2\"\n" +
                "\"Part2\",\"Set1\"\n" +
                "\"Part2\",\"Set2\"\n" +
                "\"Part2\",\"Set3\"\n" +
                "\"Part3\",\"Set2\"\n" +
                "\"Part3\",\"Set3\"\n");
        
        // Load the graph
        legoPaths.createNewGraph(DATA_DIR + "test_multi_shared.csv");
        
        // Find a path that should have a weight based on multiple shared sets
        String path = legoPaths.findPath("Part1", "Part2");
        
        // Verify the path contains expected information
        assertTrue(path.contains("path from Part1 to Part2"));
        assertTrue(path.contains("weight"));
        
        // Parts sharing 2 sets should have a weight of 0.5
        // The exact format of the output isn't specified, so we check for presence of 0.5
        assertTrue(path.contains("0.5"), "Weight should be 0.5 for parts sharing 2 sets");
    }

    /**
     * Test handling null inputs.
     */
    @Test
    public void testNullInputs() {
        // Create a simple test file
        createTestFile("test_null.csv", 
                "\"Part\",\"Set\"\n" +
                "\"Part1\",\"Set1\"\n" +
                "\"Part2\",\"Set1\"\n");
        
        // Load the graph
        legoPaths.createNewGraph(DATA_DIR + "test_null.csv");
        
        // Test with null filename
        assertThrows(NullPointerException.class, () -> {
            legoPaths.createNewGraph(null);
        });
        
        // Test with null part names
        assertThrows(NullPointerException.class, () -> {
            legoPaths.findPath(null, "Part2");
        });
        
        assertThrows(NullPointerException.class, () -> {
            legoPaths.findPath("Part1", null);
        });
    }

    /**
     * Test handling unknown part names.
     */
    @Test
    public void testUnknownParts() {
        // Create a simple test file
        createTestFile("test_unknown.csv", 
                "\"Part\",\"Set\"\n" +
                "\"Part1\",\"Set1\"\n" +
                "\"Part2\",\"Set1\"\n");
        
        // Load the graph
        legoPaths.createNewGraph(DATA_DIR + "test_unknown.csv");
        
        // Test with unknown part names
        String path = legoPaths.findPath("UnknownPart1", "Part2");
        assertTrue(path.contains("unknown part UnknownPart1"));
        
        path = legoPaths.findPath("Part1", "UnknownPart2");
        assertTrue(path.contains("unknown part UnknownPart2"));
        
        path = legoPaths.findPath("UnknownPart1", "UnknownPart2");
        assertTrue(path.contains("unknown part UnknownPart1"));
    }

    /**
     * Test finding a path between parts that don't have a connection.
     */
    @Test
    public void testNoPath() {
        // Create a test file with disconnected parts
        createTestFile("test_no_path.csv", 
                "\"Part\",\"Set\"\n" +
                "\"Part1\",\"Set1\"\n" +
                "\"Part2\",\"Set1\"\n" +
                "\"Part3\",\"Set2\"\n" +
                "\"Part4\",\"Set2\"\n");
        
        // Load the graph
        legoPaths.createNewGraph(DATA_DIR + "test_no_path.csv");
        
        // Find a path between disconnected parts
        String path = legoPaths.findPath("Part1", "Part3");
        
        // Verify that no path is found
        assertTrue(path.contains("no path found"), "Should indicate that no path was found");
    }
}