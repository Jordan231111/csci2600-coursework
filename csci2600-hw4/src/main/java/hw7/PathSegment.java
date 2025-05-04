package hw7;

/**
 * Immutable class representing a segment of a path between buildings.
 * Contains the direction, destination name, and distance.
 */
public class PathSegment {
    // This class is not an ADT.
    private final String direction;
    private final String destination;
    private final double distance;
    
    /**
     * Constructs a new PathSegment.
     * 
     * @param direction The direction of travel (North, NorthEast, etc.)
     * @param destination The name of the destination (building name or "Intersection X")
     * @param distance The distance in pixel units
     */
    public PathSegment(String direction, String destination, double distance) {
        this.direction = direction;
        this.destination = destination;
        this.distance = distance;
    }
    
    /**
     * @return The direction of travel
     */
    public String getDirection() {
        return direction;
    }
    
    /**
     * @return The name of the destination
     */
    public String getDestination() {
        return destination;
    }
    
    /**
     * @return The distance in pixel units
     */
    public double getDistance() {
        return distance;
    }
} 