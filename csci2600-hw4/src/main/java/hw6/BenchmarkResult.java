package hw6;

/**
 * Contains the results of a single benchmark run.
 */
public class BenchmarkResult {
    public final String filePath;
    public final double graphCreationTime;
    public final double averagePathFindingTime;
    public final int successfulPaths;
    public final int totalPathOperations;
    public final boolean partsFound;

    /**
     * Creates a new BenchmarkResult with the specified metrics.
     * 
     * @param filePath The path to the benchmarked file
     * @param graphCreationTime Time taken to create the graph in milliseconds
     * @param averagePathFindingTime Average time to find a path in milliseconds
     * @param successfulPaths Number of successful path operations
     * @param totalPathOperations Total number of path operations attempted
     * @param partsFound Whether any parts were found in the graph
     */
    public BenchmarkResult(String filePath, double graphCreationTime, double averagePathFindingTime, 
                          int successfulPaths, int totalPathOperations, boolean partsFound) {
        this.filePath = filePath;
        this.graphCreationTime = graphCreationTime;
        this.averagePathFindingTime = averagePathFindingTime;
        this.successfulPaths = successfulPaths;
        this.totalPathOperations = totalPathOperations;
        this.partsFound = partsFound;
    }
} 