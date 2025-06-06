package hw0;

import java.util.Random;

/**
 * RandomHello selects a random greeting to display to the user.
 */
public class RandomHello {

    /**
     * Uses a RandomHello object to print
     * a random greeting to the console.
     */
    public static void main(String[] argv) {
        RandomHello randomHello = new RandomHello();
        System.out.println(randomHello.getGreeting());
    }

    /**
     * @return a random greeting from a list of five different greetings.
     */
    public String getGreeting() {
        Random randomGenerator = new Random();

        // Create an array of greetings
        String[] greetings = new String[5];
        greetings[0] = "Hello, World";
        greetings[1] = "Hola Mundo";
        greetings[2] = "Bonjour, le Monde";
        greetings[3] = "Hallo Welt";
        greetings[4] = "Ciao Mondo";

        // Generate a random index between 0 and 4 (inclusive)
        int randomIndex = randomGenerator.nextInt(greetings.length);

        // Return the greeting at the random index
        return greetings[randomIndex];
    }
}