package hw6;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import hw4.Graph;

/**
 * Test to verify hw4 imports work correctly.
 */
public class Hw4ImportTest {
    
    @Test
    public void testGraph() {
        Graph<String, String> graph = new Graph<>();
        assertNotNull(graph);
    }
} 