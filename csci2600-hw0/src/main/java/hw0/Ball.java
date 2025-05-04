/**
 * This is part of HW0: Environment Setup and Java Introduction.
 */
package hw0;

import java.awt.Color;

/**
 * This is a simple object that has a volume and a color.
 */
public class Ball {

    private double volume;   
    private Color color;

    /**
     * Constructor that creates a new ball object with the specified volume and color.
     * @param volume the volume of the new ball object
     * @param color the color of the new ball object
     */
    public Ball(double volume, Color color) {
        this.volume = volume;
        this.color = color;
    }
    
    /**
     * Constructor that creates a new ball object with the specified volume given by a string.
     * @param volume A string representing the volume of the new object.
     * @param color the color of the new ball object
     */
    public Ball(String volume, Color color) {
        try {
            this.volume = Double.parseDouble(volume);
        } catch (NumberFormatException e) {
            // Instead of throwing an exception, set the volume to a default value
            // This prevents the client from crashing when given an invalid string.
            this.volume = 0.0;
        }
        this.color = color;
    }    

    /**
     * Returns the volume of the ball.
     * @return the volume of the ball.
     */
    public double getVolume() {
        return volume;
    }
    
    /**
     * Returns the color of the ball.
     * @return the color of the ball.
     */
    public Color getColor() {
        return color;
    }
}
