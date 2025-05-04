package hw4;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Graph class, which is our underlying implementation of the graph ADT.
 */
public class GraphTest {
    
    // Specify the types for the generic Graph
    private Graph<String, String> graph;
    
    /**
     * Sets up a new Graph for each test.
     */
    @BeforeEach
    public void setUp() {
        // Instantiate the generic graph with String types
        graph = new Graph<String, String>();
    }
    
    /**
     * Tests creating a new empty graph.
     */
    @Test
    public void testEmptyGraph() {
        assertEquals(0, graph.numNodes(), "Empty graph should have no nodes");
        assertEquals(0, graph.numEdges(), "Empty graph should have no edges");
        assertTrue(graph.getNodes().isEmpty(), "Empty graph's nodes set should be empty");
    }
    
    /**
     * Tests adding a single node to the graph.
     */
    @Test
    public void testAddSingleNode() {
        graph.addNode("a");
        
        assertEquals(1, graph.numNodes(), "Graph should have one node");
        assertEquals(0, graph.numEdges(), "Graph should have no edges");
        assertTrue(graph.containsNode("a"), "Graph should contain node 'a'");
        assertEquals(1, graph.getNodes().size(), "Graph should have exactly one node");
        assertTrue(graph.getNodes().contains("a"), "Graph's nodes set should contain 'a'");
    }
    
    /**
     * Tests adding the same node twice.
     */
    @Test
    public void testAddDuplicateNode() {
        graph.addNode("a");
        graph.addNode("a"); // Adding the same node again
        
        assertEquals(1, graph.numNodes(), "Graph should still have only one node");
        assertTrue(graph.containsNode("a"), "Graph should contain node 'a'");
    }
    
    /**
     * Tests adding multiple nodes to the graph.
     */
    @Test
    public void testAddMultipleNodes() {
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        
        assertEquals(3, graph.numNodes(), "Graph should have three nodes");
        assertEquals(0, graph.numEdges(), "Graph should have no edges");
        assertTrue(graph.containsNode("a"), "Graph should contain node 'a'");
        assertTrue(graph.containsNode("b"), "Graph should contain node 'b'");
        assertTrue(graph.containsNode("c"), "Graph should contain node 'c'");
        assertEquals(3, graph.getNodes().size(), "Graph should have exactly three nodes");
    }
    
    /**
     * Tests adding a simple edge to the graph.
     */
    @Test
    public void testAddSingleEdge() {
        graph.addNode("a");
        graph.addNode("b");
        graph.addEdge("a", "b", "1");
        
        assertEquals(1, graph.numEdges(), "Graph should have one edge");
        assertTrue(graph.containsEdge("a", "b", "1"), 
                 "Graph should contain edge from 'a' to 'b' with label '1'");
        
        // Specify types for the map
        Map<String, List<String>> children = graph.getChildrenWithLabels("a");
        assertTrue(children.containsKey("b"), "Node 'a' should have 'b' as a child");
        assertEquals(1, children.get("b").size(), "Node 'a' should have one edge to 'b'");
        assertEquals("1", children.get("b").get(0), "The edge from 'a' to 'b' should have label '1'");
        
        // Specify types for the list
        List<String> childList = graph.getChildren("a");
        assertEquals(1, childList.size(), "Node 'a' should have one child");
        assertEquals("b", childList.get(0), "The child of 'a' should be 'b'");
        
        // Specify types for the list
        List<String> labels = graph.getEdgeLabels("a", "b");
        assertEquals(1, labels.size(), "There should be one edge from 'a' to 'b'");
        assertEquals("1", labels.get(0), "The edge from 'a' to 'b' should have label '1'");
        
        // Specify types for the list
        List<String> parents = graph.getParents("b");
        assertEquals(1, parents.size(), "Node 'b' should have one parent");
        assertEquals("a", parents.get(0), "The parent of 'b' should be 'a'");
        
        // Verify that the edge is directed
        assertTrue(graph.getChildren("b").isEmpty(), "Node 'b' should have no children");
    }
    
    /**
     * Tests adding multiple edges to the graph.
     */
    @Test
    public void testAddMultipleEdges() {
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        
        graph.addEdge("a", "b", "1");
        graph.addEdge("a", "c", "2");
        graph.addEdge("b", "c", "3");
        
        assertEquals(3, graph.numEdges(), "Graph should have three edges");
        
        // Specify types for the map
        Map<String, List<String>> childrenA = graph.getChildrenWithLabels("a");
        assertEquals(2, childrenA.size(), "Node 'a' should have two children");
        assertTrue(childrenA.containsKey("b"), "Node 'a' should have 'b' as a child");
        assertTrue(childrenA.containsKey("c"), "Node 'a' should have 'c' as a child");
        
        // Specify types for the map
        Map<String, List<String>> childrenB = graph.getChildrenWithLabels("b");
        assertEquals(1, childrenB.size(), "Node 'b' should have one child");
        assertTrue(childrenB.containsKey("c"), "Node 'b' should have 'c' as a child");
        
        // Specify types for the list
        List<String> parentsOfB = graph.getParents("b");
        assertEquals(1, parentsOfB.size(), "Node 'b' should have one parent");
        assertEquals("a", parentsOfB.get(0), "The parent of 'b' should be 'a'");
        
        // Specify types for the list
        List<String> parentsOfC = graph.getParents("c");
        assertEquals(2, parentsOfC.size(), "Node 'c' should have two parents");
        assertTrue(parentsOfC.contains("a"), "Parents of 'c' should include 'a'");
        assertTrue(parentsOfC.contains("b"), "Parents of 'c' should include 'b'");
    }
    
    /**
     * Tests adding multiple edges between the same nodes (multigraph feature).
     */
    @Test
    public void testMultigraph() {
        graph.addNode("a");
        graph.addNode("b");
        
        graph.addEdge("a", "b", "1");
        graph.addEdge("a", "b", "2");
        
        assertEquals(2, graph.numEdges(), "Graph should have two edges");
        
        // Specify types for the map
        Map<String, List<String>> children = graph.getChildrenWithLabels("a");
        assertEquals(1, children.size(), "Node 'a' should have one child");
        assertEquals(2, children.get("b").size(), "Node 'a' should have two edges to 'b'");
        assertTrue(children.get("b").contains("1"), "Edges from 'a' to 'b' should include label '1'");
        assertTrue(children.get("b").contains("2"), "Edges from 'a' to 'b' should include label '2'");
        
        // Specify types for the list
        List<String> labels = graph.getEdgeLabels("a", "b");
        assertEquals(2, labels.size(), "There should be two edges from 'a' to 'b'");
        assertTrue(labels.contains("1"), "Edges from 'a' to 'b' should include label '1'");
        assertTrue(labels.contains("2"), "Edges from 'a' to 'b' should include label '2'");
        
        // Specify types for the list
        List<String> childList = graph.getChildren("a");
        assertEquals(2, childList.size(), "Node 'a' should have two children (counting duplicates)");
        assertEquals("b", childList.get(0), "Both children of 'a' should be 'b'");
        assertEquals("b", childList.get(1), "Both children of 'a' should be 'b'");
    }
    
    /**
     * Tests adding reflexive edges (from a node to itself).
     */
    @Test
    public void testReflexiveEdge() {
        graph.addNode("a");
        
        graph.addEdge("a", "a", "1");
        
        assertEquals(1, graph.numEdges(), "Graph should have one edge");
        assertTrue(graph.containsEdge("a", "a", "1"),
                 "Graph should contain reflexive edge on 'a' with label '1'");
        
        // Specify types for the map
        Map<String, List<String>> children = graph.getChildrenWithLabels("a");
        assertTrue(children.containsKey("a"), "Node 'a' should have itself as a child");
        assertEquals(1, children.get("a").size(), "Node 'a' should have one edge to itself");
        assertEquals("1", children.get("a").get(0), "The reflexive edge on 'a' should have label '1'");
    }
} 