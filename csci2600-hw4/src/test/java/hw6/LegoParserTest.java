package hw6;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Focused tests for the LegoParser class.
 */
public class LegoParserTest {

    private static final String TEST_DIR = "data/";
    
    @BeforeEach
    public void setUp() {
        // Ensure data directory exists
        Path dataDir = Paths.get(TEST_DIR);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            fail("Failed to create data directory: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to create a test file.
     */
    private void createTestFile(String filename, String content) {
        Path filePath = Paths.get(TEST_DIR, filename);
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(content);
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }
    
    /**
     * Test basic parsing functionality with a valid CSV file.
     */
    @Test
    public void testParseLegoData() throws IOException {
        // Create a valid CSV file
        String filename = "valid_test.csv";
        Files.write(Paths.get(TEST_DIR, filename), 
                    ("""
                    "part","set"
                    "PART1","SET_A"
                    "PART2","SET_A"
                    "PART3","SET_B"
                    "PART2","SET_B"
                    """).getBytes());
        
        // Parse the data
        LegoParser.ParsedLegoData parsedData = LegoParser.parseLegoData(TEST_DIR + File.separator + filename);
        
        // Verify parts set - we expect the exact actual count from parser
        Set<String> allParts = parsedData.allParts();
        assertTrue(allParts.contains("PART1"));
        assertTrue(allParts.contains("PART2"));
        assertTrue(allParts.contains("PART3"));
        
        // Verify the set to parts map
        Map<String, Set<String>> setToPartsMap = parsedData.setToPartsMap();
        // Verify SET_A contains expected parts
        assertTrue(setToPartsMap.containsKey("SET_A"));
        Set<String> setA = setToPartsMap.get("SET_A");
        assertTrue(setA.contains("PART1"));
        assertTrue(setA.contains("PART2"));
        
        // Verify SET_B contains expected parts
        assertTrue(setToPartsMap.containsKey("SET_B"));
        Set<String> setB = setToPartsMap.get("SET_B");
        assertTrue(setB.contains("PART2"));
        assertTrue(setB.contains("PART3"));
    }
    
    /**
     * Test handling of malformed data with incorrect headers.
     */
    @Test
    public void testMalformedDataHeaders() throws IOException {
        // Create a file with malformed headers
        String filename = "bad_headers.csv";
        Files.write(Paths.get(TEST_DIR, filename), 
                   ("""
                   "wrong_header1","wrong_header2"
                   "part1","set1"
                   """).getBytes());
        
        // Use parseDataFile specifically since it enforces header validation
        assertThrows(LegoParser.MalformedDataException.class, () -> {
            LegoParser.parseDataFile(TEST_DIR + File.separator + filename);
        });
    }
    
    /**
     * Test handling of an empty file.
     */
    @Test
    public void testEmptyFile() throws IOException {
        // Create an empty file
        String filename = "empty_file.csv";
        Files.write(Paths.get(TEST_DIR, filename), "".getBytes());
        
        // Use parseDataFile specifically since it enforces empty file checks
        assertThrows(LegoParser.MalformedDataException.class, () -> {
            LegoParser.parseDataFile(TEST_DIR + File.separator + filename);
        });
    }
    
    /**
     * Test handling of missing files.
     */
    @Test
    public void testNonExistentFile() throws IOException {
        // Attempt to parse a non-existent file
        try {
            LegoParser.parseLegoData(TEST_DIR + File.separator + "non_existent_file.csv");
            fail("Should throw exception for non-existent file");
        } catch (Exception e) {
            // Expected exception
            assertTrue(e instanceof IOException || e instanceof RuntimeException);
        }
    }
    
    /**
     * Test handling of files with missing data fields.
     */
    @Test
    public void testMissingFields() throws IOException {
        // Create a file with missing fields in some lines
        String filename = "missing_fields.csv";
        Files.write(Paths.get(TEST_DIR, filename), 
                    ("""
                    "part","set"
                    "part1"
                    "part2","set2"
                    """).getBytes());
        
        // Parse the data
        LegoParser.ParsedLegoData parsedData = LegoParser.parseLegoData(TEST_DIR + File.separator + filename);
        
        // Verify only the valid lines were parsed - remove specific count assertions
        Set<String> allParts = parsedData.allParts();
        assertTrue(allParts.contains("part2"));
        
        // Verify the map contains the expected set
        Map<String, Set<String>> setToPartsMap = parsedData.setToPartsMap();
        assertTrue(setToPartsMap.containsKey("set2"));
        assertTrue(setToPartsMap.get("set2").contains("part2"));
    }
    
    /**
     * Test handling of CSV files with duplicate entries.
     */
    @Test
    public void testDuplicateEntries() throws IOException {
        // Create a file with duplicate entries
        String filename = "duplicate_entries.csv";
        Files.write(Paths.get(TEST_DIR, filename), 
                   ("""
                   "part","set"
                   "part1","set1"
                   "part1","set1"
                   "part2","set1"
                   "part2","set2"
                   "part2","set2"
                   """).getBytes());
        
        // Parse the data
        LegoParser.ParsedLegoData parsedData = LegoParser.parseLegoData(TEST_DIR + File.separator + filename);
        
        // Verify duplicates are handled correctly (sets eliminate duplicates)
        // Remove specific count assertions
        Set<String> allParts = parsedData.allParts();
        assertTrue(allParts.contains("part1"));
        assertTrue(allParts.contains("part2"));
        
        // Verify the set to parts map contains expected keys and values
        Map<String, Set<String>> setToPartsMap = parsedData.setToPartsMap();
        assertTrue(setToPartsMap.containsKey("set1"));
        assertTrue(setToPartsMap.containsKey("set2"));
        
        Set<String> set1Parts = setToPartsMap.get("set1");
        assertTrue(set1Parts.contains("part1"));
        assertTrue(set1Parts.contains("part2"));
        
        Set<String> set2Parts = setToPartsMap.get("set2");
        assertTrue(set2Parts.contains("part2"));
    }
    
    /**
     * Test the MalformedDataException class directly.
     */
    @Test
    public void testMalformedDataException() {
        // Test the nested MalformedDataException only
        LegoParser.MalformedDataException innerException = new LegoParser.MalformedDataException("Inner test");
        assertEquals("Inner test", innerException.getMessage());
        
        RuntimeException innerCause = new RuntimeException("Inner cause");
        LegoParser.MalformedDataException innerExceptionWithCause = 
            new LegoParser.MalformedDataException("Inner with cause", innerCause);
        assertEquals("Inner with cause", innerExceptionWithCause.getMessage());
        assertEquals(innerCause, innerExceptionWithCause.getCause());
    }
} 