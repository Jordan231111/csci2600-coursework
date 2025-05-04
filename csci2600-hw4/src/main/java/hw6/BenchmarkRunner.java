package hw6;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Handles the execution of LegoPaths benchmarks.
 */
public class BenchmarkRunner {
    private static final int NUM_PATH_OPERATIONS = 20;
    private final String dataDir;
    private final LegoPaths legoPaths;
    
    // Control output in test environments
    private static boolean isVerboseMode() {
        // Forcibly disable all debug output in tests
        return false;
    }

    /**
     * Creates a new BenchmarkRunner for the specified data directory.
     * 
     * @param dataDir The directory containing the data files.
     */
    public BenchmarkRunner(String dataDir) {
        this.dataDir = dataDir;
        this.legoPaths = new LegoPaths();
    }

    /**
     * Runs a benchmark for a single data file.
     *
     * @param relativeFilePath The path to the data file, relative to the data directory.
     * @return A BenchmarkResult object containing the metrics, or null if the file doesn't exist.
     */
    public BenchmarkResult runSingleBenchmark(String relativeFilePath) {
        String fullPath = dataDir + relativeFilePath;
        File file = new File(fullPath);
        if (!file.exists()) {
            if (isVerboseMode()) {
                System.err.println("Benchmark Error: File not found - " + fullPath);
            }
            return null;
        }

        // Measure graph creation time
        long startGraphTime = System.nanoTime();
        legoPaths.createNewGraph(fullPath);
        long graphCreationTimeNs = System.nanoTime() - startGraphTime;

        // Get all part names
        List<String> allParts = extractAvailableParts(legoPaths);
        if (allParts.isEmpty()) {
            if (isVerboseMode()) {
                System.out.println("  No parts found in the graph for " + relativeFilePath + ". Skipping path finding.");
            }
            return new BenchmarkResult(fullPath, nsToMs(graphCreationTimeNs), 0.0, 0, 0, false);
        }

        // Measure path finding time with random part pairs
        Random random = new Random(42); // Fixed seed for reproducibility
        long totalPathFindingTimeNs = 0;
        int successfulPathsCount = 0;

        for (int i = 0; i < NUM_PATH_OPERATIONS; i++) {
            String startPart = allParts.get(random.nextInt(allParts.size()));
            String endPart = allParts.get(random.nextInt(allParts.size()));

            long startPathTime = System.nanoTime();
            String result = legoPaths.findPath(startPart, endPart);
            long pathTimeNs = System.nanoTime() - startPathTime;

            totalPathFindingTimeNs += pathTimeNs;

            if (result != null && !result.contains("no path found") && !result.contains("unknown part")) {
                successfulPathsCount++;
            }
        }

        // Calculate average path time
        double avgPathFindingTimeMs = 0.0;
        if (NUM_PATH_OPERATIONS > 0) {
            avgPathFindingTimeMs = nsToMs(totalPathFindingTimeNs) / NUM_PATH_OPERATIONS;
        }

        // Return populated result object
        return new BenchmarkResult(
            fullPath,
            nsToMs(graphCreationTimeNs),
            avgPathFindingTimeMs,
            successfulPathsCount,
            NUM_PATH_OPERATIONS,
            true
        );
    }

    /**
     * Extracts the available parts from the LegoPaths instance.
     * 
     * @param paths The LegoPaths instance containing the graph.
     * @return A List of part names.
     */
    protected List<String> extractAvailableParts(LegoPaths paths) {
        Set<String> partsSet = paths.getAllParts();
        if (partsSet == null || partsSet.isEmpty() || (partsSet.size() == 1 && partsSet.contains(""))) { 
            return new ArrayList<>();
        }
        return new ArrayList<>(partsSet);
    }

    /**
    * Converts nanoseconds to milliseconds.
    * @param nanoseconds Time in nanoseconds.
    * @return Time in milliseconds.
    */
    private static double nsToMs(long nanoseconds) {
       return nanoseconds / 1_000_000.0;
    }
} 