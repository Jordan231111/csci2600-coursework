package hw7;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the CampusModel class.
 */
public class CampusModelTest {
    
    private CampusModel model;
    
    @BeforeEach
    public void setUp() throws IOException {
        model = new CampusModel();
    }
    
    @Test
    public void testGetAllBuildings() {
        List<CampusModel.BuildingInfo> buildings = model.getAllBuildings();
        
        // Check that the list is not empty
        assertFalse(buildings.isEmpty(), "Building list should not be empty");
        
        // Check that the buildings are sorted by name
        for (int i = 0; i < buildings.size() - 1; i++) {
            String name1 = buildings.get(i).getName();
            String name2 = buildings.get(i + 1).getName();
            assertTrue(name1.compareTo(name2) <= 0, 
                    "Buildings should be sorted by name: " + name1 + " should come before " + name2);
        }
        
        // Check that some known buildings are in the list
        boolean foundEmpac = false;
        boolean foundAcademyHall = false;
        
        for (CampusModel.BuildingInfo building : buildings) {
            if (building.getName().equals("EMPAC")) {
                foundEmpac = true;
            } else if (building.getName().equals("Academy Hall")) {
                foundAcademyHall = true;
            }
        }
        
        assertTrue(foundEmpac, "EMPAC should be in the building list");
        assertTrue(foundAcademyHall, "Academy Hall should be in the building list");
    }
    
    @Test
    public void testFindBuilding() {
        // Test finding by ID
        CampusModel.Building building76 = model.findBuilding("76");
        assertNotNull(building76, "Building with ID 76 should exist");
        assertEquals("EMPAC", building76.getName(), "Building with ID 76 should be EMPAC");
        
        // Test finding by name
        CampusModel.Building empac = model.findBuilding("EMPAC");
        assertNotNull(empac, "Building with name EMPAC should exist");
        assertEquals("76", empac.getId(), "EMPAC should have ID 76");
        
        // Test case-insensitive name lookup
        CampusModel.Building empacLower = model.findBuilding("empac");
        assertNotNull(empacLower, "Case-insensitive lookup should work");
        assertEquals("76", empacLower.getId(), "Case-insensitive EMPAC should have ID 76");
        
        // Test non-existent building
        assertNull(model.findBuilding("NonExistentBuilding"), 
                "Non-existent building should return null");
    }
    
    @Test
    public void testFindShortestPath() {
        // Get buildings
        CampusModel.Building empac = model.findBuilding("EMPAC");
        CampusModel.Building academyHall = model.findBuilding("Academy Hall");
        
        // Find path from EMPAC to Academy Hall
        List<CampusModel.PathSegment> path = model.findShortestPath(empac, academyHall);
        
        // Check that the path is not null
        assertNotNull(path, "Path from EMPAC to Academy Hall should exist");
        
        // Check that the path has segments
        assertFalse(path.isEmpty(), "Path from EMPAC to Academy Hall should have segments");
        
        // Check total distance
        double totalDistance = model.getTotalDistance(path);
        assertTrue(totalDistance > 0, "Total distance should be positive");
    }
    
    @Test
    public void testFindShortestPathSameBuilding() {
        // Get a building
        CampusModel.Building empac = model.findBuilding("EMPAC");
        
        // Find path from EMPAC to EMPAC
        List<CampusModel.PathSegment> path = model.findShortestPath(empac, empac);
        
        // Check that the path is not null
        assertNotNull(path, "Path from building to itself should not be null");
        
        // Check that the path is empty (no segments)
        assertTrue(path.isEmpty(), "Path from building to itself should be empty");
        
        // Check total distance is 0
        double totalDistance = model.getTotalDistance(path);
        assertEquals(0.0, totalDistance, 0.001, 
                "Distance of path from building to itself should be 0");
    }
    
    @Test
    public void testFindShortestPathNoPath() {
        // Get buildings
        CampusModel.Building linac = model.findBuilding("LINAC Facility");
        CampusModel.Building radioClub = model.findBuilding("Radio Club");
        
        // Find path from LINAC to Radio Club (assuming no path exists)
        List<CampusModel.PathSegment> path = model.findShortestPath(linac, radioClub);
        
        // Check that the path is null (no path exists)
        assertNull(path, "No path should exist between LINAC and Radio Club");
        
        // Check total distance is NaN
        double totalDistance = model.getTotalDistance(path);
        assertTrue(Double.isNaN(totalDistance), 
                "Total distance for non-existent path should be NaN");
    }
    
    @Test
    public void testCalculateDirection() {
        // Get buildings with known relative positions
        CampusModel.Building westHall = model.findBuilding("West Hall");
        CampusModel.Building dcc = model.findBuilding("DCC");
        
        // Find path from West Hall to DCC
        List<CampusModel.PathSegment> path = model.findShortestPath(westHall, dcc);
        
        // Check that the path is not null
        assertNotNull(path, "Path from West Hall to DCC should exist");
        assertFalse(path.isEmpty(), "Path from West Hall to DCC should have segments");
        
        // Verify at least one segment has a valid direction
        boolean hasValidDirection = false;
        for (CampusModel.PathSegment segment : path) {
            String direction = segment.getDirection();
            if (direction.equals("North") || direction.equals("NorthEast") || 
                direction.equals("East") || direction.equals("SouthEast") || 
                direction.equals("South") || direction.equals("SouthWest") || 
                direction.equals("West") || direction.equals("NorthWest")) {
                hasValidDirection = true;
                break;
            }
        }
        assertTrue(hasValidDirection, "Path should have at least one segment with a valid direction");
    }
    
    @Test
    public void testUnknownBuilding() {
        assertNull(model.findBuilding("Unknown Building"), 
                "Unknown building should return null");
        assertNull(model.findBuilding("999"), 
                "Unknown building ID should return null");
    }
    
    @Test
    public void testFindBuildingWithNullOrEmpty() {
        assertNull(model.findBuilding(null), 
                "Null building name/ID should return null");
        assertNull(model.findBuilding(""), 
                "Empty building name/ID should return null");
    }
    
    @Test
    public void testFindShortestPathWithNullBuildings() {
        CampusModel.Building empac = model.findBuilding("EMPAC");
        assertNull(model.findShortestPath(null, empac), 
                "Path with null start building should return null");
        assertNull(model.findShortestPath(empac, null), 
                "Path with null end building should return null");
        assertNull(model.findShortestPath(null, null), 
                "Path with both null buildings should return null");
    }
} 