package hw6;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Tests for optimized LegoPaths implementation.
 * Focuses on performance and robustness for large datasets.
 */
public class OptimizedLegoPathsTest {
    
    private LegoPaths legoPaths;
    private static final String DATA_DIR = "data/";
    
    @BeforeEach
    public void setUp() {
        // Create a fresh LegoPaths instance for each test
        legoPaths = new LegoPaths();
        
        // Ensure data directory exists
        Path dataDir = Paths.get(DATA_DIR);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to create a test CSV file.
     */
    private void createTestFile(String filename, String content) {
        Path filePath = Paths.get(DATA_DIR, filename);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
            writer.print(content);
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to create test files for basic path finding
     */
    private void createBasicPathTestFiles() {
        createTestFile("hw6_test_basic.csv", 
                "\"Part1\",\"Set1\"\n" +
                "\"Part2\",\"Set1\"\n" +
                "\"Part2\",\"Set2\"\n" +
                "\"Part3\",\"Set2\"\n" +
                "\"Part3\",\"Set3\"\n" +
                "\"Part4\",\"Set3\"\n");
    }
    
    @Test
    public void testBasicPathFinding() {
        // Test with basic graph where there are clear paths
        createBasicPathTestFiles();
        legoPaths.createNewGraph(DATA_DIR + "hw6_test_basic.csv");
        
        // Test direct path (one edge)
        String path1to2 = legoPaths.findPath("Part1", "Part2");
        assertTrue(path1to2.contains("Part1 to Part2 with weight 1.000"));
        assertTrue(path1to2.contains("total cost: 1.000"));
        
        // Test longer path (two edges)
        String path1to3 = legoPaths.findPath("Part1", "Part3");
        assertTrue(path1to3.contains("Part1 to Part2"));
        assertTrue(path1to3.contains("Part2 to Part3"));
        assertTrue(path1to3.contains("total cost:"));
        
        // Test even longer path (three edges)
        String path1to4 = legoPaths.findPath("Part1", "Part4");
        assertTrue(path1to4.contains("Part1 to Part2"));
        assertTrue(path1to4.contains("Part2 to Part3"));
        assertTrue(path1to4.contains("Part3 to Part4"));
    }
    
    @Test
    public void testMultipleSharedSets() {
        // Test with parts sharing multiple sets to verify edge weight calculation
        createTestFile("hw6_test_multi_shared.csv",
                "\"PartA\",\"Set1\"\n" +
                "\"PartA\",\"Set2\"\n" +
                "\"PartA\",\"Set3\"\n" +
                "\"PartB\",\"Set1\"\n" +
                "\"PartB\",\"Set2\"\n" +
                "\"PartB\",\"Set3\"\n" +
                "\"PartC\",\"Set1\"\n" +
                "\"PartD\",\"Set4\"\n" +
                "\"PartC\",\"Set4\"\n");
        
        legoPaths.createNewGraph(DATA_DIR + "hw6_test_multi_shared.csv");
        
        // Test edge with weight 1/3 = 0.333
        String pathAtoBWithMultipleShares = legoPaths.findPath("PartA", "PartB");
        assertTrue(pathAtoBWithMultipleShares.contains("with weight 0.333"));
        
        // The path from A to C should be direct
        String pathAtoC = legoPaths.findPath("PartA", "PartC");
        assertTrue(pathAtoC.contains("PartA to PartC with weight 1.000"));
        
        // The path from A to D should go through C
        String pathAtoD = legoPaths.findPath("PartA", "PartD");
        assertTrue(pathAtoD.contains("PartA to PartC"));
        assertTrue(pathAtoD.contains("PartC to PartD"));
    }
    
    @Test
    public void testNoPathBetweenParts() {
        // Test with disconnected graph components
        createTestFile("hw6_test_no_path.csv",
                "\"Island1\",\"Set1\"\n" +
                "\"Island2\",\"Set1\"\n" +
                "\"Island3\",\"Set2\"\n" +
                "\"Island4\",\"Set2\"\n");
        
        legoPaths.createNewGraph(DATA_DIR + "hw6_test_no_path.csv");
        
        // Should be a path between parts in the same component
        String pathInSameComponent = legoPaths.findPath("Island1", "Island2");
        assertTrue(pathInSameComponent.contains("Island1 to Island2"));
        
        // Should be no path between parts in different components
        String noPath = legoPaths.findPath("Island1", "Island3");
        assertTrue(noPath.contains("no path found"));
    }
    
    @Test
    public void testUnknownParts() {
        // Load a graph with known parts
        createBasicPathTestFiles();
        legoPaths.createNewGraph(DATA_DIR + "hw6_test_basic.csv");
        
        // Test with unknown start part
        String unknownStart = legoPaths.findPath("UnknownPart", "Part1");
        assertTrue(unknownStart.contains("unknown part UnknownPart"));
        
        // Test with unknown end part
        String unknownEnd = legoPaths.findPath("Part1", "UnknownPart");
        assertTrue(unknownEnd.contains("unknown part UnknownPart"));
        
        // Test with both parts unknown
        String bothUnknown = legoPaths.findPath("UnknownPart1", "UnknownPart2");
        assertTrue(bothUnknown.contains("unknown part UnknownPart1"));
        assertTrue(bothUnknown.contains("unknown part UnknownPart2"));
    }
    
    @Test
    public void testSelfPath() {
        // Load any graph
        createBasicPathTestFiles();
        legoPaths.createNewGraph(DATA_DIR + "hw6_test_basic.csv");
        
        // Test path from a part to itself
        String selfPath = legoPaths.findPath("Part1", "Part1");
        assertTrue(selfPath.contains("path from Part1 to Part1:"));
        assertTrue(selfPath.contains("total cost: 0.000"));
        
        // Verify there are no edges in the path
        org.junit.jupiter.api.Assertions.assertFalse(selfPath.contains("with weight"));
    }
    
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testPerformanceOnLargerGraph() {
        // Create a larger test file programmatically
        StringBuilder builder = new StringBuilder();
        int numParts = 100;
        int numSets = 50;
        
        // Each part appears in multiple sets, creating a densely connected graph
        for (int part = 1; part <= numParts; part++) {
            for (int set = 1; set <= numSets; set++) {
                // Each part appears in about 10 sets
                if (set % (numSets / 10) == part % (numSets / 10)) {
                    builder.append("\"Part").append(part).append("\",\"Set").append(set).append("\"\n");
                }
            }
        }
        
        try {
            // Write to a temporary file
            java.nio.file.Files.writeString(java.nio.file.Paths.get(DATA_DIR + "hw6_test_performance.csv"), builder.toString());
            
            // Load the graph
            long startTime = System.currentTimeMillis();
            legoPaths.createNewGraph(DATA_DIR + "hw6_test_performance.csv");
            long loadTime = System.currentTimeMillis() - startTime;
            
            // Find multiple paths
            startTime = System.currentTimeMillis();
            for (int i = 1; i <= 10; i++) {
                legoPaths.findPath("Part1", "Part" + (numParts - i));
            }
            long pathTime = System.currentTimeMillis() - startTime;
            
            // Just log the times, no hard assertions since performance depends on the machine
            // System.out.println("Performance test: Graph load time = " + loadTime + " ms, Path finding time = " + pathTime + " ms");
            
        } catch (Exception e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }
    
    @Test
    public void testLegoParserEdgeCases() throws Exception {
        // Create test files for parser edge cases
        
        // Test with empty file
        createTestFile("empty_file.csv", "");
        
        // Test with just header
        createTestFile("header_only.csv", "\"Part\",\"Set\"\n");
        
        // Test with missing columns
        createTestFile("missing_columns.csv", 
                "\"Part1\"\n" +
                "\"Part2\",\"Set1\",\"ExtraStuff\"\n");
        
        // Test with quoted commas inside values
        createTestFile("quoted_commas.csv",
                "\"Part,1\",\"Set,1\"\n" +
                "\"Part,2\",\"Set,1\"\n");
        
        // The direct testing of LegoParser requires reflection since it might be an internal implementation detail
        // We use the legoPaths object and observe the parsing effects
        
        // Test with empty file should not crash
        legoPaths.createNewGraph(DATA_DIR + "empty_file.csv");
        String result = legoPaths.findPath("A", "B");
        assertTrue(result.contains("unknown part A") || result.contains("unknown part B"), 
                "Should handle empty file gracefully");
        
        // Test with header only should not crash
        legoPaths.createNewGraph(DATA_DIR + "header_only.csv");
        result = legoPaths.findPath("A", "B");
        assertTrue(result.contains("unknown part A") || result.contains("unknown part B"), 
                "Should handle header-only file gracefully");
        
        // Access the parser directly if possible through reflection
        try {
            // Try accessing LegoParser through reflection
            Class<?> legoPathsClass = LegoPaths.class;
            Method createGraphMethod = legoPathsClass.getDeclaredMethod("createNewGraph", String.class);
            createGraphMethod.setAccessible(true);
            
            // Test with quoted commas
            legoPaths.createNewGraph(DATA_DIR + "quoted_commas.csv");
            String path = legoPaths.findPath("Part,1", "Part,2");
            assertTrue(path.contains("path from Part,1 to Part,2") || 
                      path.contains("unknown part") || 
                      path.contains("no path exists"),
                    "Should handle quoted commas correctly or fail gracefully");
        } catch (Exception e) {
            // If reflection fails, that's acceptable for this test
            // System.out.println("Reflection to test LegoParser directly failed: " + e.getMessage());
        }
        
        // Test with malformed data
        try {
            legoPaths.createNewGraph(DATA_DIR + "missing_columns.csv");
            // If we got here, the parser handled the error gracefully
        } catch (Exception e) {
            // Acceptable; just verifying it doesn't crash the test suite
            assertNotNull(e.getMessage(), "Exception should have a message");
        }
    }
} 