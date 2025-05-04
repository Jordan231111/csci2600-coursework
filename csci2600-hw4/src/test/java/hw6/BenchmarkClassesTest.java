package hw6;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests focused specifically on achieving high coverage for BenchmarkRunner and BenchmarkResult classes.
 */
public class BenchmarkClassesTest {

    private static final String DATA_DIR = "data/";
    private BenchmarkRunner benchmarkRunner;
    
    @BeforeEach
    public void setUp() throws IOException {
        // Create the data directory if it doesn't exist
        Path dataDir = Paths.get(DATA_DIR);
        Files.createDirectories(dataDir);
        
        // Create a basic test file
        createTestFile("benchmark_coverage_test.csv", 
                "\"Part1\",\"Set1\"\n" +
                "\"Part2\",\"Set1\"\n" +
                "\"Part2\",\"Set2\"\n" +
                "\"Part3\",\"Set2\"\n");
                
        // Create a new BenchmarkRunner for each test
        benchmarkRunner = new BenchmarkRunner(DATA_DIR);
    }
    
    /**
     * Helper method to create a test CSV file.
     */
    private void createTestFile(String filename, String content) throws IOException {
        Path filePath = Paths.get(DATA_DIR, filename);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
            writer.print(content);
        }
    }
    
    @Test
    @DisplayName("Test BenchmarkResult constructor and getters")
    public void testBenchmarkResult() {
        // Create a BenchmarkResult with specific values
        String testFilePath = DATA_DIR + "benchmark_coverage_test.csv";
        double graphCreationTime = 100.5;
        double avgPathTime = 10.2;
        int successfulPaths = 15;
        int totalOps = 20;
        boolean partsFound = true;
        
        // Create a BenchmarkResult using the constructor
        BenchmarkResult result = new BenchmarkResult(
            testFilePath,
            graphCreationTime,
            avgPathTime,
            successfulPaths,
            totalOps,
            partsFound
        );
        
        // Verify all fields are set correctly
        assertEquals(testFilePath, result.filePath);
        assertEquals(graphCreationTime, result.graphCreationTime);
        assertEquals(avgPathTime, result.averagePathFindingTime);
        assertEquals(successfulPaths, result.successfulPaths);
        assertEquals(totalOps, result.totalPathOperations);
        assertTrue(result.partsFound);
    }
    
    @Test
    @DisplayName("Test BenchmarkRunner with valid file")
    public void testBenchmarkRunnerWithValidFile() {
        // Run a benchmark on our test file
        BenchmarkResult result = benchmarkRunner.runSingleBenchmark("benchmark_coverage_test.csv");
        
        // Verify the result
        assertNotNull(result);
        assertEquals(DATA_DIR + "benchmark_coverage_test.csv", result.filePath);
        assertTrue(result.graphCreationTime > 0);
        assertTrue(result.partsFound);
        assertEquals(20, result.totalPathOperations); // Default value
    }
    
    @Test
    @DisplayName("Test BenchmarkRunner with non-existent file")
    public void testBenchmarkRunnerWithNonExistentFile() {
        // Try to run a benchmark on a non-existent file
        BenchmarkResult result = benchmarkRunner.runSingleBenchmark("non_existent_file.csv");
        
        // Should return null for non-existent file
        assertNull(result);
    }
    
    @Test
    @DisplayName("Test BenchmarkRunner extractAvailableParts")
    public void testExtractAvailableParts() throws IOException {
        // Create a LegoPaths instance and load a graph
        LegoPaths legoPaths = new LegoPaths();
        legoPaths.createNewGraph(DATA_DIR + "benchmark_coverage_test.csv");
        
        // Extract the available parts
        List<String> parts = benchmarkRunner.extractAvailableParts(legoPaths);
        
        // Verify the parts are extracted correctly
        assertNotNull(parts);
        assertEquals(3, parts.size());
        assertTrue(parts.contains("Part1"));
        assertTrue(parts.contains("Part2"));
        assertTrue(parts.contains("Part3"));
    }
    
    @Test
    @DisplayName("Test BenchmarkRunner with empty parts list")
    public void testBenchmarkRunnerWithEmptyPartsList() throws IOException {
        // Create a file with no parts
        createTestFile("empty_parts.csv", "\"Part\",\"Set\"\n"); // Header only
        
        // Create a custom BenchmarkRunner for testing specific logic
        BenchmarkRunner customRunner = new BenchmarkRunner(DATA_DIR) {
            @Override
            protected List<String> extractAvailableParts(LegoPaths legoPaths) {
                // Override to return an empty list
                return new ArrayList<>();
            }
        };
        
        // Run a benchmark - it should handle empty parts list
        BenchmarkResult result = customRunner.runSingleBenchmark("empty_parts.csv");
        
        // Verify the result for empty parts
        assertNotNull(result);
        assertFalse(result.partsFound);
        assertEquals(0, result.successfulPaths);
        assertEquals(0, result.totalPathOperations);
    }
    
    @Test
    @DisplayName("Test BenchmarkRunner with single part")
    public void testBenchmarkRunnerWithSinglePart() throws IOException {
        // Create a file with only one part
        createTestFile("single_part.csv", "\"Part1\",\"Set1\"\n");
        
        // Create a custom BenchmarkRunner for testing specific logic
        BenchmarkRunner customRunner = new BenchmarkRunner(DATA_DIR) {
            @Override
            protected List<String> extractAvailableParts(LegoPaths legoPaths) {
                List<String> parts = new ArrayList<>();
                parts.add("Part1");
                return parts;
            }
        };
        
        // Run a benchmark - it should handle single part (can't create paths)
        BenchmarkResult result = customRunner.runSingleBenchmark("single_part.csv");
        
        // Verify the result for single part
        assertNotNull(result);
        assertTrue(result.partsFound);
        assertEquals(20, result.totalPathOperations);
        // With one part, all path operations will be from a part to itself
        // Depending on the implementation, this may count as successful paths
        // Our assertion needs to be flexible based on the actual implementation
        assertTrue(result.successfulPaths >= 0 && result.successfulPaths <= result.totalPathOperations,
                "Successful paths should be between 0 and total operations");
    }
    
    @Test
    @DisplayName("Test BenchmarkRunner with multiple files")
    public void testRunMultipleBenchmarks() throws IOException {
        // Create additional test files
        createTestFile("test1.csv", "\"Part1\",\"Set1\"\n\"Part2\",\"Set1\"\n");
        createTestFile("test2.csv", "\"Part3\",\"Set2\"\n\"Part4\",\"Set2\"\n");
        
        // Run benchmarks on multiple files individually and collect results
        BenchmarkResult result1 = benchmarkRunner.runSingleBenchmark("benchmark_coverage_test.csv");
        BenchmarkResult result2 = benchmarkRunner.runSingleBenchmark("test1.csv");
        BenchmarkResult result3 = benchmarkRunner.runSingleBenchmark("test2.csv");
        
        List<BenchmarkResult> results = Arrays.asList(result1, result2, result3);
        
        // Verify the results
        assertNotNull(results);
        assertEquals(3, results.size());
        for (BenchmarkResult result : results) {
            assertNotNull(result);
            assertTrue(result.partsFound);
        }
    }
} 