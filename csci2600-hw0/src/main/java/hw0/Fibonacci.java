/**
 * This is part of HW0: Environment Setup and Java Introduction.
 */
package hw0;

import java.util.HashMap;
import java.util.Map;

/**
 * Fibonacci calculates the <var>n</var>th term in the Fibonacci sequence.
 *
 * The first two terms of the Fibonacci sequence are 0 and 1,
 * and each subsequent term is the sum of the previous two terms.
 *
 */
public class Fibonacci {

    // Memoization cache to store previously computed Fibonacci numbers
    private Map<Integer, Long> memo = new HashMap<>();

    /**
     * Calculates the desired term in the Fibonacci sequence.
     *
     * @param n the index of the desired term; the first index of the sequence is 0
     * @return the <var>n</var>th term in the Fibonacci sequence
     * @throws IllegalArgumentException if <code>n</code> is a negative number
     */
    public long getFibTerm(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(n + " is negative");
        }
        return fib(n);
    }

    /**
     * Helper method to compute Fibonacci numbers using memoization.
     *
     * @param n the index of the desired term
     * @return the <var>n</var>th Fibonacci number
     */
    private long fib(int n) {
        if (n == 0) {
            return 0L;
        } else if (n == 1) {
            return 1L;
        }

        // Check if the value is already computed
        if (memo.containsKey(n)) {
            return memo.get(n);
        }

        // Compute the Fibonacci number recursively and store it in the cache
        long result = fib(n - 1) + fib(n - 2);
        memo.put(n, result);
        return result;
    }
}
