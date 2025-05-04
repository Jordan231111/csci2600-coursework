package hw7;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The main class for the Campus Paths application.
 * This class is the entry point for the application and implements
 * both the controller and view components of the MVC pattern.
 */
public class CampusPaths {
    // This class is not an ADT.
    
    /**
     * Main method that starts the application.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        CampusPathsController controller = null;
        
        try {
            // Create model and controller
            CampusModel model = new CampusModel();
            CampusPaths instance = new CampusPaths();
            controller = new CampusPathsController(instance, model);
            
            // Start the application
            controller.run();
        } catch (IOException e) {
            System.err.println("Error loading campus data: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.err.println("Error reading input: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
        } finally {
            // Close any resources
            if (controller != null) {
                controller.close();
            }
        }
    }
    
    /**
     * Constructor for CampusPaths
     */
    public CampusPaths() {
        // Default constructor
    }
    
    /**
     * Controller class that handles user input and coordinates with the model.
     * This class is part of the Controller in the MVC design pattern.
     */
    static class CampusPathsController {
        // This class is not an ADT.
        
        private final CampusModel model;
        private final Scanner scanner;
        private boolean running;
        private final CampusPaths campusPaths;
        
        /**
         * Creates a new CampusPathsController.
         * 
         * @param campusPaths The CampusPaths instance
         * @param model The model to use
         */
        public CampusPathsController(CampusPaths campusPaths, CampusModel model) {
            this.campusPaths = campusPaths;
            this.model = model;
            this.scanner = new Scanner(System.in);
            this.running = true;
        }
        
        /**
         * Starts the main application loop.
         */
        public void run() {
            // printMenu(); // Don't display menu at startup for test compatibility
            while (running) {
                // Check if there is a next line before attempting to read it
                if (!scanner.hasNextLine()) { 
                    running = false; // Stop if no more input
                    break; // Exit loop cleanly
                }
                String command = scanner.nextLine().trim(); 
                
                // processCommand handles empty string, no need to check here
                processCommand(command);
            }
        }
        
        /**
         * Processes a user command.
         * 
         * @param command The command to process
         */
        private void processCommand(String command) {
            if (command.isEmpty() || (command.length() > 0 && command.charAt(0) != 'b' && command.charAt(0) != 'r' &&
                                     command.charAt(0) != 'q' && command.charAt(0) != 'm')) {
                System.out.println("Unknown option");
                // No return needed if it's unknown, just don't process further below
            } else if (command.length() > 0) {
                // Only process valid commands here (Need command.length() > 0 check again)
                char first = command.charAt(0);
                switch (first) {
                    case 'b':
                        listBuildings();
                        break;
                    case 'r':
                        findRoute();
                        break;
                    case 'q':
                        running = false; // Set running = false ONLY for 'q'
                        break;
                    case 'm':
                        printMenu();
                        break;
                    // Default case is now handled by the initial if condition
                }
            }
            // If command was empty, the first part of the outer if handles it.
        }
        
        /**
         * Lists all buildings in alphabetical order.
         * This method is part of the View in the MVC design pattern.
         */
        private void listBuildings() {
            List<CampusModel.BuildingInfo> buildings = model.getAllBuildings();
            
            for (CampusModel.BuildingInfo building : buildings) {
                System.out.printf("%s,%s%n", building.getName(), building.getId());
            }
        }
        
        /**
         * Prompts the user for two buildings and finds the shortest route between them.
         * This method contains both Controller and View components.
         */
        private void findRoute() {
            CampusModel.Building start = null;
            String startName = "";
            CampusModel.Building end = null;
            String endName = "";
            boolean eofDuringSecondRead = false; // Flag for specific EOF case

            try {
                // --- Read First Building --- 
                System.out.print("First building id/name, followed by Enter: ");
                if (!scanner.hasNextLine()) {
                    throw new NoSuchElementException("Input ended unexpectedly while waiting for first building.");
                }
                startName = scanner.nextLine().trim();
                start = model.findBuilding(startName); // Find start building

            } catch (NoSuchElementException e) {
                // If exception happens during first read, re-throw immediately
                // No need to print errors here as main will handle the exception.
                throw e;
            }

            try {
                // --- Read Second Building --- 
                System.out.print("Second building id/name, followed by Enter: ");
                if (!scanner.hasNextLine()) {
                    throw new NoSuchElementException("Input ended unexpectedly while waiting for second building.");
                }
                endName = scanner.nextLine().trim();
                end = model.findBuilding(endName); // Find end building

            } catch (NoSuchElementException e) {
                // If exception happens during second read, set flag and re-throw.
                eofDuringSecondRead = true;
                throw e;
            }

            // --- Process Results and Print Errors (only if EOF didn't happen during second read) ---
            if (!eofDuringSecondRead) {
                boolean errorPrinted = false;
                // Check start building 
                if (start == null) {
                    System.out.println("Unknown building: [" + startName + "]");
                    errorPrinted = true;
                }

                // Check end building
                if (end == null) {
                    // Avoid printing duplicate unknown message if start was also null and same as end
                    if (!errorPrinted || !startName.equals(endName)) {
                         System.out.println("Unknown building: [" + endName + "]");
                         errorPrinted = true;
                    }
                }

                // Only find and display path if NO errors were printed (meaning both buildings are valid)
                if (!errorPrinted) {
                    List<CampusModel.PathSegment> path = model.findShortestPath(start, end);
                    displayPath(start, end, path);
                }
            }
            // If eofDuringSecondRead was true, we skip printing errors and pathfinding,
            // allowing main to just catch the exception. 
        }
        
        /**
         * Displays a path between two buildings.
         * This method is part of the View in the MVC design pattern.
         * 
         * @param start The starting building
         * @param end The destination building
         * @param path The path, or null if no path exists
         */
        private void displayPath(CampusModel.Building start, CampusModel.Building end, 
                                List<CampusModel.PathSegment> path) {
            // Check if the path is null (no path exists)
            if (path == null) {
                System.out.println("There is no path from " + start.getName() + " to " + end.getName() + ".");
                return;
            }
            
            System.out.println("Path from " + start.getName() + " to " + end.getName() + ":");
            
            // If path is empty but not null, it's a same-building path with 0 distance
            if (path.isEmpty()) {
                System.out.printf("Total distance: 0.000 pixel units.\n");
                return;
            }
            
            for (CampusModel.PathSegment segment : path) {
                String destination;
                if (segment.getEnd().isBuilding()) {
                    destination = segment.getEnd().getName();
                } else {
                    destination = "Intersection " + segment.getEnd().getId();
                }
                
                System.out.println("\tWalk " + segment.getDirection() + " to (" + destination + ")");
            }
            
            double totalDistance = model.getTotalDistance(path);
            System.out.printf("Total distance: %.3f pixel units.\n", totalDistance);
        }
        
        /**
         * Prints the menu of commands.
         * This method is part of the View in the MVC design pattern.
         */
        private void printMenu() {
            System.out.println("Menu:");
            System.out.println("b - List all buildings");
            System.out.println("r - Find route between buildings");
            System.out.println("q - Quit");
            System.out.println("m - Show this menu");
        }
        
        /**
         * Closes resources used by the controller.
         */
        public void close() {
            scanner.close();
        }
    }
}
