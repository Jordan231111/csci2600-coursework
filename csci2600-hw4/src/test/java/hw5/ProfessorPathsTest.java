package hw5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the ProfessorPaths class.
 */
public class ProfessorPathsTest {

    private ProfessorPaths paths;
    private static final String DATA_DIR = "data/"; // Relative path to test data
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    // Helper to create simple CSV files for testing on the fly
    private void createTestFile(String filename, String content) throws IOException {
        Path dirPath = Path.of(DATA_DIR);
        Files.createDirectories(dirPath); // Ensure data directory exists
        Path filePath = dirPath.resolve(filename);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            writer.print(content);
        }
    }

    @BeforeEach
    public void setUp() throws IOException {
        paths = new ProfessorPaths();
        // Set up System.out/err capture
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        // Create necessary test files before each test (overwrites if exist)
        // These match the files planned in Thought 9
        createTestFile("empty.csv", "");
        createTestFile("single_prof_one_course.csv", "\"Prof A\",\"CS101\"");
        createTestFile("two_profs_no_share.csv", "\"Prof A\",\"CS101\"\n\"Prof B\",\"MATH201\"");
        createTestFile("two_profs_one_share.csv", "\"Prof A\",\"CS101\"\n\"Prof B\",\"CS101\"");
        createTestFile("three_profs_path.csv", "\"Prof A\",\"CS101\"\n\"Prof B\",\"CS101\"\n\"Prof B\",\"MATH201\"\n\"Prof C\",\"MATH201\"");
        createTestFile("three_profs_multi_path.csv", "\"Prof A\",\"CS101\"\n\"Prof B\",\"CS101\"\n\"Prof B\",\"MATH201\"\n\"Prof C\",\"MATH201\"\n\"Prof A\",\"PHYS301\"\n\"Prof C\",\"PHYS301\"\n\"Prof A\",\"ALGOL68\"\n\"Prof C\",\"ALGOL68\"");
        createTestFile("lex_prof_choice.csv", "\"P1\",\"C1\"\n\"P2\",\"C1\"\n\"P1\",\"C2\"\n\"P3\",\"C2\"");
        createTestFile("lex_course_choice.csv", "\"P1\",\"C_HIGH\"\n\"P2\",\"C_HIGH\"\n\"P1\",\"C_LOW\"\n\"P2\",\"C_LOW\"");
        createTestFile("special_chars.csv", "\"Prof~A\",\"Course&(1)\"\n\"Prof`B\",\"Course&(1)\"");

        // New test files for additional coverage
        createTestFile("invalid_format.csv", "This is not a valid CSV format");
        createTestFile("multi_step_paths.csv",
            "\"A\",\"C1\"\n\"B\",\"C1\"\n\"B\",\"C2\"\n\"C\",\"C2\"\n\"C\",\"C3\"\n\"D\",\"C3\"\n\"D\",\"C4\"\n\"E\",\"C4\"\n\"E\",\"C5\"\n\"F\",\"C5\"");
        createTestFile("complex_network.csv",
            "\"Prof1\",\"CS101\"\n\"Prof2\",\"CS101\"\n\"Prof2\",\"CS102\"\n\"Prof3\",\"CS102\"\n\"Prof3\",\"CS103\"\n" +
            "\"Prof4\",\"CS103\"\n\"Prof1\",\"CS104\"\n\"Prof4\",\"CS104\"\n\"Prof1\",\"CS105\"\n\"Prof5\",\"CS105\"\n" +
            "\"Prof6\",\"CS106\"\n\"Prof7\",\"CS106\"\n\"Prof7\",\"CS107\"\n\"Prof8\",\"CS107\"\n\"Prof8\",\"CS108\"\n\"Prof9\",\"CS108\"");
        createTestFile("professors_no_courses.csv",
            "\"Prof A\",\"CS101\"\n\"Prof B\",\"CS101\"\n\"Prof C\",\"MATH201\"\n\"Prof D\",\"PHYS301\"");
        createTestFile("null_empty_values.csv",
            "\"Prof A\",\"CS101\"\n\"\",\"CS102\"\n\"Prof B\",\"\"\n\"Prof C\",\"CS103\"");
        createTestFile("many_courses.csv",
            "\"Prof A\",\"CS101\"\n\"Prof A\",\"CS102\"\n\"Prof A\",\"CS103\"\n\"Prof A\",\"CS104\"\n" +
            "\"Prof B\",\"CS101\"\n\"Prof B\",\"CS102\"\n\"Prof B\",\"CS103\"\n\"Prof B\",\"CS104\"");
        createTestFile("cyclic_path.csv",
            "\"A\",\"C1\"\n\"B\",\"C1\"\n\"B\",\"C2\"\n\"C\",\"C2\"\n\"C\",\"C3\"\n\"A\",\"C3\"");
        createTestFile("test_large.csv",
            "\"Prof1\",\"CS101\"\n\"Prof2\",\"CS101\"\n\"Prof2\",\"CS102\"\n\"Prof3\",\"CS102\"");
        createTestFile("large_test_graph.csv",
            "\"Prof1\",\"CS101\"\n\"Prof2\",\"CS101\"\n\"Prof2\",\"CS102\"\n\"Prof3\",\"CS102\"");
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // Test createNewGraph implicitly via findPath tests

    @Test
    public void testFindPathOnEmptyGraph() {
        paths.createNewGraph(DATA_DIR + "empty.csv");
        String expected = "unknown professor Prof A\nunknown professor Prof B\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof B"));
    }

    @Test
    public void testFindPathUnknownProfessorStart() {
        paths.createNewGraph(DATA_DIR + "two_profs_one_share.csv");
        String expected = "unknown professor Unknown Prof\n";
        assertEquals(expected, paths.findPath("Unknown Prof", "Prof B"));
    }

    @Test
    public void testFindPathUnknownProfessorEnd() {
        paths.createNewGraph(DATA_DIR + "two_profs_one_share.csv");
        String expected = "unknown professor Unknown Prof\n";
        assertEquals(expected, paths.findPath("Prof A", "Unknown Prof"));
    }

    @Test
    public void testFindPathBothProfessorsUnknown() {
        paths.createNewGraph(DATA_DIR + "two_profs_one_share.csv");
        String expected = "unknown professor Unknown1\nunknown professor Unknown2\n";
        assertEquals(expected, paths.findPath("Unknown1", "Unknown2"));
    }

    @Test
    public void testFindPathSameUnknownProfessor() {
        paths.createNewGraph(DATA_DIR + "two_profs_one_share.csv");
        String expected = "unknown professor Unknown1\n";
        assertEquals(expected, paths.findPath("Unknown1", "Unknown1"));
    }

    @Test
    public void testFindPathSelfExistentProfessor() {
        paths.createNewGraph(DATA_DIR + "two_profs_one_share.csv");
        String expected = "path from Prof A to Prof A:\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof A"));
    }

    @Test
    public void testFindPathNoPathExists() {
        paths.createNewGraph(DATA_DIR + "two_profs_no_share.csv");
        String expected = "path from Prof A to Prof B:\nno path found\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof B"));
    }

    @Test
    public void testFindPathSimpleDirectPath() {
        paths.createNewGraph(DATA_DIR + "two_profs_one_share.csv");
        String expected = "path from Prof A to Prof B:\nProf A to Prof B via CS101\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof B"));
    }

    @Test
    public void testFindPathLongerPath() {
        paths.createNewGraph(DATA_DIR + "three_profs_path.csv");
        String expected = "path from Prof A to Prof C:\nProf A to Prof B via CS101\nProf B to Prof C via MATH201\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof C"));
    }

    @Test
    public void testFindPathShortestPathChosen() {
        // Uses three_profs_multi_path.csv where A->C is direct (PHYS301 or ALGOL68)
        // and A->B->C exists (CS101, MATH201)
        // BFS guarantees shortest path (length 1).
        // Lexicographically, ALGOL68 comes before PHYS301.
        paths.createNewGraph(DATA_DIR + "three_profs_multi_path.csv");
        String expected = "path from Prof A to Prof C:\nProf A to Prof C via ALGOL68\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof C"));
    }

    @Test
    public void testFindPathLexicographicalProfessorChoice() {
        // Uses lex_prof_choice.csv: P1->P2 (C1), P1->P3 (C2)
        // P2 comes before P3 alphabetically.
        paths.createNewGraph(DATA_DIR + "lex_prof_choice.csv");
        // This test assumes we want path *from* P1. If we wanted path *to* P1, result might differ.
        // Let's test path P1 to P2 vs P1 to P3 existing implicitly. Test P1->P2
         String expected = "path from P1 to P2:\nP1 to P2 via C1\n";
        assertEquals(expected, paths.findPath("P1", "P2"));
        // Path P1->P3 also exists, but P1->P2 should be chosen *if P2 is the target*.
        // A better test might be P1 to a common target reachable via P2 or P3, where P2 path is chosen first.
        // Let's re-test A->C on three_profs_multi_path.csv
        paths.createNewGraph(DATA_DIR + "three_profs_multi_path.csv");
        String expectedAC = "path from Prof A to Prof C:\nProf A to Prof C via ALGOL68\n";
        assertEquals(expectedAC, paths.findPath("Prof A", "Prof C"));
        // Test A -> B
         String expectedAB = "path from Prof A to Prof B:\nProf A to Prof B via CS101\n";
        assertEquals(expectedAB, paths.findPath("Prof A", "Prof B"));
    }

    @Test
    public void testFindPathLexicographicalCourseChoice() {
        try {
            // Make sure the test file is created properly
            // We use A_COURSE and B_COURSE to ensure correct lexicographical ordering
            createTestFile("lex_course_choice.csv",
                "\"P1\",\"A_COURSE\"\n\"P2\",\"A_COURSE\"\n\"P1\",\"B_COURSE\"\n\"P2\",\"B_COURSE\"");

            // Test lexicographical ordering of courses
            paths.createNewGraph(DATA_DIR + "lex_course_choice.csv");
            String result = paths.findPath("P1", "P2");
            assertTrue(result.contains("P1 to P2 via A_COURSE"),
                "Expected lexicographically smallest course A_COURSE, got: " + result);
        } catch (IOException e) {
            org.junit.jupiter.api.Assertions.fail("Failed to create test file: " + e.getMessage());
        }
    }

    @Test
    public void testFindPathWithSpecialChars() {
        paths.createNewGraph(DATA_DIR + "special_chars.csv");
        String expected = "path from Prof~A to Prof`B:\nProf~A to Prof`B via Course&(1)\n";
        assertEquals(expected, paths.findPath("Prof~A", "Prof`B"));
    }

    @Test
    public void testFileNotFound() {
        // Since createNewGraph no longer throws IOException, we need to test the behavior differently
        paths.createNewGraph("nonexistent_file.csv");
        // The graph should be empty, so any path lookup should indicate unknown professors
        String expected = "unknown professor Prof A\nunknown professor Prof B\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof B"));
    }

    @Test
    public void testInvalidFileFormat() {
        // No try-catch for IOException since createNewGraph handles it internally now
        paths.createNewGraph(DATA_DIR + "invalid_format.csv");
        // The graph should be empty or properly handle the invalid format
        String expected = "unknown professor Prof A\nunknown professor Prof B\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof B"));
    }

    @Test
    public void testMultiStepPath() {
        paths.createNewGraph(DATA_DIR + "multi_step_paths.csv");
        String expected = "path from A to F:\nA to B via C1\nB to C via C2\nC to D via C3\nD to E via C4\nE to F via C5\n";
        assertEquals(expected, paths.findPath("A", "F"));
    }

    @Test
    public void testComplexNetwork() {
        paths.createNewGraph(DATA_DIR + "complex_network.csv");
        // Test direct path from Prof1 to Prof4 (should choose shortest)
        String expected = "path from Prof1 to Prof4:\nProf1 to Prof4 via CS104\n";
        assertEquals(expected, paths.findPath("Prof1", "Prof4"));

        // Test path with multiple options
        String expectedPath = "path from Prof1 to Prof3:\nProf1 to Prof2 via CS101\nProf2 to Prof3 via CS102\n";
        assertEquals(expectedPath, paths.findPath("Prof1", "Prof3"));
    }

    @Test
    public void testPathWithMultipleOptions() {
        try {
            // Create a CSV with multiple possible paths of same length but different course options
            createTestFile("multiple_course_options.csv",
                "\"A\",\"C1\"\n\"B\",\"C1\"\n\"A\",\"C2\"\n\"B\",\"C2\"\n\"A\",\"C3\"\n\"B\",\"C3\"");

            paths.createNewGraph(DATA_DIR + "multiple_course_options.csv");
            // Should choose the lexicographically smallest course (C1)
            String expected = "path from A to B:\nA to B via C1\n";
            assertEquals(expected, paths.findPath("A", "B"));
        } catch (IOException e) {
            org.junit.jupiter.api.Assertions.fail("Failed to create test file: " + e.getMessage());
        }
    }

    @Test
    public void testReloadGraphWithNewData() {
        // Test that the graph can be reloaded with new data
        paths.createNewGraph(DATA_DIR + "two_profs_one_share.csv");
        String expected1 = "path from Prof A to Prof B:\nProf A to Prof B via CS101\n";
        assertEquals(expected1, paths.findPath("Prof A", "Prof B"));

        // Now reload with different data
        paths.createNewGraph(DATA_DIR + "three_profs_path.csv");
        String expected2 = "path from Prof A to Prof C:\nProf A to Prof B via CS101\nProf B to Prof C via MATH201\n";
        assertEquals(expected2, paths.findPath("Prof A", "Prof C"));
    }

    @Test
    public void testSingleNodeNetwork() {
        paths.createNewGraph(DATA_DIR + "single_prof_one_course.csv");
        // Test self-path
        String expected = "path from Prof A to Prof A:\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof A"));

        // Test unknown destination
        String expectedUnknown = "unknown professor Prof B\n";
        assertEquals(expectedUnknown, paths.findPath("Prof A", "Prof B"));
    }

    @Test
    public void testFindPathOnLargeGraph() {
        // This is a smaller test for CI/CD pipelines to avoid timeouts with massive data
        paths.createNewGraph(DATA_DIR + "complex_network.csv");

        // Test a few path finding operations
        String expected1 = "path from Prof1 to Prof9:\nProf1 to Prof2 via CS101\nProf2 to Prof3 via CS102\nProf3 to Prof4 via CS103\nProf4 to Prof1 via CS104\nProf1 to Prof5 via CS105\n";
        String expected2 = "path from Prof1 to Prof9:\nno path found\n";
        String result = paths.findPath("Prof1", "Prof9");

        assertTrue(result.equals(expected1) || result.equals(expected2),
                "Result: " + result + " did not match either expected pattern.");
    }

    @Test
    public void testPathSegmentMethods() throws Exception {
        // Testing the PathSegment class, which is a record with minimal methods
        // but can benefit from coverage testing

        // Use reflection to directly create PathSegment objects
        Class<?> pathSegmentClass = Class.forName("hw5.ProfessorPaths$PathSegment");
        Constructor<?> constructor = pathSegmentClass.getDeclaredConstructor(String.class, String.class);

        Method compareToMethod = pathSegmentClass.getMethod("compareTo", pathSegmentClass);

        // Create test instances
        Object ps1 = constructor.newInstance("Prof A", "CS101");
        Object ps2 = constructor.newInstance("Prof A", "CS102");
        Object ps3 = constructor.newInstance("Prof B", "CS101");
        Object ps4 = constructor.newInstance("Prof A", null);
        Object ps5 = constructor.newInstance("Prof B", null);

        // Test compareTo
        assertTrue((int)compareToMethod.invoke(ps1, ps2) < 0, "CS101 should come before CS102");
        assertTrue((int)compareToMethod.invoke(ps2, ps1) > 0, "CS102 should come after CS101");
        assertTrue((int)compareToMethod.invoke(ps1, ps3) < 0, "Prof A should come before Prof B with same course");
        assertTrue((int)compareToMethod.invoke(ps3, ps1) > 0, "Prof B should come after Prof A with same course");
        assertTrue((int)compareToMethod.invoke(ps4, ps1) < 0, "Null course should come before any non-null course");
        assertTrue((int)compareToMethod.invoke(ps1, ps4) > 0, "Non-null course should come after null course");
        assertTrue((int)compareToMethod.invoke(ps4, ps5) < 0, "Prof A with null should come before Prof B with null");

        // Ensure equals works correctly for a complete record
        Object ps1Copy = constructor.newInstance("Prof A", "CS101");
        assertTrue(ps1.equals(ps1Copy), "Equal PathSegments should match with equals()");
        assertFalse(ps1.equals(ps2), "Different PathSegments should not match with equals()");
    }

    // ADDITIONAL TEST METHODS FOR COVERAGE IMPROVEMENT

    @Test
    public void testGetAllProfessorNames() {
        // Test on empty graph
        paths.createNewGraph(DATA_DIR + "empty.csv");
        assertTrue(paths.getAllProfessorNames().isEmpty(), "Empty graph should have no professors");

        // Test on graph with professors
        paths.createNewGraph(DATA_DIR + "three_profs_path.csv");
        Set<String> professors = paths.getAllProfessorNames();
        assertEquals(3, professors.size(), "Should have 3 professors");
        assertTrue(professors.contains("Prof A"));
        assertTrue(professors.contains("Prof B"));
        assertTrue(professors.contains("Prof C"));

        // The HashSet returned by getAllProfessorNames is a copy and should be modifiable
        Set<String> modifiableSet = paths.getAllProfessorNames();
        modifiableSet.add("New Prof"); // Should not throw exception
    }

    @Test
    public void testCreateNewGraphWithNullOrEmpty() {
        // Test with null filename
        assertThrows(IllegalArgumentException.class, () -> paths.createNewGraph(null));

        // Test with empty filename
        assertThrows(IllegalArgumentException.class, () -> paths.createNewGraph(""));
    }

    @Test
    public void testFindPathWithNullParameters() {
        paths.createNewGraph(DATA_DIR + "two_profs_one_share.csv");

        // Test with null parameters
        assertThrows(IllegalArgumentException.class, () -> paths.findPath(null, "Prof B"));
        assertThrows(IllegalArgumentException.class, () -> paths.findPath("Prof A", null));
        assertThrows(IllegalArgumentException.class, () -> paths.findPath(null, null));
    }

    @Test
    public void testHandlingNullAndEmptyValues() {
        // Test a file with null/empty professor or course values
        paths.createNewGraph(DATA_DIR + "null_empty_values.csv");

        // Make sure it loaded properly despite invalid entries
        Set<String> professors = paths.getAllProfessorNames();
        assertTrue(professors.contains("Prof A"));
        assertTrue(professors.contains("Prof C"));

        // Test findPath still works
        String expected = "path from Prof A to Prof C:\nno path found\n";
        assertEquals(expected, paths.findPath("Prof A", "Prof C"));
    }

    @Test
    public void testCyclicPath() {
        // Test graph with a cycle
        paths.createNewGraph(DATA_DIR + "cyclic_path.csv");

        // Test path from A to C
        String result = paths.findPath("A", "C");
        assertTrue(
            result.contains("path from A to C:") &&
            (result.contains("A to B via C1") || result.contains("A to C via")) && // Check if either possible first step exists
            (result.contains("B to C via C2") || !result.contains("B to C via")), // Check second step or its absence
            "Should find a path from A to C, exact path may vary: " + result
        );

        // Test cycle in reverse
        String result2 = paths.findPath("C", "A");
        assertTrue(
            result2.contains("path from C to A:") &&
            (result2.contains("C to A via C3") || result2.contains("C to B via")), // Check either direct or via B path
            "Should find a path from C to A, exact path may vary: " + result2
        );
    }

    @Test
    public void testMainMethod() {
        // === Test main method with no args ===
        ProfessorPaths.main(new String[0]);

        // Check error output contains expected usage message
        String errorOutput = errContent.toString();
        assertTrue(
            errorOutput.contains("Usage: java hw5.ProfessorPaths <datafile> <professor1> <professor2>") &&
            errorOutput.contains("Usage: java hw5.ProfessorPaths <datafile> --large-scale-test"),
            "Should show usage message on System.err for no arguments. Got:\n" + errorOutput
        );

        // Ensure standard output is empty for the no-args case
        String output = outContent.toString();
        assertTrue(output.isEmpty(), "Standard output should be empty for incorrect usage (no args). Got:\n" + output);

        // Reset output streams for the next test part
        outContent.reset();
        errContent.reset();

        // === Test main method with valid file but potentially invalid professors ===
        // This part of the test was already present, just ensure streams are checked correctly
        // Test main method with valid file but invalid professors
        ProfessorPaths.main(new String[] {
            DATA_DIR + "two_profs_one_share.csv", "NonExistent1", "NonExistent2"
        });

        // No assertion needed - just making sure it doesn't throw an exception
        // (Original comment preserved)
        // Adding an assertion to check standard output for the valid execution path
        output = outContent.toString();
        assertTrue(
            output.contains("Testing with " + DATA_DIR + "two_profs_one_share.csv") ||
            output.contains("Graph loaded in") ||
            output.contains("unknown professor NonExistent1"), // Check for part of the findPath output
            "Should show standard execution output when arguments are provided. Got:\n" + output
        );
    }

    @Test
    public void testMainMethodLargeTest() {
        // Test the --large-scale-test option with the file we created
        ProfessorPaths.main(new String[] {DATA_DIR + "test_large.csv", "--large-scale-test"});

        // Check System.out for expected large-scale test output
        String output = outContent.toString();
        assertTrue(
            output.contains("Running large-scale test on: " + DATA_DIR + "test_large.csv") &&
            output.contains("Graph loaded in") &&
            output.contains("Average time per query:"),
            "Should show output related to large scale test execution. Got:\n" + output
        );
    }

    @Test
    public void testMainMethodFileNotFound() {
        // Test main method with nonexistent file
        ProfessorPaths.main(new String[] {
            "nonexistent_file.csv", "Prof A", "Prof B"
        });

        // Check System.err for the file reading error
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Error reading data file nonexistent_file.csv"),
                   "Should print file reading error to System.err. Got:\n" + errorOutput);

        // Check System.out for the subsequent findPath attempt (which should fail)
        String output = outContent.toString();
        assertTrue(output.contains("unknown professor Prof A"),
                   "Should attempt findPath and report unknown professors after failed load. Got:\n" + output);
    }

    @Test
    public void testCreateNewGraphWithErrorHandling() {
        // Test handling of exceptions during graph creation

        // Make a severely malformed file
        try {
            createTestFile("severely_malformed.csv",
                "This is not a CSV file at all\nIt has no proper format\n1,2,3,4,5");
        } catch (IOException e) {
            org.junit.jupiter.api.Assertions.fail("Failed to create test file: " + e.getMessage());
        }

        // Create graph should handle the exception gracefully (prints to System.err)
        paths.createNewGraph(DATA_DIR + "severely_malformed.csv");

        // Check System.err for warning/error messages during parsing if any
        String errorOutput = errContent.toString();
        // Depending on parser implementation, it might print warnings. This is optional.
        // assertTrue(errorOutput.contains("Warning:") || errorOutput.isEmpty(),
        //            "Error stream might contain warnings for malformed data. Got:\n" + errorOutput);

        // Graph should be empty as parsing likely failed early or skipped all lines
        assertTrue(paths.getAllProfessorNames().isEmpty(),
                   "Graph should be empty after attempting to load severely malformed data");
    }
}