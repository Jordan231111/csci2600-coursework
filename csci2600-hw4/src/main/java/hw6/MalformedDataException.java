package hw6;

/**
 * Exception thrown when the format of LEGO data is invalid.
 */
public class MalformedDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new MalformedDataException with the specified message.
     *
     * @param message The detail message explaining the cause of the exception
     */
    public MalformedDataException(String message) {
        super(message);
    }

    /**
     * Creates a new MalformedDataException with the specified message and cause.
     *
     * @param message The detail message explaining the cause of the exception
     * @param cause The cause of this exception
     */
    public MalformedDataException(String message, Throwable cause) {
        super(message, cause);
    }
} 