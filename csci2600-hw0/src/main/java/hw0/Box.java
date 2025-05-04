/**
 * This is part of HW0: Environment Setup and Java Introduction.
 */
package hw0;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This is a container that can be used to contain Balls. The key difference
 * between a BallContainer and a Box is that a Box has a finite volume. Once a box
 * is full, a client cannot put in more Balls.
 */
public class Box implements Iterable<Ball> {

    /**
     * ballContainer is used to internally store balls for this Box.
     */
    private BallContainer ballContainer;
    
    /**
     * The maximum volume that this box can contain.
     */
    private double maxVolume;

    /**
     * Constructor that creates a new box.
     * @param maxVolume Total volume of balls that this box can contain.
     */
    public Box(double maxVolume) {
        this.maxVolume = maxVolume;
        this.ballContainer = new BallContainer();
    }

    /**
     * Implements the Iterable interface for this box.
     * @return an Iterator over the Ball objects contained in this box.
     */
    public Iterator<Ball> iterator() {
        return ballContainer.iterator();
    }

    /**
     * Adds a Ball to this box.
     * Returns true if the ball is successfully added (i.e., the ball is not
     * already in the box and adding it will not exceed the box's volume limit).
     * @param b Ball to be added.
     * @return true if added; false otherwise.
     */
    public boolean add(Ball b) {
        if (ballContainer.contains(b)) {
            return false;
        }
        if (ballContainer.getVolume() + b.getVolume() <= maxVolume) {
            return ballContainer.add(b);
        }
        return false;
    }

    /**
     * Returns an iterator that iterates over all balls in this box in ascending order by their size.
     * @return an iterator over all balls in ascending order by volume.
     */
    public Iterator<Ball> getBallsFromSmallest() {
        // Copy the balls into a list.
        List<Ball> sortedList = new ArrayList<>();
        for (Ball b : ballContainer) {
            sortedList.add(b);
        }
        // Sort the list by volume (ascending).
        Collections.sort(sortedList, new Comparator<Ball>() {
            public int compare(Ball b1, Ball b2) {
                return Double.compare(b1.getVolume(), b2.getVolume());
            }
        });
        return sortedList.iterator();
    }

    /**
     * Removes a ball from the box.
     * @param b Ball to be removed.
     * @return true if the ball was successfully removed; false otherwise.
     */
    public boolean remove(Ball b) {
        return ballContainer.remove(b);
    }

    /**
     * Returns the total volume of all balls in the box.
     * @return the volume of the contents of the box.
     */
    public double getVolume() {
       return ballContainer.getVolume();
    }

    /**
     * Returns the number of Balls in this box.
     * @return the number of Balls in the box.
     */
    public int size() {
        return ballContainer.size();
    }

    /**
     * Empties the box.
     */
    public void clear() {
        ballContainer.clear();
    }

    /**
     * Returns true if this box contains the specified Ball; false otherwise.
     * @param b Ball to check.
     * @return true if the box contains the ball; false otherwise.
     */
    public boolean contains(Ball b) {
        return ballContainer.contains(b);
    }
}
