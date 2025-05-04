package hw4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A mutable directed labeled multigraph. A multigraph is a graph which can have multiple
 * edges between the same pair of nodes. Each edge has a label of type E.
 * The graph stores data of type N for nodes.
 * Nodes are uniquely identified by their data.
 *
 * @param <N> the type of data stored in the nodes
 * @param <E> the type of data stored in the edge labels
 */
public class Graph<N, E> {
    
    // Abstraction Function:
    // Represents a directed labeled multigraph where:
    // - Nodes are represented by data values of type N
    // - Directed edges exist from one node to another, potentially with multiple edges
    //   between the same pair of nodes
    // - Each edge has a label of type E
    // - Nodes are uniquely identified by their data values (no two nodes have the same data)
    //
    // Representation Invariant:
    // - nodeEdges != null
    // - No key in nodeEdges is null
    // - No value in nodeEdges is null
    // - For every node key N in nodeEdges, the corresponding map value (Map<N, List<E>>) is not null
    // - Every node N that appears as a destination in an edge list (value of the inner map) is also a key in nodeEdges
    // - For every edge, both source and destination nodes exist as keys in nodeEdges
    // - For every edge label E in the lists, the label is not null (optional: depends on E's constraints)
    
    /**
     * Map of node data to outgoing edges.
     * For each node (key of type N), stores a map of destination nodes (keys of type N)
     * to lists of edge labels (values of type List<E>).
     */
    private Map<N, Map<N, List<E>>> nodeEdges;
    
    /**
     * Flag to control whether checkRep is performed (can be disabled for performance).
     */
    private static final boolean CHECK_REP_ENABLED = false;
    private static final int DEFAULT_NEIGHBOR_CAPACITY = 16; // Default initial capacity for neighbor maps
    
    /**
     * @spec.effects Constructs a new empty graph
     */
    public Graph() {
        nodeEdges = new HashMap<>();
        if (CHECK_REP_ENABLED) checkRep();
    }
    
    /**
     * Adds a node to this graph.
     * 
     * @param nodeData the data of the node to add
     * @spec.requires nodeData != null
     * @spec.modifies this
     * @spec.effects If no node with nodeData exists in this graph, adds a node with nodeData to this.
     *               If a node with nodeData already exists, the graph remains unchanged.
     */
    public void addNode(N nodeData) {
        if (CHECK_REP_ENABLED) checkRep();
        
        nodeEdges.computeIfAbsent(nodeData, k -> new HashMap<>(DEFAULT_NEIGHBOR_CAPACITY));
        
        if (CHECK_REP_ENABLED) checkRep();
    }
    
    /**
     * Adds an edge from the node with parentData to the node with childData.
     * 
     * @param parentData the data of the parent node
     * @param childData the data of the child node
     * @param edgeLabel the label of the edge
     * @spec.requires parentData != null && childData != null && edgeLabel != null
     * @spec.requires nodes with parentData and childData exist in this graph
     * @spec.modifies this
     * @spec.effects Adds an edge from the node with parentData to the node with childData with label edgeLabel.
     *               If an identical edge already exists, this method may or may not add a duplicate edge.
     */
    public void addEdge(N parentData, N childData, E edgeLabel) {
        if (CHECK_REP_ENABLED) checkRep();
        
        Map<N, List<E>> childrenMap = nodeEdges.get(parentData);
        
        List<E> edgeLabels = childrenMap.computeIfAbsent(childData, k -> new ArrayList<>());
        
        edgeLabels.add(edgeLabel);
        
        if (CHECK_REP_ENABLED) checkRep();
    }
    
    /**
     * Returns whether a node exists in this graph.
     * 
     * @param nodeData the data of the node to check
     * @spec.requires nodeData != null
     * @return true if a node with nodeData exists in this graph, false otherwise
     */
    public boolean containsNode(N nodeData) {
        if (CHECK_REP_ENABLED) checkRep();
        return nodeEdges.containsKey(nodeData);
    }
    
    /**
     * Returns whether an edge exists from the node with parentData to the node with childData with the given label.
     * 
     * @param parentData the data of the parent node
     * @param childData the data of the child node
     * @param edgeLabel the label of the edge
     * @spec.requires parentData != null && childData != null && edgeLabel != null
     * @return true if an edge from the node with parentData to the node with childData with label edgeLabel exists,
     *         false otherwise
     */
    public boolean containsEdge(N parentData, N childData, E edgeLabel) {
        if (CHECK_REP_ENABLED) checkRep();
        
        if (!nodeEdges.containsKey(parentData)) {
            return false;
        }
        
        Map<N, List<E>> childrenMap = nodeEdges.get(parentData);
        if (!childrenMap.containsKey(childData)) {
            return false;
        }
        
        List<E> edgeLabels = childrenMap.get(childData);
        return edgeLabels.contains(edgeLabel);
    }
    
    /**
     * Returns a set of all nodes in this graph.
     * 
     * @return a set containing all nodes (of type N) in this graph
     * @spec.effects The returned set is a copy; changes to it will not affect this graph.
     */
    public Set<N> getNodes() {
        if (CHECK_REP_ENABLED) checkRep();
        
        return new HashSet<>(nodeEdges.keySet());
    }
    
    /**
     * Returns a map of child nodes and their corresponding edge labels from a given parent node.
     * 
     * @param parentData the data of the parent node
     * @spec.requires parentData != null
     * @spec.requires a node with parentData exists in this graph
     * @return a map where each key is a child node data (type N) and each value is a list of edge labels (type E)
     *         from the parent to that child
     * @spec.effects The returned map is a deep copy; changes to it will not affect this graph.
     */
    public Map<N, List<E>> getChildrenWithLabels(N parentData) {
        if (CHECK_REP_ENABLED) checkRep();
        
        Map<N, List<E>> childrenMap = nodeEdges.get(parentData);
        Map<N, List<E>> result = new HashMap<>();
        
        for (Map.Entry<N, List<E>> entry : childrenMap.entrySet()) {
            N childData = entry.getKey();
            List<E> originalLabels = entry.getValue();
            List<E> labelsCopy = new ArrayList<>(originalLabels);
            result.put(childData, labelsCopy);
        }
        
        return result;
    }
    
    /**
     * Returns a list of all child nodes of a given parent node.
     * 
     * @param parentData the data of the parent node
     * @spec.requires parentData != null
     * @spec.requires a node with parentData exists in this graph
     * @return a list containing all child nodes (type N) of the node with parentData
     * @spec.effects The returned list is a copy; changes to it will not affect this graph.
     *              If a node has multiple edges to the same child, that child will appear multiple times in the list.
     */
    public List<N> getChildren(N parentData) {
        if (CHECK_REP_ENABLED) checkRep();
        
        Map<N, List<E>> childrenMap = nodeEdges.get(parentData);
        List<N> children = new ArrayList<>();
        
        for (Map.Entry<N, List<E>> entry : childrenMap.entrySet()) {
            N childData = entry.getKey();
            List<E> edgeLabels = entry.getValue();
            for (int i = 0; i < edgeLabels.size(); i++) {
                children.add(childData);
            }
        }
        
        return children;
    }
    
    /**
     * Returns a list of edge labels for all edges from the node with parentData to the node with childData.
     * 
     * @param parentData the data of the parent node
     * @param childData the data of the child node
     * @spec.requires parentData != null && childData != null
     * @spec.requires nodes with parentData and childData exist in this graph
     * @return a list containing all edge labels (type E) from parentData to childData.
     *         Returns an empty list if no such edges exist.
     * @spec.effects The returned list is a copy; changes to it will not affect this graph.
     */
    public List<E> getEdgeLabels(N parentData, N childData) {
        if (CHECK_REP_ENABLED) checkRep();
        
        Map<N, List<E>> childrenMap = nodeEdges.get(parentData);
        
        if (childrenMap.containsKey(childData)) {
            return new ArrayList<>(childrenMap.get(childData));
        } else {
            return new ArrayList<>();
        }
    }
    
    /**
     * Returns a list of all parent nodes that have an edge pointing to the given child node.
     * 
     * @param childData the data of the child node
     * @spec.requires childData != null
     * @spec.requires a node with childData exists in this graph
     * @return a list containing all parent nodes (type N) of the node with childData
     * @spec.effects The returned list is a copy; changes to it will not affect this graph.
     *              If a parent has multiple edges to the same child, that parent will appear multiple times in the list.
     */
    public List<N> getParents(N childData) {
        if (CHECK_REP_ENABLED) checkRep();
        
        List<N> parents = new ArrayList<>();
        
        for (Map.Entry<N, Map<N, List<E>>> parentEntry : nodeEdges.entrySet()) {
            N parentNode = parentEntry.getKey();
            Map<N, List<E>> childrenMap = parentEntry.getValue();
            if (childrenMap.containsKey(childData)) {
                int edgeCount = childrenMap.get(childData).size();
                for (int i = 0; i < edgeCount; i++) {
                    parents.add(parentNode);
                }
            }
        }
        
        return parents;
    }

    /**
     * Returns the number of nodes in this graph.
     * 
     * @return the number of nodes in this graph
     */
    public int numNodes() {
        if (CHECK_REP_ENABLED) checkRep();
        return nodeEdges.size();
    }
    
    /**
     * Returns the total number of edges in this graph.
     * 
     * @return the total number of edges in this graph
     */
    public int numEdges() {
        if (CHECK_REP_ENABLED) checkRep();
        
        int totalEdges = 0;
        
        for (Map<N, List<E>> childrenMap : nodeEdges.values()) {
            for (List<E> edgeLabels : childrenMap.values()) {
                totalEdges += edgeLabels.size();
            }
        }
        
        return totalEdges;
    }
    
    /**
     * Checks that the representation invariant holds.
     * Can be disabled for performance by setting CHECK_REP_ENABLED to false.
     */
    private void checkRep() {
        assert nodeEdges != null : "nodeEdges should not be null";
        
        for (Map.Entry<N, Map<N, List<E>>> parentEntry : nodeEdges.entrySet()) {
            N parentNode = parentEntry.getKey();
            assert parentNode != null : "Node data cannot be null";
            
            Map<N, List<E>> childrenMap = parentEntry.getValue();
            assert childrenMap != null : "Children map for node " + parentNode + " should not be null";
            
            for (Map.Entry<N, List<E>> childEntry : childrenMap.entrySet()) {
                N childNode = childEntry.getKey();
                assert childNode != null : "Child node data cannot be null";
                assert nodeEdges.containsKey(childNode) : "Destination node " + childNode + " does not exist in the graph";
                
                List<E> edgeLabels = childEntry.getValue();
                assert edgeLabels != null : "Edge label list for edge " + parentNode + " -> " + childNode + " should not be null";
            }
        }
    }

    /**
     * Returns an unmodifiable view of the map associating child nodes with their edge labels
     * for a given parent node. This is intended for efficient read-only access, especially
     * in performance-critical algorithms like Dijkstra's, as it avoids copying.
     *
     * @param parentData the data of the parent node
     * @spec.requires parentData != null
     * @return An unmodifiable view of the map from child nodes (type N) to lists of edge labels (type E)
     *         originating from the parent node. Returns an empty map view if the parent node
     *         does not exist or has no outgoing edges.
     * @spec.effects The returned map is an unmodifiable view; attempting to modify it
     *               will result in an UnsupportedOperationException. Changes to the graph
     *               *may* be reflected in the view.
     */
    public Map<N, List<E>> getOutgoingEdgesView(N parentData) {
        if (CHECK_REP_ENABLED) checkRep();
        // Return an unmodifiable view, defaulting to an empty map if the node doesn't exist.
        // This avoids null checks in the caller and provides safe read-only access.
        return Collections.unmodifiableMap(nodeEdges.getOrDefault(parentData, Collections.emptyMap()));
    }
} 