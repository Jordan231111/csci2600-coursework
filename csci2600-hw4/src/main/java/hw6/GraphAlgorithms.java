package hw6;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

import hw4.Graph;

/**
 * Contains static methods for graph algorithms, such as Dijkstra's algorithm
 * for finding the shortest path in a weighted graph.
 *
 * This class is not an ADT.
 */
public final class GraphAlgorithms {

    // Private constructor to prevent instantiation of this utility class.
    private GraphAlgorithms() {}

    /**
     * Represents a single step (edge) in a path.
     * Stores the source node, destination node, and the edge weight.
     * Used to reconstruct the path found by Dijkstra's algorithm.
     *
     * @param <N> The type of the nodes.
     * @param source The source node of the edge.
     * @param destination The destination node of the edge.
     * @param weight The weight of the edge.
     */
    public record PathEdge<N>(N source, N destination, Double weight) {}

    /**
     * Represents the result of a pathfinding algorithm.
     * Contains the sequence of edges forming the path and the total cost.
     *
     * @param <N> The type of the nodes in the path.
     * @param edges The list of PathEdge objects representing the path from start to end.
     *              Null if no path is found.
     * @param totalCost The sum of the weights of the edges in the path. NaN if no path found.
     */
    public record PathResult<N>(List<PathEdge<N>> edges, double totalCost) {}

    /**
     * Represents an entry in the priority queue used by Dijkstra's algorithm.
     * Stores a node and the total cost to reach that node from the start.
     * Implements Comparable to allow proper ordering in the priority queue.
     *
     * @param <N> The type of the nodes.
     */
    private static class PathEntry<N> implements Comparable<PathEntry<N>> {
        // Using final for fields that never change improves JVM optimization
        private final N node;
        private final double cost;
        
        public PathEntry(N node, double cost) {
            this.node = node;
            this.cost = cost;
        }

        public N getNode() {
            return node;
        }

        public double getCost() {
            return cost;
        }

        /**
         * Compares this PathEntry to another based on cost.
         * Lower cost has higher priority.
         * This is a performance-critical method since it's called frequently
         * during priority queue operations.
         */
        @Override
        public int compareTo(PathEntry<N> other) {
            return Double.compare(this.cost, other.cost);
        }
    }

    /**
     * Finds the minimum-cost path between two nodes in a weighted directed graph
     * using Dijkstra's algorithm. Assumes non-negative edge weights.
     * 
     * This optimized implementation:
     * 1. Uses a visited flag to avoid duplicate processing
     * 2. Avoids removing nodes from the priority queue (O(n) operation)
     * 3. Pre-allocates collections with expected sizes to reduce resizing
     * 4. Minimizes object creation and method calls in tight loops
     * 5. Uses early termination when destination is reached
     * 6. Implements specialized priority queue handling for better performance
     * 7. Eliminates redundant checks and operations for better CPU efficiency
     *
     * @param <N>       The type of data stored in the nodes.
     * @param graph     The graph to search within. Edge labels must be Double weights.
     * @param startNode The starting node.
     * @param endNode   The destination node.
     * @return A PathResult containing the list of edges in the minimum-cost path
     *         and the total cost, or a PathResult with null edges and NaN cost if
     *         no path exists.
     * @throws NullPointerException if graph, startNode, or endNode is null.
     * @throws IllegalArgumentException if startNode or endNode are not in the graph.
     */
    public static <N> PathResult<N> findShortestPath(Graph<N, Double> graph, N startNode, N endNode) {
        // Validate inputs - using Objects.requireNonNull for cleaner null checks
        Objects.requireNonNull(graph, "Graph cannot be null");
        Objects.requireNonNull(startNode, "Start node cannot be null");
        Objects.requireNonNull(endNode, "End node cannot be null");

        if (!graph.containsNode(startNode)) {
            throw new IllegalArgumentException("Start node not found in graph: " + startNode);
        }
        if (!graph.containsNode(endNode)) {
            throw new IllegalArgumentException("End node not found in graph: " + endNode);
        }

        // Early termination for start == end (common case optimization)
        if (startNode.equals(endNode)) {
            return new PathResult<>(Collections.emptyList(), 0.0);
        }

        // Get the number of nodes to properly size data structures
        int nodeCount = graph.getNodes().size();
        
        // Reuse one map with smaller load factor for faster lookups in hot path
        Set<N> visitedNodes = new HashSet<>(nodeCount);
        Map<N, Double> distances = new HashMap<>(nodeCount);
        Map<N, N> previousNodes = new HashMap<>(nodeCount);
        Map<N, Double> edgeWeights = new HashMap<>(nodeCount);
        
        // Initialize distance to start node as 0
        distances.put(startNode, 0.0);
        
        // Use simplified queue with just what we need
        PriorityQueue<PathEntry<N>> queue = new PriorityQueue<>();
        queue.offer(new PathEntry<>(startNode, 0.0));
        
        // Direct reference to endNode for faster equality checks
        N targetNode = endNode;
        
        // Main Dijkstra loop - heavily optimized for CPU efficiency
        while (!queue.isEmpty()) {
            PathEntry<N> current = queue.poll();
            N currentNode = current.getNode();
            double currentDistance = current.getCost();
            
            // Skip if already visited (critical fast path)
            if (visitedNodes.contains(currentNode)) {
                continue;
            }
            
            // Fast path - object equality check only if reference equality fails
            if (currentNode == targetNode || currentNode.equals(targetNode)) {
                return buildPathResult(startNode, endNode, previousNodes, edgeWeights);
            }
            
            // Mark as visited before processing neighbors
            visitedNodes.add(currentNode);
            
            // Get all neighbors with their edge weights using the efficient view
            Map<N, List<Double>> neighborsView = graph.getOutgoingEdgesView(currentNode);
            
            // Process each neighbor
            for (Map.Entry<N, List<Double>> entry : neighborsView.entrySet()) {
                N neighbor = entry.getKey();
                
                // Skip already visited nodes (fast rejection)
                if (visitedNodes.contains(neighbor)) {
                    continue;
                }
                
                List<Double> weights = entry.getValue();
                if (weights.isEmpty()) {
                    continue;
                }
                
                // Fetch the first edge weight - our graph has at most one weight per edge
                double weight = weights.get(0);
                double newDistance = currentDistance + weight;
                
                // Get existing distance or set to infinity if not yet visited
                Double existingDistance = distances.get(neighbor);
                if (existingDistance == null || newDistance < existingDistance) {
                    // Update distance
                    distances.put(neighbor, newDistance);
                    
                    // Update path tracking
                    previousNodes.put(neighbor, currentNode);
                    edgeWeights.put(neighbor, weight);
                    
                    // Add to queue
                    queue.offer(new PathEntry<>(neighbor, newDistance));
                }
            }
        }
        
        // No path found
        return new PathResult<>(null, Double.NaN);
    }
    
    /**
     * Builds the final path result by reconstructing the path from start to end.
     * Optimized for memory efficiency and performance.
     * 
     * @param <N> The type of data stored in the nodes.
     * @param startNode The starting node.
     * @param endNode The destination node.
     * @param previousNodes Map of each node to its predecessor in the shortest path.
     * @param edgeWeights Map of each node to the weight of the edge from its predecessor.
     * @return A PathResult containing the list of edges in the minimum-cost path and the total cost.
     */
    private static <N> PathResult<N> buildPathResult(N startNode, N endNode, Map<N, N> previousNodes, Map<N, Double> edgeWeights) {
        // Handle trivial case
        if (startNode.equals(endNode)) {
            return new PathResult<>(Collections.emptyList(), 0.0);
        }
        
        // Use LinkedList for efficient addFirst during reconstruction
        List<PathEdge<N>> path = new LinkedList<>();
        double totalCost = 0.0;
        
        // Reconstruct the path by walking backwards from endNode
        N current = endNode;
        while (!current.equals(startNode)) {
            N previous = previousNodes.get(current);
            Double weight = edgeWeights.get(current);
            
            if (previous == null || weight == null) {
                // This should not happen if we found a path
                throw new IllegalStateException("Path reconstruction failed: missing previous node or edge weight");
            }
            
            // Add to beginning of path (efficient with LinkedList)
            path.add(0, new PathEdge<>(previous, current, weight));
            totalCost += weight;
            current = previous;
        }
        
        return new PathResult<>(path, totalCost);
    }
} 