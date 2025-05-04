/**
 * This is part of HW0: Environment Setup and Java Introduction.
 */
package hw0;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;

/**
 * This is a container that can be used to contain Balls.
 * A given ball may only appear in a BallContainer once.
 */
public class BallContainer implements Iterable<Ball> {

    // Contents of the BallContainer.
    private Set<Ball> contents;

    /**
     * Constructor that creates a new ball container.
     */
    public BallContainer() {
        contents = new LinkedHashSet<Ball>();
    }

    /**
     * Implements the Iterable interface for this container.
     * @return an Iterator over the Ball objects contained in this container
     */
    public Iterator<Ball> iterator() {
        // Wrap the iterator in an unmodifiable set to prevent external modification.
        return Collections.unmodifiableSet(contents).iterator();
    }

    /**
     * Adds a ball to the container. This method returns <tt>true</tt>
     * if the ball was successfully added to the container, i.e., the ball was
     * not already in the container.
     * @param b ball to be added
     * @return true if the ball was successfully added; false if it is already in the container.
     */
    public boolean add(Ball b) {
        if (contents.contains(b)) {
            return false;
        }
        return contents.add(b);
    }

    /**
     * Removes a ball from the container.
     * @param b ball to be removed
     * @return true if the ball was successfully removed; false if it was not in the container.
     */
    public boolean remove(Ball b) {
        return contents.remove(b);
    }

    /**
     * Returns the total volume of all balls in the container.
     * @return the volume of the contents of the container.
     */
    public double getVolume() {
        double total = 0.0;
        for (Ball b : contents) {
            total += b.getVolume();
        }
        return total;
    }

    /**
     * Returns the number of balls in this container.
     * @return the number of balls in this container.
     */
    public int size() {
        return contents.size();
    }
    
    /**
     * Returns the number of different colors for the balls in this container.
     * @return the number of different colors for the balls in this container.
     */
    public int differentColors() {
        HashSet<java.awt.Color> colorSet = new HashSet<>();
        for (Ball b : contents) {
            colorSet.add(b.getColor());
        }
        return colorSet.size();
    }
    
    /**
     * Returns true if all balls in this container have the same color;
     * otherwise returns false.
     * @return true if all balls have the same color; false otherwise.
     */
    public boolean areSameColor() {
        if (contents.isEmpty()) {
            return true;
        }
        Iterator<Ball> it = contents.iterator();
        java.awt.Color firstColor = it.next().getColor();
        while (it.hasNext()) {
            if (!it.next().getColor().equals(firstColor)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Empties the container.
     */
    public void clear() {
        contents.clear();
    }

    /**
     * Returns true if this container contains the specified ball.
     * @param b ball to be checked
     * @return true if the container contains the ball; false otherwise.
     */
    public boolean contains(Ball b) {
        return contents.contains(b);
    }
}
