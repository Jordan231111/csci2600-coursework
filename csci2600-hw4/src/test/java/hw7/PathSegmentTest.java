package hw7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Tests for the PathSegment class.
 */
public class PathSegmentTest {
    
    @Test
    public void testPathSegmentConstructorAndGetters() {
        PathSegment segment = new PathSegment("North", "Test Destination", 10.5);
        
        // Test getters
        assertEquals("North", segment.getDirection(), "getDirection should return the correct direction");
        assertEquals("Test Destination", segment.getDestination(), "getDestination should return the correct destination");
        assertEquals(10.5, segment.getDistance(), 0.001, "getDistance should return the correct distance");
    }
} 