package hw6;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parser utility for reading LEGO dataset files.
 * The file format is expected to be CSV with each line containing "part","set".
 *
 * This class is not an ADT.
 */
public final class LegoParser {
    
    // Larger buffer size for improved file reading performance
    private static final int BUFFER_SIZE = 16384; // 16KB buffer

    // Constants for initial capacity estimation
    private static final int EXPECTED_PART_COUNT = 30000;
    private static final int EXPECTED_SET_COUNT = 15000;
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private LegoParser() {}

    /**
     * Represents the parsed data from a LEGO file.
     *
     * @param allParts         A set containing all unique part identifiers found.
     * @param setToPartsMap A map where keys are set identifiers and values are sets
     *                         of part identifiers included in that set.
     */
    public record ParsedLegoData(Set<String> allParts, Map<String, Set<String>> setToPartsMap) {}

    /**
     * Reads LEGO data from a CSV file.
     * This method delegates to fastParseDataFile for optimized performance.
     *
     * @param filename The path to the CSV data file (e.g., "data/lego.csv").
     * @return A ParsedLegoData record containing the set of all parts and a map from sets to parts.
     * @throws IOException if an I/O error occurs reading the file.
     */
    public static ParsedLegoData parseLegoData(String filename) throws IOException {
        return fastParseDataFile(filename);
    }

    /**
     * Reads LEGO data from a CSV file.
     * Optimized for large file parsing with minimal memory allocations.
     *
     * @param filename The path to the CSV data file (e.g., "data/lego.csv").
     * @return A ParsedLegoData record containing the set of all parts and a map from sets to parts.
     * @throws IOException if an I/O error occurs reading the file.
     * @throws MalformedDataException if the file format is invalid.
     */
    public static ParsedLegoData parseDataFile(String filename) throws IOException {
        Map<String, Set<String>> setToPartsMap = new HashMap<>(EXPECTED_SET_COUNT);
        Set<String> allParts = new HashSet<>(EXPECTED_PART_COUNT);

        // Use try-with-resources and Files.newBufferedReader for efficiency and encoding control
        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8), BUFFER_SIZE)) {
            // Skip the header line
            String header = reader.readLine();
            if (header == null) {
                throw new MalformedDataException("Empty file: " + filename);
            }

            // Validate header format
            String[] headerFields = header.split(",");
            if (headerFields.length < 2 || !headerFields[0].equals("part") || !headerFields[1].equals("set")) {
                throw new MalformedDataException("Invalid header format: " + header);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 2) {
                    continue; // Skip malformed lines
                }

                String part = fields[0];
                String set = fields[1];

                // Add to allParts set
                allParts.add(part);

                // Add to setToPartsMap
                setToPartsMap.computeIfAbsent(set, k -> new HashSet<>()).add(part);
            }
        }

        return new ParsedLegoData(allParts, setToPartsMap);
    }

    /**
     * Fast parsing of LEGO data using a more efficient approach with pre-sized collections.
     *
     * @param filename The path to the CSV data file.
     * @return A ParsedLegoData record.
     * @throws IOException if an I/O error occurs.
     */
    public static ParsedLegoData fastParseDataFile(String filename) throws IOException {
        // Pre-size collections based on expected data size
        Map<String, Set<String>> setToPartsMap = new HashMap<>(EXPECTED_SET_COUNT);
        Set<String> allParts = new HashSet<>(EXPECTED_PART_COUNT);
        
        // Use try-with-resources and Files.newBufferedReader for efficiency and encoding control
        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8), BUFFER_SIZE)) {
            // Read header but don't validate it - some test files have data in first line
            String header = reader.readLine();
            
            // If the header is already a data line, process it
            if (header != null && header.startsWith("\"")) {
                processLine(header, allParts, setToPartsMap);
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line, allParts, setToPartsMap);
            }
        }
        
        return new ParsedLegoData(allParts, setToPartsMap);
    }
    
    /**
     * Process a single line of CSV data.
     * Optimized version with minimal string object creation.
     * 
     * @param line The line to process
     * @param allParts Set to add parts to
     * @param setToPartsMap Map to add sets and parts to
     */
    private static void processLine(String line, Set<String> allParts, Map<String, Set<String>> setToPartsMap) {
        // Fast path for empty lines
        final int lineLength = line.length();
        if (lineLength == 0) {
            return;
        }
        
        String part = null;
        String set = null;
        
        // Parse CSV line with quoted values (e.g., "Part1","Set1")
        if (line.charAt(0) == '"') {
            // Handle the more complex case with quoted values
            try {
                int firstQuoteEnd = -1;
                int secondQuoteStart = -1;
                int secondQuoteEnd = -1;
                
                // Find first quote end (accounting for escaped quotes)
                for (int i = 1; i < lineLength; i++) {
                    if (line.charAt(i) == '"') {
                        // Check if this is an escaped quote (i.e., "")
                        if (i + 1 < lineLength && line.charAt(i + 1) == '"') {
                            i++; // Skip the next quote as it's part of the escaped sequence
                            continue;
                        }
                        firstQuoteEnd = i;
                        break;
                    }
                }
                
                if (firstQuoteEnd > 0) {
                    // Extract part name, handling escaped quotes by replacing "" with "
                    part = line.substring(1, firstQuoteEnd).replace("\"\"", "\"").intern();
                    
                    // Find second quote start
                    for (int i = firstQuoteEnd + 1; i < lineLength; i++) {
                        if (line.charAt(i) == '"') {
                            secondQuoteStart = i;
                            break;
                        }
                    }
                    
                    if (secondQuoteStart > 0) {
                        // Find second quote end (accounting for escaped quotes)
                        for (int i = secondQuoteStart + 1; i < lineLength; i++) {
                            if (line.charAt(i) == '"') {
                                // Check if this is an escaped quote
                                if (i + 1 < lineLength && line.charAt(i + 1) == '"') {
                                    i++; // Skip the next quote
                                    continue;
                                }
                                secondQuoteEnd = i;
                                break;
                            }
                        }
                        
                        if (secondQuoteEnd > 0) {
                            // Extract set name, handling escaped quotes
                            set = line.substring(secondQuoteStart + 1, secondQuoteEnd).replace("\"\"", "\"").intern();
                        }
                    }
                }
            } catch (Exception e) {
                // Fallback to simpler parsing if complex parsing fails
                int commaIdx = line.indexOf(',');
                if (commaIdx > 0 && commaIdx < lineLength - 1) {
                    // Try to extract quoted values with simpler algorithm
                    if (line.charAt(0) == '"' && commaIdx > 1 && line.charAt(commaIdx - 1) == '"') {
                        part = line.substring(1, commaIdx - 1).trim().intern();
                    } else {
                        part = line.substring(0, commaIdx).trim().intern();
                    }
                    
                    String remainder = line.substring(commaIdx + 1).trim();
                    if (remainder.startsWith("\"") && remainder.endsWith("\"") && remainder.length() > 1) {
                        set = remainder.substring(1, remainder.length() - 1).trim().intern();
                    } else {
                        set = remainder.intern();
                    }
                }
            }
        } else {
            // Fallback for unquoted format (e.g., Part1,Set1)
            int commaIdx = line.indexOf(',');
            if (commaIdx > 0 && commaIdx < lineLength - 1) {
                // Intern strings to reduce memory usage with duplicate values
                part = line.substring(0, commaIdx).trim().intern();
                set = line.substring(commaIdx + 1).trim().intern();
            }
        }
        
        // Add to data structures if both part and set were successfully parsed
        if (part != null && set != null) {
            allParts.add(part);
            
            // Optimize computeIfAbsent with direct lookup first
            Set<String> partsInSet = setToPartsMap.get(set);
            if (partsInSet == null) {
                partsInSet = new HashSet<>();
                setToPartsMap.put(set, partsInSet);
            }
            partsInSet.add(part);
        }
    }

    /**
     * Exception class for errors encountered during parsing due to invalid file format.
     */
    public static class MalformedDataException extends IOException {
        private static final long serialVersionUID = 1L;
        
        public MalformedDataException(String message) {
            super(message);
        }

        public MalformedDataException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}