package hw6;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 * Simple test to verify package structure.
 */
public class SimpleTest {
    
    @Test
    public void testLegoPaths() {
        LegoPaths legoPaths = new LegoPaths();
        assertNotNull(legoPaths);
    }
    
    @Test
    public void testBenchmarkClasses() {
        BenchmarkResult result = new BenchmarkResult("test", 1.0, 2.0, 3, 4, true);
        assertNotNull(result);
        
        BenchmarkRunner runner = new BenchmarkRunner("data/");
        assertNotNull(runner);
    }
} 