//PACKAGE

package PlanetCoasterXML;

//EXCEPTION CLASS

/**
 * This is a little Exception Class that be thrown when an error occurred while writing in one of the methods in this class.
 */
public class PlanetCoasterWriterException extends Exception {
    /**
     * Default exception constructor that accepts a message.
     *
     * @param message The exception message;
     */
    public PlanetCoasterWriterException(final String message) {
        super(message);
    }
}
