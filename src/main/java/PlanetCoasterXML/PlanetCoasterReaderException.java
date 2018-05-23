//PACKAGE

package PlanetCoasterXML;

//EXCEPTION CLASS

/**
 * This is a little Exception Class that be thrown when an error occur in the Reader
 */
public class PlanetCoasterReaderException extends Exception {
    // Constructor that accepts a message
    PlanetCoasterReaderException(String message) {
        super(message);
    }
}
