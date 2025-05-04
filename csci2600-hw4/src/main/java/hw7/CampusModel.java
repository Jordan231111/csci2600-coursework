package hw7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hw4.Graph;
import hw6.GraphAlgorithms;
import hw6.GraphAlgorithms.PathEdge;
import hw6.GraphAlgorithms.PathResult;

/**
 * A model for the campus pathfinding application.
 * Represents the data and operations for finding paths between buildings on campus.
 * This class is part of the Model in the MVC design pattern.
 */
public class CampusModel {
    // This class is not an ADT.
    
    // Constants for file paths
    // Allow overriding via system properties for testing
    private static final String DEFAULT_NODES_FILE = "data/RPI_map_data_Nodes.csv";
    private static final String DEFAULT_EDGES_FILE = "data/RPI_map_data_Edges.csv";
    
    // Constants for direction calculations
    private static final double NORTH_START = 337.5; // 360 - 22.5
    private static final double NORTH_END = 22.5;
    private static final double NORTHEAST_END = 67.5;
    private static final double EAST_END = 112.5;
    private static final double SOUTHEAST_END = 157.5;
    private static final double SOUTH_END = 202.5;
    private static final double SOUTHWEST_END = 247.5;
    private static final double WEST_END = 292.5;
    private static final double NORTHWEST_END = 337.5;
    
    /**
     * Represents a node (building or intersection) on the campus map.
     */
    public class MapNode {
        // This class is not an ADT.

        private final String id;
        private final double x;
        private final double y;
        private final String name;
        private final boolean isBuilding;
        
        /**
         * Creates a new MapNode.
         * 
         * @param id The ID of the node
         * @param x The x-coordinate of the node
         * @param y The y-coordinate of the node
         * @param name The name of the node (may be empty for intersections)
         */
        public MapNode(String id, double x, double y, String name) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.name = name;
            this.isBuilding = !name.isEmpty();
        }
        
        /**
         * @return The ID of the node
         */
        public String getId() {
            return id;
        }
        
        /**
         * @return The x-coordinate of the node
         */
        public double getX() {
            return x;
        }
        
        /**
         * @return The y-coordinate of the node
         */
        public double getY() {
            return y;
        }
        
        /**
         * @return The name of the node (empty string for intersections)
         */
        public String getName() {
            return name;
        }
        
        /**
         * @return Whether this node represents a building (true) or an intersection (false)
         */
        public boolean isBuilding() {
            return isBuilding;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof MapNode)) return false;
            MapNode other = (MapNode) obj;
            return id.equals(other.id);
        }
        
        @Override
        public int hashCode() {
            return id.hashCode();
        }
        
        @Override
        public String toString() {
            return isBuilding ? name : "Intersection " + id;
        }
    }
    
    /**
     * Represents a building on campus. Used in the public API.
     */
    public class Building {
        // This class is not an ADT.

        private final String id;
        private final String name;
        private final double x;
        private final double y;
        
        /**
         * Creates a new Building.
         * 
         * @param id The ID of the building
         * @param name The name of the building
         * @param x The x-coordinate of the building
         * @param y The y-coordinate of the building
         */
        public Building(String id, String name, double x, double y) {
            this.id = id;
            this.name = name;
            this.x = x;
            this.y = y;
        }
        
        /**
         * @return The ID of the building
         */
        public String getId() {
            return id;
        }
        
        /**
         * @return The name of the building
         */
        public String getName() {
            return name;
        }
        
        /**
         * @return The x-coordinate of the building
         */
        public double getX() {
            return x;
        }
        
        /**
         * @return The y-coordinate of the building
         */
        public double getY() {
            return y;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    /**
     * Represents a segment (edge) in a path, with information about direction and destination.
     */
    public class PathSegment {
        // This class is not an ADT.

        private final MapNode start;
        private final MapNode end;
        private final double distance;
        private final String direction;
        
        /**
         * Creates a new PathSegment.
         * 
         * @param start The starting node
         * @param end The ending node
         * @param distance The distance of this segment
         * @param direction The direction of this segment
         */
        public PathSegment(MapNode start, MapNode end, double distance, String direction) {
            this.start = start;
            this.end = end;
            this.distance = distance;
            this.direction = direction;
        }
        
        /**
         * @return The starting node
         */
        public MapNode getStart() {
            return start;
        }
        
        /**
         * @return The ending node
         */
        public MapNode getEnd() {
            return end;
        }
        
        /**
         * @return The distance of this segment
         */
        public double getDistance() {
            return distance;
        }
        
        /**
         * @return The direction of this segment
         */
        public String getDirection() {
            return direction;
        }
    }
    
    /**
     * Information about a building, returned as part of the public API.
     */
    public class BuildingInfo {
        // This class is not an ADT.
        
        private final String id;
        private final String name;
        
        /**
         * Creates a new BuildingInfo.
         * 
         * @param id The ID of the building
         * @param name The name of the building
         */
        public BuildingInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        /**
         * @return The ID of the building
         */
        public String getId() {
            return id;
        }
        
        /**
         * @return The name of the building
         */
        public String getName() {
            return name;
        }
    }
    
    // The graph representing the campus map
    private final Graph<MapNode, Double> graph;
    
    // Maps for quick lookup of nodes
    private final Map<String, MapNode> nodeById;
    private final Map<String, MapNode> nodeByName;
    
    /**
     * Creates a new CampusModel and initializes it by loading data from the CSV files.
     * 
     * @throws IOException if there is an error reading the data files
     */
    public CampusModel() throws IOException {
        graph = new Graph<>();
        nodeById = new HashMap<>();
        nodeByName = new HashMap<>();
        
        loadData();
    }
    
    /**
     * Loads data from the CSV files and builds the graph.
     * 
     * @throws IOException if there is an error reading the data files
     */
    private void loadData() throws IOException {
        // Load nodes (buildings and intersections)
        String nodesFile = System.getProperty("hw7.nodes.file", DEFAULT_NODES_FILE);
        String edgesFile = System.getProperty("hw7.edges.file", DEFAULT_EDGES_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(nodesFile))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                
                // Format: Name,id,x-coordinate,y-coordinate
                String name = parts[0];
                String id = parts[1];
                double x = Double.parseDouble(parts[2]);
                double y = Double.parseDouble(parts[3]);
                
                MapNode node = new MapNode(id, x, y, name);
                graph.addNode(node);
                nodeById.put(id, node);
                
                // Only add to name map if this is a building
                if (node.isBuilding()) {
                    nodeByName.put(name.toLowerCase(), node);
                }
            }
        }
        
        // Load edges
        try (BufferedReader reader = new BufferedReader(new FileReader(edgesFile))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                
                // Format: id1,id2
                String id1 = parts[0];
                String id2 = parts[1];
                
                MapNode node1 = nodeById.get(id1);
                MapNode node2 = nodeById.get(id2);
                
                if (node1 != null && node2 != null) {
                    // Calculate distance using Euclidean distance formula
                    double distance = calculateDistance(node1, node2);
                    
                    // Add edges in both directions (bi-directional pathways)
                    graph.addEdge(node1, node2, distance);
                    graph.addEdge(node2, node1, distance);
                }
            }
        }
    }
    
    /**
     * Calculates the Euclidean distance between two nodes.
     * 
     * @param node1 The first node
     * @param node2 The second node
     * @return The distance between the nodes
     */
    private double calculateDistance(MapNode node1, MapNode node2) {
        double dx = node2.getX() - node1.getX();
        double dy = node2.getY() - node1.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Gets all buildings on campus in alphabetical order by name.
     * 
     * @return A list of all buildings in alphabetical order
     */
    public List<BuildingInfo> getAllBuildings() {
        List<BuildingInfo> buildings = new ArrayList<>();
        
        for (MapNode node : graph.getNodes()) {
            if (node.isBuilding()) {
                // Constructor is BuildingInfo(id, name)
                BuildingInfo info = new BuildingInfo(node.getId(), node.getName()); 
                buildings.add(info);
            }
        }
        
        // Sort by name (case sensitive) first, then by ID if names are equal
        buildings.sort((b1, b2) -> {
            int nameCompare = b1.getName().compareTo(b2.getName());
            if (nameCompare != 0) {
                return nameCompare;
            }
            return b1.getId().compareTo(b2.getId()); 
        });

        return buildings;
    }
    
    /**
     * Finds a building by its ID or name (case-insensitive).
     * 
     * @param idOrName The ID or name of the building
     * @return The Building object if found, null otherwise
     */
    public Building findBuilding(String idOrName) {
        if (idOrName == null || idOrName.isEmpty()) {
            return null;
        }
        
        // Try as ID first
        MapNode node = nodeById.get(idOrName);
        
        // If not found by ID, try as name (case-insensitive)
        if (node == null || !node.isBuilding()) {
            node = nodeByName.get(idOrName.toLowerCase());
        }
        
        if (node != null && node.isBuilding()) {
            return new Building(node.getId(), node.getName(), node.getX(), node.getY());
        }
        
        return null;
    }
    
    /**
     * Finds the shortest path between two buildings.
     * 
     * @param start The starting building
     * @param end The destination building
     * @return A list of path segments representing the shortest path, or null if no path exists
     */
    public List<PathSegment> findShortestPath(Building start, Building end) {
        if (start == null || end == null) {
            return null;
        }
        
        // Handle the case where start and end are the same
        if (start.getId().equals(end.getId())) {
            return new ArrayList<>();
        }
        
        // Get the MapNode objects
        MapNode startNode = nodeById.get(start.getId());
        MapNode endNode = nodeById.get(end.getId());
        
        if (startNode == null || endNode == null) {
            return null;
        }
        
        // Find shortest path using Dijkstra's algorithm
        PathResult<MapNode> result = GraphAlgorithms.findShortestPath(graph, startNode, endNode);
        
        // If no path found, return null
        if (result.edges() == null) {
            return null;
        }
        
        // Convert to PathSegment objects with directions
        List<PathSegment> path = new ArrayList<>();
        
        for (PathEdge<MapNode> edge : result.edges()) {
            MapNode fromNode = edge.source();
            MapNode toNode = edge.destination();
            
            // Calculate direction
            String direction = calculateDirection(fromNode, toNode);
            
            PathSegment segment = new PathSegment(fromNode, toNode, edge.weight(), direction);
            path.add(segment);
        }
        
        return path;
    }
    
    /**
     * Calculates the direction from one node to another.
     * 
     * @param from The starting node
     * @param to The destination node
     * @return The direction as a string (North, NorthEast, East, etc.)
     */
    private String calculateDirection(MapNode from, MapNode to) {
        // Calculate the angle
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        
        // Convert to angle in degrees (0 is East, increasing counterclockwise)
        double angle = Math.toDegrees(Math.atan2(-dy, dx)); // Negative dy because y-axis points down
        
        // Convert to 0-360 range
        if (angle < 0) {
            angle += 360;
        }
        
        // Map angle to direction
        // Corrected direction mapping (assuming angle is 0-360, 0=East, 90=North, etc.)
        if (angle >= 337.5 || angle < 22.5) {
            return "East";
        } else if (angle < 67.5) {
            return "NorthEast";
        } else if (angle < 112.5) {
            return "North";
        } else if (angle < 157.5) {
            return "NorthWest";
        } else if (angle < 202.5) {
            return "West";
        } else if (angle < 247.5) {
            return "SouthWest";
        } else if (angle < 292.5) {
            return "South";
        } else { // angle < 337.5
            return "SouthEast";
        }
    }
    
    /**
     * Gets the total distance of a path.
     * 
     * @param path The path to calculate distance for
     * @return The total distance, or Double.NaN if the path is null
     */
    public double getTotalDistance(List<PathSegment> path) {
        if (path == null) {
            return Double.NaN;
        }
        
        double total = 0.0;
        for (PathSegment segment : path) {
            total += segment.getDistance();
        }
        
        return total;
    }
} 