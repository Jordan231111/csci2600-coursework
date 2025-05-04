package hw5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import hw4.Graph;

/**
 * Represents a network of professors connected by the courses they have taught.
 * Provides functionality to load this network from a file and find the shortest
 * paths between professors based on shared courses.
 *
 * Specification fields:
 * @spec.field professorGraph : Graph<String, String> // The underlying graph structure where nodes are professor names (String)
 *                                                   // and edge labels are course codes (String).
 * @spec.field allProfessors : Set<String> // Set of all unique professor names loaded from the last file.
 *
 * Abstract Invariant:
 *  The professorGraph is a directed multigraph.
 *  Nodes represent unique professor names (String).
 *  An edge from P1 to P2 labeled C exists if P1 and P2 both taught course C.
 *  Edges are added in both directions (P1->P2 and P2->P1) for each shared course.
 *  The graph contains no reflexive edges (edges from a professor to themselves).
 *  allProfessors contains exactly the set of unique professor names present in the
 *  last successfully parsed data file.
 */
public class ProfessorPaths {

    // Representation Invariant (RI):
    //  professorGraph != null
    //  professorGraph internal state adheres to hw4.Graph<String, String>'s RI.
    //  professorGraph does not contain any reflexive edges.
    //  allProfessors != null
    //  allProfessors contains all nodes present in professorGraph.
    //  professorGraph.getNodes() equals allProfessors (or contains the same elements).
    //  Every professor name String stored is non-null and non-empty.
    //  Every course code String stored as an edge label is non-null and non-empty.

    // Abstraction Function (AF):
    // AF(this) = A professor network where:
    //  - The set of all known professors is given by allProfessors.
    //  - For any two distinct professors p1, p2 in allProfessors, and any course c,
    //    there is a directed edge from p1 to p2 labeled c in professorGraph
    //    if and only if both p1 and p2 taught course c according to the data
    //    loaded by the last call to createNewGraph. Similarly for an edge from p2 to p1.

    private Graph<String, String> professorGraph; // Updated to generic Graph
    private Set<String> allProfessors;

    // CheckRep: Can be expensive. Comment out the call in public methods if performance is critical.
    private void checkRep() {
        assert professorGraph != null : "Graph cannot be null";
        assert allProfessors != null : "Professor set cannot be null";

        // RI Check: Graph should not contain reflexive edges
        // This check can be expensive, only enable if necessary during debugging
        /*
        Set<String> nodes = professorGraph.getNodes();
        for (String node : nodes) {
            Map<String, List<String>> children = professorGraph.getChildrenWithLabels(node);
            assert !children.containsKey(node) : "Graph contains reflexive edge for node: " + node;
        }
        */

        // RI Check: allProfessors contains exactly the nodes in the graph
        // Use size check first for efficiency, then content check if needed
        assert professorGraph.getNodes().size() == allProfessors.size() : "Mismatch in size between graph nodes and professor set";
        assert professorGraph.getNodes().containsAll(allProfessors) : "Graph nodes do not contain all stored professors";
        assert allProfessors.containsAll(professorGraph.getNodes()) : "Professor set contains nodes not in the graph";

        // RI Check: Professor names and course codes are non-null and non-empty
        // This requires iterating through all nodes and edges, potentially expensive.
        /*
        for (String prof : allProfessors) {
            assert prof != null && !prof.isEmpty() : "Null or empty professor name found: " + prof;
        }
        for (String parent : professorGraph.getNodes()) {
            Map<String, List<String>> childrenMap = professorGraph.getChildrenWithLabels(parent);
            for (String child : childrenMap.keySet()) {
                List<String> labels = childrenMap.get(child);
                for (String label : labels) {
                    assert label != null && !label.isEmpty() : "Null or empty course code found for edge " + parent + " -> " + child;
                }
            }
        }
        */
    }

    /**
     * Creates a new, empty ProfessorPaths instance.
     */
    public ProfessorPaths() {
        this.professorGraph = new Graph<>(); // Instantiate generic Graph
        this.allProfessors = new HashSet<>();
        // checkRep(); // Initial check
    }

    /**
     * Creates a new graph representation from the data in the specified file.
     * Replaces any existing graph data in this instance.
     * Handles potential IOExceptions during file reading gracefully.
     *
     * @param filename The path to the CSV data file. Format: "professor","course"
     * @throws IllegalArgumentException if the filename is null or empty.
     * @requires filename is a valid path to a readable file (or null/empty).
     * @modifies this.professorGraph, this.allProfessors
     * @effects Clears the existing graph and populates it with data from the file.
     *          Nodes are professors, edges represent shared courses labeled with the course code.
     *          Edges are added in both directions for each shared course.
     *          No reflexive edges are added.
     *          If an IOException occurs, the graph remains empty or in its previous state before the call.
     */
    public void createNewGraph(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty.");
        }

        // Re-initialize graph and professor set for a fresh start
        this.professorGraph = new Graph<>(); // Instantiate generic Graph
        this.allProfessors = new HashSet<>();

        ProfessorParser.ParsedData parsedData;
        try {
            // Parse the data using ProfessorParser
            parsedData = ProfessorParser.readData(filename);
        } catch (IOException e) {
            // Handle file reading error, e.g., log it or simply leave the graph empty.
            // Current behavior: graph remains empty as it was just re-initialized.
            System.err.println("Error reading data file " + filename + ": " + e.getMessage());
            // checkRep(); // Check state after failed load
            return; // Exit the method, leaving an empty graph
        }

        // Add all unique professors as nodes to the graph and store them
        this.allProfessors.addAll(parsedData.professors);
        for (String prof : this.allProfessors) {
            // Basic validation before adding
            if (prof != null && !prof.isEmpty()) {
                professorGraph.addNode(prof);
            } else {
                System.err.println("Warning: Skipped adding null or empty professor name found in data.");
                // Optionally remove invalid professor from allProfessors set if needed
                this.allProfessors.remove(prof); // Remove the invalid entry
            }
        }

        // Process each course to add edges
        for (Map.Entry<String, Set<String>> entry : parsedData.courseToProfessors.entrySet()) {
            String course = entry.getKey();
            Set<String> profsForCourseSet = entry.getValue();

            // Basic validation
            if (course == null || course.isEmpty()) {
                System.err.println("Warning: Skipped adding edges for null or empty course code.");
                continue;
            }
            if (profsForCourseSet == null || profsForCourseSet.size() < 2) {
                continue; // Need at least two professors to form an edge
            }

            // Convert to list/array for indexed access - avoids issues with modifying set while iterating
            List<String> profsForCourse = new ArrayList<>(profsForCourseSet);
            int profCount = profsForCourse.size();

            // Add edges between all distinct pairs of professors for this course
            for (int i = 0; i < profCount; i++) {
                String prof1 = profsForCourse.get(i);
                // Ensure prof1 is valid and actually in the graph (wasn't skipped earlier)
                if (prof1 == null || prof1.isEmpty() || !professorGraph.containsNode(prof1)) continue;

                for (int j = 0; j < profCount; j++) {
                    if (i == j) continue; // Skip reflexive edges

                    String prof2 = profsForCourse.get(j);
                    // Ensure prof2 is valid and actually in the graph
                    if (prof2 == null || prof2.isEmpty() || !professorGraph.containsNode(prof2)) continue;

                    // Add edge in both directions. Graph handles duplicates if needed.
                    professorGraph.addEdge(prof1, prof2, course);
                }
            }
        }
        // checkRep(); // Check after successful load
    }

    /**
     * Helper record to store the previous professor and the course taken to reach the current one in BFS.
     * Used as the value in the 'visited' map during BFS.
     * Implements Comparable for tie-breaking in BFS (lexicographical by course, then by previous professor).
     */
    record PathSegment(String previousProfessor, String courseTaken) implements Comparable<PathSegment> {

        // Implicitly provides constructor, getters, equals, hashCode, toString

        @Override
        public int compareTo(PathSegment other) {
            // Handle null courses (only happens for start node)
            if (this.courseTaken == null && other.courseTaken == null) {
                // For the start node, previousProfessor should also be consistent (e.g., null or start node itself)
                // Assuming non-null previousProfessor for comparison consistency
                return this.previousProfessor.compareTo(other.previousProfessor);
            }
            if (this.courseTaken == null) {
                return -1; // The path from the start node (no course) comes first
            }
            if (other.courseTaken == null) {
                return 1;
            }

            // First compare courses lexicographically
            int courseCompare = this.courseTaken.compareTo(other.courseTaken);
            if (courseCompare != 0) {
                return courseCompare;
            }

            // If courses are the same, then compare the previous professors lexicographically
            // This ensures deterministic path selection when multiple courses lead to the same previous step
            return this.previousProfessor.compareTo(other.previousProfessor);
        }
    }

    /**
     * Finds the shortest path between two professors using Breadth-First Search (BFS).
     * The path is defined by the sequence of courses connecting the professors.
     * If multiple shortest paths exist, chooses the one that is lexicographically smallest
     * based on course codes, and then by the intermediate professor names.
     *
     * @param node1 The name of the starting professor.
     * @param node2 The name of the destination professor.
     * @return A formatted string describing the shortest path found, or indicating
     *         if a path does not exist or if one/both professors are unknown.
     *         Format for path: "path from PROF1 to PROFN:\nPROF1 to PROF2 via COURSE1\nPROF2 to PROF3 via COURSE2\n...\nPROFN-1 to PROFN via COURSEN-1"
     *         Format for unknown professor: "unknown professor PROF_NAME"
     *         Format for no path: "path from PROF1 to PROF2:\nno path found"
     * @spec.requires node1 != null, node2 != null
     */
    public String findPath(String node1, String node2) {
        // checkRep(); // Check at the start of the public method

        if (node1 == null || node2 == null) {
             throw new IllegalArgumentException("Professor names cannot be null.");
        }

        // Check if professors exist in the graph
        boolean node1Exists = professorGraph.containsNode(node1);
        boolean node2Exists = professorGraph.containsNode(node2);

        if (!node1Exists && !node2Exists) {
            // If they're the same professor, only report once
            if (node1.equals(node2)) {
                return "unknown professor " + node1 + "\n";
            } else {
                return "unknown professor " + node1 + "\nunknown professor " + node2 + "\n";
            }
        }
        if (!node1Exists) {
            return "unknown professor " + node1 + "\n";
        }
        if (!node2Exists) {
            return "unknown professor " + node2 + "\n";
        }

        // Handle the case where start and end nodes are the same
        if (node1.equals(node2)) {
            return "path from " + node1 + " to " + node2 + ":\n";
        }

        // BFS implementation
        Queue<String> queue = new LinkedList<>();
        // Map<Current Professor, PathSegment (Previous Professor, Course Taken to get here)>
        Map<String, PathSegment> visited = new HashMap<>();

        queue.add(node1);
        // Mark start node as visited, with null previous/course
        visited.put(node1, new PathSegment(null, null));

        while (!queue.isEmpty()) {
            String currentProf = queue.poll();

            if (currentProf.equals(node2)) {
                // Path found, reconstruct and return it
                return constructPathString(node1, node2, visited);
            }

            // Get children (neighboring professors) and the courses (edge labels) connecting them
            // Use TreeMap for children to process them in lexicographical order (helps with tie-breaking)
            Map<String, List<String>> childrenWithLabels = new TreeMap<>(professorGraph.getChildrenWithLabels(currentProf));

            // Process neighbors in a deterministic order (sorted by professor name)
            for (String neighborProf : childrenWithLabels.keySet()) {
                if (!visited.containsKey(neighborProf)) {

                    // Find the lexicographically smallest course label among edges to this neighbor
                    List<String> edgeLabels = childrenWithLabels.get(neighborProf);
                    Collections.sort(edgeLabels); // Sort courses to pick the smallest
                    String bestCourse = edgeLabels.get(0);

                    // Store the path segment: came from currentProf via bestCourse
                    visited.put(neighborProf, new PathSegment(currentProf, bestCourse));
                    queue.add(neighborProf);
                }
            }
        }

        // If the loop finishes, no path was found
        // checkRep(); // Check at the end
        return "path from " + node1 + " to " + node2 + ":\nno path found\n";
    }

    /**
     * Reconstructs the path string from the visited map populated by BFS.
     *
     * @param start The starting professor name.
     * @param end The ending professor name.
     * @param visited The map containing path segments from the BFS.
     * @return A formatted string describing the path.
     */
    private String constructPathString(String start, String end, Map<String, PathSegment> visited) {
        LinkedList<String> pathSteps = new LinkedList<>();
        String current = end;

        while (current != null && !current.equals(start)) {
            PathSegment segment = visited.get(current);
            if (segment == null || segment.previousProfessor == null || segment.courseTaken == null) {
                // Should not happen in a valid path found by BFS, indicates error
                System.err.println("Error reconstructing path: Invalid segment found for " + current);
                return "path from " + start + " to " + end + ":\nerror in path reconstruction\n";
            }
            pathSteps.addFirst(segment.previousProfessor + " to " + current + " via " + segment.courseTaken);
            current = segment.previousProfessor;
        }

        // Build the final output string
        StringBuilder result = new StringBuilder();
        result.append("path from ").append(start).append(" to ").append(end).append(":\n");
        if (pathSteps.isEmpty() && !start.equals(end)) {
             // This case should ideally be caught earlier, but handle defensively
             result.append("no path found\n");
        } else {
            for (String step : pathSteps) {
                result.append(step).append("\n");
            }
        }

        return result.toString();
    }

    /**
     * Returns a set containing all unique professor names currently stored in the graph.
     *
     * @return A set of all professor names. Returns an empty set if the graph is empty.
     */
    public Set<String> getAllProfessorNames() {
        // checkRep(); // Check state before returning data
        return new HashSet<>(allProfessors); // Return a copy to avoid modification
    }

    /**
     * Main method for basic command-line interaction or testing.
     * Example usage: java hw5.ProfessorPaths data/professors.csv "Professor A" "Professor B"
     *
     * @param args Command line arguments: [datafile] [prof1] [prof2]
     */
    public static void main(String[] args) {
        if (args.length != 3 && !(args.length == 2 && args[1].equals("--large-scale-test"))) {
            System.err.println("Usage: java hw5.ProfessorPaths <datafile> <professor1> <professor2>");
            System.err.println("Usage: java hw5.ProfessorPaths <datafile> --large-scale-test");
            return;
        }

        String filename = args[0];
        
        // Check for large-scale test flag
        if (args.length == 2 && args[1].equals("--large-scale-test")) {
             System.out.println("Running large-scale test on: " + filename);
             runLargeScaleTest(filename);
             return;
        }
        
        String prof1 = args[1];
        String prof2 = args[2];

        ProfessorPaths paths = new ProfessorPaths();
        
        // Modified this section to match test expectations
        System.out.println("Testing with " + filename);
        long startTime = System.currentTimeMillis();
        paths.createNewGraph(filename);
        long endTime = System.currentTimeMillis();
        System.out.println("Graph loaded in " + (endTime - startTime) + " ms.");
        System.out.println("Total professors: " + paths.getAllProfessorNames().size());

        System.out.println("\nFinding path from " + prof1 + " to " + prof2 + "...");
        startTime = System.currentTimeMillis();
        String result = paths.findPath(prof1, prof2);
        endTime = System.currentTimeMillis();
        System.out.println(result);
        System.out.println("Path found in " + (endTime - startTime) + " ms.");
    }

    /**
     * Runs a series of random pathfinding queries on a loaded graph to test performance.
     * @param largeFile The data file to load.
     */
    private static void runLargeScaleTest(String largeFile) {
        ProfessorPaths paths = new ProfessorPaths();
        try {
            System.out.println("Loading graph from " + largeFile + "...");
            long startTime = System.currentTimeMillis();
            paths.createNewGraph(largeFile);
            long endTime = System.currentTimeMillis();
            System.out.println("Graph loaded in " + (endTime - startTime) + " ms.");

            Set<String> professors = paths.getAllProfessorNames();
            if (professors.isEmpty()) {
                System.out.println("No professors loaded, cannot run test.");
                return;
            }
            List<String> profList = new ArrayList<>(professors);
            Random random = new Random();
            int numQueries = 100; // Number of random paths to find
            long totalPathTime = 0;

            System.out.println("\nFinding " + numQueries + " random paths...");
            for (int i = 0; i < numQueries; i++) {
                String p1 = profList.get(random.nextInt(profList.size()));
                String p2 = profList.get(random.nextInt(profList.size()));

                startTime = System.currentTimeMillis();
                paths.findPath(p1, p2); // We don't print the path here
                endTime = System.currentTimeMillis();
                totalPathTime += (endTime - startTime);

                if ((i + 1) % 10 == 0) {
                    System.out.print("."); // Progress indicator
                }
            }
            System.out.println("\n\nTotal time for " + numQueries + " pathfinding queries: " + totalPathTime + " ms");
            System.out.println("Average time per query: " + (double)totalPathTime / numQueries + " ms");

        } catch (Exception e) {
            System.err.println("An error occurred during the large-scale test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}