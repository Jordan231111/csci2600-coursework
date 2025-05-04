package hw6;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * A command-line benchmark utility for LegoPaths.
 * Uses BenchmarkRunner to perform the actual benchmarking.
 */
public class BenchmarkLegoPaths {

    private static final String DATA_DIR = "data/";
    private static final String[] TEST_FILES = {
        "hw6_test_basic.csv",
        "hw6_test_multi_shared.csv",
        "hw6_test_no_path.csv",
        "hw6_test_performance.csv"
        // Add more test files if needed
    };
    
    // Control output in test environments
    private static boolean isVerboseMode() {
        // Check for a system property that might be set for testing
        if ("true".equals(System.getProperty("benchmark.verbose"))) {
            return true;
        }
        // Forcibly disable all debug output in tests
        return false;
    }

    /**
     * Main entry point for the benchmark utility.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Warm up the JVM
        warmUp();

        if (isVerboseMode()) {
            System.out.println("=== LegoPaths Performance Benchmark ===");
            System.out.println("Measuring graph loading and path finding times\n");
        }

        BenchmarkRunner runner = new BenchmarkRunner(DATA_DIR);

        // Run benchmarks for each test file using the runner
        for (String testFile : TEST_FILES) {
            if (isVerboseMode()) {
                System.out.println("Benchmarking with " + testFile + ":");
            }
            BenchmarkResult result = runner.runSingleBenchmark(testFile);

            if (result != null && isVerboseMode()) {
                System.out.println("  Graph creation time: " + String.format("%.2f", result.graphCreationTime) + " ms");
                if (result.partsFound) {
                    System.out.println("  Finding " + result.totalPathOperations + " random paths...");
                    System.out.println("  Average path finding time: " + String.format("%.2f", result.averagePathFindingTime) + " ms");
                    System.out.println("  Successful paths: " + result.successfulPaths + "/" + result.totalPathOperations);
                }
            } else if (isVerboseMode()) {
                System.out.println("  Skipped (file not found or error).");
            }
            
            if (isVerboseMode()) {
                System.out.println();
            }
        }
    }

    /**
     * Warm-up method to initialize the JVM for more consistent performance measurements.
     */
    private static void warmUp() {
        if (isVerboseMode()) {
            System.out.println("Performing warm-up...");
        }
        
        LegoPaths warmUpInstance = new LegoPaths();
        File tempWarmupDir = new File(DATA_DIR);
        File warmupFile = new File(tempWarmupDir, "warmup.csv");

        try {
            // Ensure DATA_DIR exists before writing the warmup file
            if (!tempWarmupDir.exists()) {
                if (!tempWarmupDir.mkdirs()) {
                    if (isVerboseMode()) {
                        System.err.println("Warm-up Error: Could not create data directory: " + DATA_DIR);
                    }
                    return;
                }
            }

            // Create a minimal test file for warm-up
            String warmupContent = "\"WarmUpPart1\",\"WarmUpSet1\"\n" +
                                "\"WarmUpPart2\",\"WarmUpSet1\"\n";

            Files.writeString(warmupFile.toPath(), warmupContent);

            // Run warm-up operations
            warmUpInstance.createNewGraph(warmupFile.getAbsolutePath());
            warmUpInstance.findPath("WarmUpPart1", "WarmUpPart2");

            if (isVerboseMode()) {
                System.out.println("Warm-up complete.");
            }

        } catch (IOException e) {
            if (isVerboseMode()) {
                System.err.println("Warm-up IO Error (ignorable): " + e.getMessage());
            }
        } catch (Exception e) {
            if (isVerboseMode()) {
                System.err.println("Warm-up Error (ignorable): " + e.getMessage());
            }
        } finally {
            // Clean up the temporary file if it was created
            if (warmupFile.exists()) {
                if (!warmupFile.delete() && isVerboseMode()) {
                    System.err.println("Warm-up Warning: Could not delete temporary file: " + warmupFile.getAbsolutePath());
                }
            }
        }
    }
} 