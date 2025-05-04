package hw5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class provides static methods for parsing professor/course data from a CSV file.
 * It does not represent an Abstract Data Type (ADT).
 */
public class ProfessorParser {

    /**
     * A simple container class to hold the results of parsing the data file.
     */
    public static class ParsedData {
        /** Set of all unique professor names found in the file. */
        public final Set<String> professors;
        /** Map where keys are course codes and values are sets of professors who taught that course. */
        public final Map<String, Set<String>> courseToProfessors;

        /**
         * Creates a new ParsedData object.
         * @param professors The set of professor names.
         * @param courseToProfessors The map from courses to sets of professors.
         */
        public ParsedData(Set<String> professors, Map<String, Set<String>> courseToProfessors) {
            // Use defensive copying if mutability is a concern, but for this scope, direct assignment is okay.
            // For more robust code, consider:
            // this.professors = Collections.unmodifiableSet(new HashSet<>(professors));
            // Map<String, Set<String>> tempMap = new HashMap<>();
            // courseToProfessors.forEach((course, profSet) ->
            //     tempMap.put(course, Collections.unmodifiableSet(new HashSet<>(profSet)))
            // );
            // this.courseToProfessors = Collections.unmodifiableMap(tempMap);
            this.professors = professors;
            this.courseToProfessors = courseToProfessors;
        }
    }

    /**
     * Reads professor and course data from a specified CSV file.
     * The file format is expected to be: "professor_name","course_code"
     * Each line represents one instance of a professor teaching a course.
     *
     * @param filename The path to the CSV data file.
     * @return A ParsedData object containing a set of all unique professors
     *         and a map from each course to the set of professors who taught it.
     * @throws IOException if an error occurs reading the file.
     * @throws IllegalArgumentException if the filename is null or empty.
     * @effects Reads the file specified by filename.
     * @modifies None (beyond reading the file system).
     */
    public static ParsedData readData(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty.");
        }

        Set<String> professors = new HashSet<>();
        Map<String, Set<String>> courseToProfessors = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines or lines that don't have quotes and commas
                if (line.isEmpty() || !line.contains("\",\"")) {
                    continue;
                }

                // Fast path for properly formatted lines
                int commaPos = line.indexOf("\",\"");
                if (commaPos <= 0 || !line.startsWith("\"") || !line.endsWith("\"")) {
                    continue;
                }

                // Extract professor (remove leading quote)
                String professor = line.substring(1, commaPos).trim();
                
                // Extract course (remove trailing quote)
                String course = line.substring(commaPos + 3, line.length() - 1).trim();
                
                // Skip if either field is empty
                if (professor.isEmpty() || course.isEmpty()) {
                    continue;
                }

                // Add professor to the overall set
                professors.add(professor);

                // Add professor to the set for this course
                courseToProfessors.computeIfAbsent(course, k -> new HashSet<>()).add(professor);
            }
        }

        return new ParsedData(professors, courseToProfessors);
    }

    // Optional: Add a main method for testing the parser directly (as suggested in HW)
    // Remember to comment it out for final submission/coverage measurement.
    /*
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java hw5.ProfessorParser <filename>");
            return;
        }
        String filename = args[0];
        try {
            ParsedData data = readData(filename);
            System.out.println("Successfully parsed data:");
            System.out.println("Total unique professors: " + data.professors.size());
            System.out.println("Total unique courses: " + data.courseToProfessors.size());
            // Optional: Print more details for debugging
            // System.out.println("Professors: " + data.professors);
            // System.out.println("Course Mappings: " + data.courseToProfessors);
        } catch (IOException e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
             System.err.println("Error: " + e.getMessage());
        }
    }
    */
} 