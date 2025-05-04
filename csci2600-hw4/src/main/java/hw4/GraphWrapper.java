package hw4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper for the Graph ADT that provides a specific interface for testing.
 * This class is not an ADT.
 */
public class GraphWrapper {
    
    // The graph being wrapped, now generic
    private Graph<String, String> graph;
    
    /**
     * Constructs a new GraphWrapper with an empty Graph<String, String>.
     */
    public GraphWrapper() {
        graph = new Graph<String, String>();
    }
    
    /**
     * Adds a node to the graph.
     * 
     * @param nodeData the data of the node to add
     */
    public void addNode(String nodeData) {
        graph.addNode(nodeData);
    }
    
    /**
     * Adds an edge from the parent node to the child node with the given label.
     * 
     * @param parentNode the data of the parent node
     * @param childNode the data of the child node
     * @param edgeLabel the label of the edge
     */
    public void addEdge(String parentNode, String childNode, String edgeLabel) {
        graph.addEdge(parentNode, childNode, edgeLabel);
    }
    
    /**
     * Returns an iterator over all nodes in the graph in lexicographical order.
     * 
     * @return an iterator over all nodes in lexicographical order
     */
    public Iterator<String> listNodes() {
        // graph.getNodes() now returns Set<String>
        Set<String> nodes = graph.getNodes();
        List<String> sortedNodes = new ArrayList<>(nodes);
        Collections.sort(sortedNodes);
        return sortedNodes.iterator();
    }
    
    /**
     * Returns an iterator over all children of the given parent node in the format
     * "childNode(edgeLabel)" in lexicographical order by node name and secondarily by edge label.
     * 
     * @param parentNode the data of the parent node
     * @return an iterator over strings representing children with edge labels
     */
    public Iterator<String> listChildren(String parentNode) {
        // graph.getChildrenWithLabels() now returns Map<String, List<String>>
        Map<String, List<String>> childrenWithLabels = graph.getChildrenWithLabels(parentNode);
        List<String> result = new ArrayList<>();
        
        // Create a sorted list of child nodes for consistent ordering
        List<String> sortedChildNodes = new ArrayList<>(childrenWithLabels.keySet());
        Collections.sort(sortedChildNodes);
        
        // For each child node in sorted order
        for (String childNode : sortedChildNodes) {
            List<String> labels = childrenWithLabels.get(childNode);
            // Sort the labels for this child
            Collections.sort(labels);
            
            // Add each child-label pair in the required format
            for (String label : labels) {
                result.add(childNode + "(" + label + ")");
            }
        }
        
        // The result list is naturally sorted by child node and then label due to the loops
        return result.iterator();
    }
    
    /**
     * Returns an XML representation of all children of the given parent node.
     * The children are listed in lexicographical order by node name and secondarily by edge label.
     * 
     * @param parentNode the data of the parent node
     * @return an XML string representing the children with edge labels
     */
    public String listChildrenXML(String parentNode) {
        // graph.getChildrenWithLabels() now returns Map<String, List<String>>
        Map<String, List<String>> childrenWithLabels = graph.getChildrenWithLabels(parentNode);
        StringBuilder xml = new StringBuilder("<nodes>\n");
        
        // Create a list to hold all edge entries for sorting
        List<String> edgeEntries = new ArrayList<>();
        
        // Create a sorted list of child nodes for consistent ordering
        List<String> sortedChildNodes = new ArrayList<>(childrenWithLabels.keySet());
        Collections.sort(sortedChildNodes);
        
        // For each child node in sorted order
        for (String childNode : sortedChildNodes) {
            List<String> labels = childrenWithLabels.get(childNode);
            // Sort the labels for this child
            Collections.sort(labels);
            
            // Add each child-label pair in XML format to the list
            for (String label : labels) {
                edgeEntries.add("  <edge label=\"" + label + "\" node=\"" + childNode + "\" />");
            }
        }
        
        // The edgeEntries list is already sorted by node then label
        
        // Add all sorted entries to the XML
        for (String entry : edgeEntries) {
            xml.append(entry).append("\n");
        }
        
        xml.append("</nodes>");
        return xml.toString();
    }
}
