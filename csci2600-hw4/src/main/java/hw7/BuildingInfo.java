package hw7;

/**
 * Immutable class representing basic information about a building.
 * Used to communicate between the model and view layers.
 */
public class BuildingInfo {
    // This class is not an ADT.
    private final String name;
    private final String id;
    
    /**
     * Constructs a new BuildingInfo.
     * 
     * @param name The building's name
     * @param id The building's unique ID
     */
    public BuildingInfo(String name, String id) {
        this.name = name;
        this.id = id;
    }
    
    /**
     * @return The building's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return The building's unique ID
     */
    public String getId() {
        return id;
    }
} 