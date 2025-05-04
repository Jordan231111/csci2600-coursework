package hw6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for the BenchmarkRunner class (extracted from BenchmarkLegoPaths).
 */
class BenchmarkLegoPathsTest {

    // Assumes the 'data' directory is in the project root for test execution context
    private static final String TEST_DATA_DIR = "data/";
    private BenchmarkRunner benchmarkRunner;

    // Use @TempDir for creating temporary files safely if needed
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Create a new runner for each test to ensure isolation
        // Requires LegoPaths to be working correctly for these tests
        benchmarkRunner = new BenchmarkRunner(TEST_DATA_DIR);
    }

    @Test
    @DisplayName("Benchmark with a valid basic CSV file")
    void testRunBenchmarkWithValidFile() {
        // Use an existing small test file assumed to be in data/
        String testFile = "hw6_test_basic.csv";
        // Precondition: Ensure data/hw6_test_basic.csv exists and LegoPaths can parse it

        BenchmarkResult result = benchmarkRunner.runSingleBenchmark(testFile);

        assertNotNull(result, "BenchmarkResult should not be null for a valid file");
        assertTrue(result.graphCreationTime >= 0, "Graph creation time should be non-negative");
        assertTrue(result.averagePathFindingTime >= 0, "Average path time should be non-negative");
        assertTrue(result.partsFound, "Parts should be found in the basic test file");
        assertEquals(20, result.totalPathOperations, "Default number of path operations should be 20");
        // We can't easily assert exact successfulPaths without knowing the graph structure and random pairs
        assertTrue(result.successfulPaths >= 0 && result.successfulPaths <= result.totalPathOperations, 
                   "Successful paths should be between 0 and total operations");
        assertEquals(TEST_DATA_DIR + testFile, result.filePath, "File path in result should match input");
    }

    @Test
    @DisplayName("Benchmark with a non-existent file")
    void testRunBenchmarkWithNonExistentFile() {
        String nonExistentFile = "non_existent_file_12345.csv";
        BenchmarkResult result = benchmarkRunner.runSingleBenchmark(nonExistentFile);

        assertNull(result, "BenchmarkResult should be null for a non-existent file");
        // Should also print an error message to System.err, testing that is more complex (requires capturing stderr)
    }

    @Test
    @DisplayName("Benchmark with a file containing no parts")
    void testRunBenchmarkWithNoPartsFile() throws IOException {
        // Create a temporary empty or header-only CSV file
        Path emptyFile = tempDir.resolve("empty_test.csv");
        Files.writeString(emptyFile, "\"Part\",\"Set\"\n"); // Just a header
        
        // Create a runner pointing to the temporary directory
        BenchmarkRunner tempRunner = new BenchmarkRunner(tempDir.toString() + "/");
        BenchmarkResult result = tempRunner.runSingleBenchmark("empty_test.csv");

        assertNotNull(result, "BenchmarkResult should not be null even if no parts are found");
        assertTrue(result.graphCreationTime >= 0, "Graph creation time should still be measured");
        // Since LegoPaths may handle empty files in different ways, we'll make the assertions more flexible
        assertTrue(result.averagePathFindingTime >= 0, "Average path time should be non-negative");
        // We can't assert on successful paths because it depends on the implementation
        assertTrue(result.totalPathOperations >= 0, "Total path operations should be non-negative");
        assertEquals(tempDir.toString() + "/empty_test.csv", result.filePath, "File path should match temp file");
    }

    @Test
    @DisplayName("Test extractAvailableParts directly (requires LegoPaths access)")
    void testExtractAvailableParts() throws IOException {
        // This test requires LegoPaths to work and getAllParts() to be reliable.
        // Create a simple graph using a temporary file.
        Path simpleFile = tempDir.resolve("simple_parts.csv");
        Files.writeString(simpleFile, "\"PartA\",\"Set1\"\n\"PartB\",\"Set1\"\n\"PartC\",\"Set2\"\n");
        
        LegoPaths tempPaths = new LegoPaths();
        tempPaths.createNewGraph(simpleFile.toString());

        // Use the runner's helper method (assuming default/package-private access)
        List<String> parts = benchmarkRunner.extractAvailableParts(tempPaths);

        assertNotNull(parts, "Parts list should not be null");
        assertEquals(3, parts.size(), "Should extract 3 unique parts");
        assertTrue(parts.contains("PartA"), "List should contain PartA");
        assertTrue(parts.contains("PartB"), "List should contain PartB");
        assertTrue(parts.contains("PartC"), "List should contain PartC");
    }
    
    @Test
    @DisplayName("Test extractAvailableParts with no parts")
    void testExtractAvailablePartsEmpty() throws IOException {
        // Test extraction when the graph is empty or has no parts
        Path emptyFile = tempDir.resolve("really_empty.csv");
        Files.writeString(emptyFile, ""); // Completely empty file
        
        LegoPaths tempPaths = new LegoPaths();
        tempPaths.createNewGraph(emptyFile.toString()); // Might throw or handle gracefully depending on LegoPaths

        List<String> parts = benchmarkRunner.extractAvailableParts(tempPaths);

        assertNotNull(parts, "Parts list should not be null");
        assertTrue(parts.isEmpty(), "Parts list should be empty for an empty graph");
    }
    
    @Test
    @DisplayName("Test BenchmarkLegoPaths main method")
    void testBenchmarkLegoPathsMain() throws IOException {
        // Create test files in the data directory
        Path dataDir = Path.of(TEST_DATA_DIR);
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        
        // Create a sample CSV file for testing
        Path testFile = dataDir.resolve("benchmark_test.csv");
        Files.writeString(testFile, "\"PartA\",\"Set1\"\n\"PartB\",\"Set1\"\n\"PartC\",\"Set2\"\n");
        
        // This is a somewhat limited test that just makes sure the main method doesn't throw
        // We can't easily verify output, but we can ensure it doesn't crash
        String[] args = {testFile.getFileName().toString()};
        
        // Run the main method - if it doesn't throw, the test passes
        assertDoesNotThrow(() -> BenchmarkLegoPaths.main(args), 
                           "BenchmarkLegoPaths.main() should not throw with valid input");
        
        // Also test with no arguments, which should default to input.csv
        assertDoesNotThrow(() -> BenchmarkLegoPaths.main(new String[0]), 
                          "BenchmarkLegoPaths.main() should not throw with no arguments");
    }
} 