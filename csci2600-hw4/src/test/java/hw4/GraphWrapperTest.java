package hw4;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the GraphWrapper class.
 */
public class GraphWrapperTest {
    
    // Assuming GraphWrapper uses Graph<String, String> internally now
    private GraphWrapper graph;
    
    /**
     * Sets up a new GraphWrapper for each test.
     */
    @BeforeEach
    public void setUp() {
        // GraphWrapper instantiation might remain the same if it handles the generic type internally
        graph = new GraphWrapper();
    }
    
    /**
     * Tests creating a new empty GraphWrapper.
     */
    @Test
    public void testEmptyGraph() {
        Iterator<String> nodes = graph.listNodes();
        assertFalse(nodes.hasNext(), "Empty graph should have no nodes");
    }
    
    /**
     * Tests adding a single node to the graph.
     */
    @Test
    public void testAddSingleNode() {
        graph.addNode("a");
        
        Iterator<String> nodes = graph.listNodes();
        assertTrue(nodes.hasNext(), "Graph should have one node");
        assertEquals("a", nodes.next(), "Graph should contain node 'a'");
        assertFalse(nodes.hasNext(), "Graph should have no more nodes");
        
        Iterator<String> children = graph.listChildren("a");
        assertFalse(children.hasNext(), "Node 'a' should have no children");
        
        String xml = graph.listChildrenXML("a");
        assertEquals("<nodes>\n</nodes>", xml, "XML should represent empty children list");
    }
    
    /**
     * Tests adding multiple nodes to the graph.
     */
    @Test
    public void testAddMultipleNodes() {
        graph.addNode("b");
        graph.addNode("a");
        graph.addNode("c");
        
        Iterator<String> nodes = graph.listNodes();
        assertTrue(nodes.hasNext());
        assertEquals("a", nodes.next(), "Nodes should be in lexicographical order");
        assertEquals("b", nodes.next(), "Nodes should be in lexicographical order");
        assertEquals("c", nodes.next(), "Nodes should be in lexicographical order");
        assertFalse(nodes.hasNext());
    }
    
    /**
     * Tests adding a simple edge to the graph.
     */
    @Test
    public void testAddSingleEdge() {
        graph.addNode("a");
        graph.addNode("b");
        graph.addEdge("a", "b", "1");
        
        Iterator<String> children = graph.listChildren("a");
        assertTrue(children.hasNext());
        assertEquals("b(1)", children.next(), "Node 'a' should have child 'b' with label '1'");
        assertFalse(children.hasNext());
        
        String xml = graph.listChildrenXML("a");
        assertEquals("<nodes>\n  <edge label=\"1\" node=\"b\" />\n</nodes>", xml, "XML should represent one edge");
        
        // Check that the edge is directed
        children = graph.listChildren("b");
        assertFalse(children.hasNext(), "Node 'b' should have no children");
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
        
        Iterator<String> children = graph.listChildren("a");
        assertTrue(children.hasNext());
        assertEquals("b(1)", children.next());
        assertEquals("c(2)", children.next());
        assertFalse(children.hasNext());
        
        children = graph.listChildren("b");
        assertTrue(children.hasNext());
        assertEquals("c(3)", children.next());
        assertFalse(children.hasNext());
        
        String xml = graph.listChildrenXML("a");
        assertEquals("<nodes>\n  <edge label=\"1\" node=\"b\" />\n  <edge label=\"2\" node=\"c\" />\n</nodes>", xml);
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
        
        Iterator<String> children = graph.listChildren("a");
        assertTrue(children.hasNext());
        assertEquals("b(1)", children.next());
        assertEquals("b(2)", children.next());
        assertFalse(children.hasNext());
        
        String xml = graph.listChildrenXML("a");
        assertEquals("<nodes>\n  <edge label=\"1\" node=\"b\" />\n  <edge label=\"2\" node=\"b\" />\n</nodes>", xml);
    }
    
    /**
     * Tests adding reflexive edges (from a node to itself).
     */
    @Test
    public void testReflexiveEdge() {
        graph.addNode("a");
        
        graph.addEdge("a", "a", "1");
        
        Iterator<String> children = graph.listChildren("a");
        assertTrue(children.hasNext());
        assertEquals("a(1)", children.next());
        assertFalse(children.hasNext());
        
        String xml = graph.listChildrenXML("a");
        assertEquals("<nodes>\n  <edge label=\"1\" node=\"a\" />\n</nodes>", xml);
    }
    
    /**
     * Tests the example from Figure 3 in the assignment description.
     */
    @Test
    public void testFigure3Example() {
        // Create the graph from Figure 3
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        
        graph.addEdge("a", "a", "1"); // Reflexive edge
        graph.addEdge("a", "b", "4");
        graph.addEdge("a", "c", "4");
        graph.addEdge("a", "c", "7");
        graph.addEdge("b", "a", "2");
        graph.addEdge("b", "c", "3");
        
        // Test listChildren for node 'a'
        Iterator<String> children = graph.listChildren("a");
        assertTrue(children.hasNext());
        assertEquals("a(1)", children.next());
        assertEquals("b(4)", children.next());
        assertEquals("c(4)", children.next());
        assertEquals("c(7)", children.next());
        assertFalse(children.hasNext());
        
        // Test listChildrenXML for node 'a'
        String xml = graph.listChildrenXML("a");
        assertEquals("<nodes>\n  <edge label=\"1\" node=\"a\" />\n  <edge label=\"4\" node=\"b\" />\n  <edge label=\"4\" node=\"c\" />\n  <edge label=\"7\" node=\"c\" />\n</nodes>", xml);
        
        // Test listNodes
        Iterator<String> nodes = graph.listNodes();
        assertEquals("a", nodes.next());
        assertEquals("b", nodes.next());
        assertEquals("c", nodes.next());
        assertFalse(nodes.hasNext());
    }
    
    /**
     * Tests the specific example from the assignment for listChildren("a").
     */
    @Test
    public void testListChildrenExample() {
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        
        graph.addEdge("a", "b", "x");
        graph.addEdge("a", "b", "y");
        graph.addEdge("a", "c", "z");
        
        Iterator<String> children = graph.listChildren("a");
        assertTrue(children.hasNext());
        assertEquals("b(x)", children.next());
        assertEquals("b(y)", children.next());
        assertEquals("c(z)", children.next());
        assertFalse(children.hasNext());
    }
    
    /**
     * Tests the specific example from the assignment for listChildrenXML("a").
     */
    @Test
    public void testListChildrenXMLExample() {
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        
        graph.addEdge("a", "b", "x");
        graph.addEdge("a", "b", "y");
        graph.addEdge("a", "c", "z");
        
        String xml = graph.listChildrenXML("a");
        assertEquals("<nodes>\n  <edge label=\"x\" node=\"b\" />\n  <edge label=\"y\" node=\"b\" />\n  <edge label=\"z\" node=\"c\" />\n</nodes>", xml);
    }
}
