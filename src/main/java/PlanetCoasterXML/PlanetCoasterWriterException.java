//PACKAGE

package PlanetCoasterXML;

//EXCEPTION CLASS

/**
 * This is a little Exception Class that be thrown when an error occurred while writing in one of the methods in this class
 */
public class PlanetCoasterWriterException extends Exception {
    // Constructor that accepts a message
    public PlanetCoasterWriterException(String message) {
        super(message);
    }
}
