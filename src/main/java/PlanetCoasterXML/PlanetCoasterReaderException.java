//PACKAGE

package PlanetCoasterXML;

//EXCEPTION CLASS

/**
 * This is a little Exception Class that be thrown when an error occur in the Reader.
 */
public class PlanetCoasterReaderException extends Exception {
    /**
     * Default exception constructor that accepts a message.
     *
     * @param message The exception message;
     */
    PlanetCoasterReaderException(final String message) {
        super(message);
    }
}
