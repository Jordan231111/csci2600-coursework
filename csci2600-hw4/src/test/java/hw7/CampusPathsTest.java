package hw7;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CampusPathsTest {

	/**
	 * @param file1 
	 * @param file2
	 * @return true if file1 and file2 have the same content, false otherwise
	 * @throws IOException
	 */	
	/* compares two text files, line by line */
	private static boolean compare(String file1, String file2) throws IOException {
		BufferedReader is1 = new BufferedReader(new FileReader(file1)); // Decorator design pattern!
		BufferedReader is2 = new BufferedReader(new FileReader(file2));
		String line1, line2;
		boolean result = true;
		while ((line1=is1.readLine()) != null) {
			line2 = is2.readLine();
			if (line2 == null) {
				System.out.println(file1+" longer than "+file2);
				result = false;
				break;
			}
			// Revert to original comparison
			if (!line1.equals(line2)) { 
				System.out.println("Lines: " + line1 + " and " + line2 + " differ.");
				result = false;
				break;
			}
		}
		if (result && is2.readLine() != null) {
			System.out.println(file1 + " shorter than " + file2);
			result = false;
		}
		is1.close();
		is2.close();
		return result;
	}

	private void runTest(String filename) throws IOException {
		InputStream in = System.in;
		PrintStream out = System.out;
		String inFilename = "data/" + filename + ".test"; // Input filename: [filename].test here  
		String expectedFilename = "data/" + filename + ".expected"; // Expected result filename: [filename].expected
		String outFilename = "data/" + filename + ".out"; // Output filename: [filename].out
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(inFilename));
		System.setIn(is); // redirects standard input to a file, [filename].test 
		PrintStream os = new PrintStream(new FileOutputStream(outFilename));
		System.setOut(os); // redirects standard output to a file, [filename].out 
		CampusPaths.main(null); // Call to YOUR main. May have to rename.
		System.setIn(in); // restores standard input
		System.setOut(out); // restores standard output
		os.flush(); // Explicitly flush the stream
		os.close(); // Close the stream (already implicitly done by try-with-resources if PrintStream was created there, but explicit close after flush doesn't hurt)
		assertTrue(compare(expectedFilename, outFilename)); 
		// TODO: You can implement more informative file comparison, if you would like.

	}
	
	@Test
	public void testListBuildings() throws IOException {
		runTest("test1");
	}

	@Test
	public void testRoute() throws IOException {
		runTest("test_route");
	}

	@Test
	public void testNameRoute() throws IOException {
		runTest("test_name_route");
	}

	@Test
	public void testNoPath() throws IOException {
		runTest("test_no_path");
	}
	
	@Test
	public void testUnknownBuilding() throws IOException {
		runTest("test_unknown_building");
	}
	
	@Test
	public void testSameBuilding() throws IOException {
		runTest("test_same_building");
	}
	
	@Test
	public void testMenu() throws IOException {
		runTest("test_menu");
	}
	
	@Test
	public void testUnknownOption() throws IOException {
		runTest("test_unknown_option");
	}
	
	@Test
	public void testMainMethodWithIOException() {
		// Test handling of IOException in main method
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		InputStream originalIn = System.in;
		System.setOut(new PrintStream(outContent));
		
		// Create input with "q" to exit immediately
		ByteArrayInputStream inContent = new ByteArrayInputStream("q\n".getBytes());
		System.setIn(inContent);
		
		// Simulate an IOException by providing a non-existent file path
		String originalNodesFile = System.getProperty("hw7.nodes.file");
		System.setProperty("hw7.nodes.file", "non_existent_file.csv");
		
		assertDoesNotThrow(() -> {
			CampusPaths.main(null);
		}, "Main method should handle IOException gracefully");
		
		// Restore original properties and output stream
		if (originalNodesFile != null) {
			System.setProperty("hw7.nodes.file", originalNodesFile);
		} else {
			System.clearProperty("hw7.nodes.file");
		}
		System.setOut(originalOut);
		System.setIn(originalIn);
	}
	
	@Test
	public void testEmptyUserInput() {
		// Test handling of empty user input
		InputStream originalIn = System.in;
		PrintStream originalOut = System.out;
		
		// Simulate empty input followed by quit
		String simulatedInput = "\nq\n";
		ByteArrayInputStream inContent = new ByteArrayInputStream(simulatedInput.getBytes());
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		
		System.setIn(inContent);
		System.setOut(new PrintStream(outContent));
		
		assertDoesNotThrow(() -> {
			CampusPaths.main(null);
		}, "Should handle empty input gracefully");
		
		// Restore original streams
		System.setIn(originalIn);
		System.setOut(originalOut);
	}

    @Test
    public void testMainIOExceptionOnModelLoad() {
        // Temporarily set properties to point to non-existent files
        String originalNodes = System.getProperty("hw7.nodes.file", "data/RPI_map_data_Nodes.csv");
        String originalEdges = System.getProperty("hw7.edges.file", "data/RPI_map_data_Edges.csv");
        System.setProperty("hw7.nodes.file", "non_existent_nodes.csv");
        System.setProperty("hw7.edges.file", "non_existent_edges.csv");

        // Redirect System.err to capture output
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));
        InputStream originalIn = System.in;
        // Provide 'q' to prevent blocking on input if startup somehow proceeds
        System.setIn(new ByteArrayInputStream("q\n".getBytes())); 

        try {
            assertDoesNotThrow(() -> {
                CampusPaths.main(null);
            }, "Main should catch IOException during model load");
            
            // Check if the specific error message was printed to System.err
            String errOutput = errContent.toString();
            // Check for *part* of the message as file path might vary
            assertTrue(errOutput.contains("Error loading campus data"), 
                       "Expected IOException error message on System.err");

        } finally {
            // Restore original properties and streams
            System.setProperty("hw7.nodes.file", originalNodes);
            System.setProperty("hw7.edges.file", originalEdges);
            System.setErr(originalErr);
            System.setIn(originalIn);
            System.clearProperty("hw7.nodes.file"); // Clear if it wasn't originally set
            System.clearProperty("hw7.edges.file"); // Clear if it wasn't originally set
        }
    }

    @Test
    public void testMainNoSuchElementException() {
        // Input that provides 'r' but cuts off before the second building name
        String simulatedInput = "r\n1\n"; 
        InputStream originalIn = System.in;
        // Use ByteArrayInputStream which will signal EOF after reading the input
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes())); 

        // Capture System.err to verify the correct catch block was hit
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));
        
        // Redirect System.out to avoid cluttering the test logs
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream())); 

        try {
            // Execute main - it should not throw an uncaught exception
            assertDoesNotThrow(() -> {
                CampusPaths.main(null);
            }, "Main should catch NoSuchElementException during run");

            // Verify that the specific error message for NoSuchElementException was printed
            String errOutput = errContent.toString();
            assertTrue(errOutput.contains("Error reading input"), 
                       "Expected NoSuchElementException error message on System.err. Got: " + errOutput);

        } finally {
            // IMPORTANT: Restore original System streams
            System.setIn(originalIn);
            System.setErr(originalErr);
            System.setOut(originalOut);
        }
    }

    @Test
    public void testUnknownOptionDirectInputSimulation() {
        // 1. Prepare simulated input (invalid option 'a', then quit 'q')
        String simulatedInput = "a\nq\n";
        InputStream originalIn = System.in;
        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        // 2. Prepare to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // 3. Prepare to capture errors (optional but good practice)
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        CampusModel model = null;
        CampusPaths.CampusPathsController controller = null;

        try {
            // 4. Setup model and controller (handle potential IOExceptions during model load)
             model = new CampusModel(); // Assuming default constructor is sufficient
             // Assuming CampusPaths has a default constructor or adjust as needed
             CampusPaths campusPathsInstance = new CampusPaths(); 
             // Create controller instance - needs CampusPaths instance and model
             controller = new CampusPaths.CampusPathsController(campusPathsInstance, model); 

            // 5. Run the controller's loop directly
            controller.run();

            // 6. Assert the captured output
            String output = outContent.toString();
            // Normalize line endings for cross-platform compatibility
            String expectedOutputPart = "Unknown option" + System.lineSeparator(); 
            assertTrue(output.contains(expectedOutputPart), 
                "Expected output to contain 'Unknown option'. Actual output:\n" + output);

        } catch (IOException e) {
            // Fail the test if model loading itself fails unexpectedly
             org.junit.jupiter.api.Assertions.fail("IOException during model setup: " + e.getMessage());
        } finally {
            // 7. IMPORTANT: Restore original streams
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);
            // Close the controller's scanner if it was created
            if (controller != null) {
                 controller.close();
            }
        }
    }
}
