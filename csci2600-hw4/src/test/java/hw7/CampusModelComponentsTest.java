package hw7;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the nested classes in CampusModel.
 */
public class CampusModelComponentsTest {
    
    private CampusModel model;
    
    @BeforeEach
    public void setUp() throws IOException {
        model = new CampusModel();
    }
    
    @Test
    public void testBuildingMethodsAndToString() {
        CampusModel.Building building = model.findBuilding("EMPAC");
        
        // Test getters
        assertEquals("76", building.getId(), "getId should return the correct ID");
        assertEquals("EMPAC", building.getName(), "getName should return the correct name");
        assertTrue(building.getX() > 0, "X coordinate should be positive");
        assertTrue(building.getY() > 0, "Y coordinate should be positive");
        
        // Test toString
        assertEquals("EMPAC", building.toString(), "toString should return the building name");
    }
    
    @Test
    public void testMapNodeEqualsAndHashCode() throws Exception {
        // Create a simple test using reflection to access MapNode
        java.lang.reflect.Constructor<?> constructor = 
            Class.forName("hw7.CampusModel$MapNode").getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        
        Object node1 = constructor.newInstance(model, "1", 10.0, 20.0, "Building1");
        Object node2 = constructor.newInstance(model, "1", 30.0, 40.0, "Building1-alt");
        Object node3 = constructor.newInstance(model, "2", 10.0, 20.0, "Building2");
        
        // Test equals method
        assertTrue(node1.equals(node1), "Node should equal itself");
        assertTrue(node1.equals(node2), "Nodes with same ID should be equal");
        assertFalse(node1.equals(node3), "Nodes with different IDs should not be equal");
        assertFalse(node1.equals(null), "Node should not equal null");
        assertFalse(node1.equals("not a node"), "Node should not equal non-node objects");
        
        // Test hashCode
        assertEquals(node1.hashCode(), node2.hashCode(), 
                "Nodes with same ID should have same hash code");
        assertNotEquals(node1.hashCode(), node3.hashCode(), 
                "Nodes with different IDs should have different hash codes");
    }
    
    @Test
    public void testMapNodeToString() throws Exception {
        // Create test nodes using reflection
        java.lang.reflect.Constructor<?> constructor = 
            Class.forName("hw7.CampusModel$MapNode").getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        
        Object buildingNode = constructor.newInstance(model, "1", 10.0, 20.0, "Test Building");
        Object intersectionNode = constructor.newInstance(model, "2", 30.0, 40.0, "");
        
        // Test toString for building node
        assertEquals("Test Building", buildingNode.toString(), 
                "Building node toString should return building name");
        
        // Test toString for intersection node
        assertEquals("Intersection 2", intersectionNode.toString(), 
                "Intersection node toString should return 'Intersection' followed by ID");
    }
    
    @Test
    public void testPathSegmentGetters() throws IOException {
        // Get buildings for a path
        CampusModel.Building start = model.findBuilding("EMPAC");
        CampusModel.Building end = model.findBuilding("Academy Hall");
        
        // Find path
        java.util.List<CampusModel.PathSegment> path = model.findShortestPath(start, end);
        if (path != null && !path.isEmpty()) {
            CampusModel.PathSegment segment = path.get(0);
            
            // Test getters
            assertNotEquals(null, segment.getStart(), "getStart should not return null");
            assertNotEquals(null, segment.getEnd(), "getEnd should not return null");
            assertTrue(segment.getDistance() > 0, "getDistance should return positive value");
            assertNotEquals("", segment.getDirection(), "getDirection should not return empty string");
        }
    }
} 