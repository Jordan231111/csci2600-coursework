package hw7;

import java.util.List;
import java.util.Scanner;

/**
 * Text-based user interface for the RPI Campus Paths application.
 * This class combines the view and controller roles in the MVC pattern.
 * 
 * NOTE: This class is not used in the current implementation.
 * The functionality has been moved to CampusPaths.java which contains
 * the main method and controller implementation.
 */
public class CampusPathsTextUI {
    // The campus model
    private final CampusModel model;
    
    // Scanner for reading user input
    private final Scanner scanner;
    
    /**
     * Constructs a new CampusPathsTextUI.
     * 
     * @param model The campus model
     */
    public CampusPathsTextUI(CampusModel model) {
        this.model = model;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Runs the text-based user interface.
     */
    public void run() {
        boolean running = true;
        
        while (running) {
            String command = scanner.nextLine().trim();
            
            switch (command) {
                case "b":
                    listBuildings();
                    break;
                case "r":
                    findRoute();
                    break;
                case "m":
                    showMenu();
                    break;
                case "q":
                    running = false;
                    break;
                default:
                    System.out.println("Unknown option");
                    break;
            }
        }
    }
    
    /**
     * Lists all buildings on campus in alphabetical order.
     * This method is part of the VIEW role.
     */
    private void listBuildings() {
        List<CampusModel.BuildingInfo> buildings = model.getAllBuildings();
        
        for (CampusModel.BuildingInfo building : buildings) {
            System.out.println(building.getName() + "," + building.getId());
        }
    }
    
    /**
     * Finds the shortest route between two buildings.
     * This method combines CONTROLLER and VIEW roles.
     */
    private void findRoute() {
        // Prompt for first building (CONTROLLER)
        System.out.println("First building id/name, followed by Enter: ");
        String building1 = scanner.nextLine().trim();
        
        // Prompt for second building (CONTROLLER)
        System.out.println("Second building id/name, followed by Enter: ");
        String building2 = scanner.nextLine().trim();
        
        // Look up buildings in the model (CONTROLLER)
        CampusModel.Building start = model.findBuilding(building1);
        CampusModel.Building end = model.findBuilding(building2);
        
        // Check if buildings were found (CONTROLLER)
        boolean startFound = (start != null);
        boolean endFound = (end != null);
        
        // Display error messages if buildings were not found (VIEW)
        if (!startFound) {
            System.out.println("Unknown building: [" + building1 + "]");
        }
        
        if (!endFound) {
            System.out.println("Unknown building: [" + building2 + "]");
        }
        
        // If both buildings were found, find the shortest path (CONTROLLER)
        if (startFound && endFound) {
            List<CampusModel.PathSegment> path = model.findShortestPath(start, end);
            
            // Display the path (VIEW)
            if (path != null) {
                System.out.println("Path from " + start.getName() + " to " + end.getName() + ":");
                
                // Display each segment of the path
                for (CampusModel.PathSegment segment : path) {
                    System.out.println("\tWalk " + segment.getDirection() + " to (" + segment.getEnd().getName() + ")");
                }
                
                // Display the total distance
                double totalDistance = model.getTotalDistance(path);
                System.out.printf("Total distance: %.3f pixel units. ", totalDistance);
            } else {
                // No path was found
                System.out.println("There is no path from " + start.getName() + " to " + end.getName() + ". ");
            }
        }
    }
    
    /**
     * Shows the menu of available commands.
     * This method is part of the VIEW role.
     */
    private void showMenu() {
        System.out.println("Menu:");
        System.out.println("b: List all buildings");
        System.out.println("r: Find a route between two buildings");
        System.out.println("m: Show this menu");
        System.out.println("q: Quit");
    }
} 