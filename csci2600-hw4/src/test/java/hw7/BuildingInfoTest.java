package hw7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Tests for the BuildingInfo class.
 */
public class BuildingInfoTest {
    
    @Test
    public void testBuildingInfoConstructorAndGetters() {
        BuildingInfo buildingInfo = new BuildingInfo("Test Building", "42");
        
        // Test getters
        assertEquals("42", buildingInfo.getId(), "getId should return the correct id");
        assertEquals("Test Building", buildingInfo.getName(), "getName should return the correct name");
    }
} 