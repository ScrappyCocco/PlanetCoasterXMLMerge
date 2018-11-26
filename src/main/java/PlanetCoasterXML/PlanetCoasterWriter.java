//PACKAGE

package PlanetCoasterXML;

//IMPORTS

import com.google.common.collect.LinkedListMultimap;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//CLASS

/**
 * This class is made of static methods because it does not need to have any field.
 * <br>
 * Every method is useful to to write a list to a file or to prepare an output (for example preparing the xml code).
 */
public class PlanetCoasterWriter {

    /**
     * Java Logger used to log few things about errors and information
     */
    private static final Logger LOGGER = Logger.getLogger( PlanetCoasterWriter.class.getName() );

    /**
     * This function generate the xml Document from the reader.
     * <br>
     * It's used to prepare the xml output after the merge.
     *
     * @param input_final_file The reader to use to generate the XML Document;
     * @return A XML Document, created from the input Reader;
     * @throws PlanetCoasterWriterException Exception thrown if an error occur reading the new document;
     */
    public static Document generate_xml_output(final PlanetCoasterReader input_final_file) throws PlanetCoasterWriterException {
        Document xml_document;
        //initialize the xml Document and the DocumentBuilder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            xml_document = builder.newDocument();
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE ,"An error occurred parsing the input file", e);
            throw new PlanetCoasterWriterException("Error (ParserConfigurationException) --> " + e.getMessage());
        }
        //Start creating the root of the xml file
        Element root_element = xml_document.createElement("localisation"); //creating root node
        Attr xmlns_attrib = xml_document.createAttribute("xmlns");
        xmlns_attrib.setValue("http://www.planetcoaster.com/CommunityTranslation");
        root_element.setAttributeNode(xmlns_attrib);
        xml_document.appendChild(root_element); //Setting root child element
        Element translation = xml_document.createElement("translation");
        //----------------------------------------------------------
        //create the xml with elements, comments, etc
        Element entry;
        Comment comment;
        LinkedListMultimap<String, byte[]> inputFileMultimap = input_final_file.getLoadedFileMultimap();
        int comments_counter = 0;
        for (final String xml_key : inputFileMultimap.keys()) {
            if (xml_key.contains("XMLPARSER-Comment")) { //It's a comment, create a XML comment, and attach it
                comments_counter++;
                comment = xml_document.createComment(new String(inputFileMultimap.get(xml_key).get(0), StandardCharsets.UTF_8));
                translation.appendChild(comment);
            } else { //It's a entry, create a XML tag in planet coaster style, and attach it
                entry = xml_document.createElement("entry"); //creating the node (tag)
                Attr key = xml_document.createAttribute("key"); //creating the attr
                key.setValue(xml_key);
                entry.setAttributeNode(key);
                Attr translation_attr = xml_document.createAttribute("translation"); //creating the attr
                translation_attr.setValue(new String(inputFileMultimap.get(xml_key).get(0), StandardCharsets.UTF_8));
                entry.setAttributeNode(translation_attr);
                translation.appendChild(entry);
            }
        }
        Window.print_log("XML File prepared, comments:" + comments_counter);
        root_element.appendChild(translation);
        Window.print_log("---------------------------");
        return xml_document;
    }

    /**
     * This function format the xml file (NOT MINE).
     *
     * @param input  the input xml to format;
     * @param indent the indentations for formatting the code (always 2 in this program);
     * @return the new xml code formatted;
     * @throws javax.xml.transform.TransformerException throw a TransformerException if there are problems with the xml file;
     */
    private static String prettyFormat(final String input, final int indent) throws javax.xml.transform.TransformerException {
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        transformerFactory.setAttribute("indent-number", indent);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(xmlInput, xmlOutput);
        return xmlOutput.getWriter().toString();
    }

    /**
     * This method write the XML document to an XML file.
     *
     * @param xml_document     the xml document to write;
     * @param output_file_name the output file name;
     * @throws java.io.IOException if an error occur during the creation of the file;
     */
    public static void write_xml_file(final Document xml_document, final String output_file_name) throws java.io.IOException {
        DOMImplementation impl = xml_document.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer ser = implLS.createLSSerializer();
        String output = ser.writeToString(xml_document);
        output = output.replace("UTF-16", "utf-8"); //UTF-8
        try {
            output = prettyFormat(output, 2); //format the xml file
        } catch (javax.xml.transform.TransformerException err) {
            Window.print_log("Error using prettyFormat (TransformerException)");
            throw new java.io.IOException("File prettyFormat error-->" + err.getMessage());
        }
        Window.print_log("Saving the file...");
        //write the file using utf-8
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file_name), StandardCharsets.UTF_8))) {
            out.write(output);
        }
        Window.print_log("File Saved!");
    }

    /**
     * This method write an ArrayList to a file, the file will have as name fileName.
     * <br>
     * This is used mainly to write a file with the duplicate keys found using PlanetCoasterDuplicates.
     *
     * @param input_array The ArrayList with the values to write in the file;
     * @param fileName    The name of the file to create;
     * @throws PlanetCoasterWriterException Exception thrown if an error occur while writing the file (java.io.IOException);
     */
    public static void write_string_array_to_file(final ArrayList<String> input_array, final String fileName) throws PlanetCoasterWriterException {
        try {
            if (input_array.size() > 0) {
                Window.print_log("Creation of " + fileName + " started...");
                PrintWriter writer = new PrintWriter(fileName, "UTF-8");
                for (final String output_string : input_array) {
                    writer.println("\"" + output_string + "\""); //print string to file
                }
                writer.close();
                Window.print_log("Creation of " + fileName + " successfully ended!");
            } else {
                Window.print_log("Skipped creation of " + fileName + ", the list is empty...");
            }
        } catch (java.io.IOException e) {
            Window.print_log("Error creating the file:" + e.toString());
            throw new PlanetCoasterWriterException("File creation error-->" + e.getMessage());
        }
    }

    /**
     * This method write an ArrayList to a file, the file will have as name fileName.
     * <br>
     * This is used mainly to write a file with the values of a Reader, for example the comments.
     *
     * @param input_array The ArrayList with the values to write in the file (converting them to string);
     * @param fileName    The name of the file to create;
     * @throws PlanetCoasterWriterException Exception thrown if an error occur while writing the file (java.io.IOException);
     */
    public static void write_byte_array_to_file(final ArrayList<byte[]> input_array, final String fileName) throws PlanetCoasterWriterException {
        try {
            if (input_array.size() > 0) {
                Window.print_log("Creation of " + fileName + " started...");
                try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
                    for (final byte[] output_string : input_array) {
                        writer.println("\"" + new String(output_string, StandardCharsets.UTF_8) + "\""); //print string to file
                    }
                }
                Window.print_log("Creation of " + fileName + " successfully ended!");
            } else {
                Window.print_log("Skipped creation of " + fileName + ", the list is empty...");
            }
        } catch (java.io.IOException e) {
            Window.print_log("Error creating the file:" + e.toString());
            throw new PlanetCoasterWriterException("File creation error-->" + e.getMessage());
        }
    }

    /**
     * This method write a LinkedListMultimap to a file, it use the other write_multimap_to_file with 3 parameters.
     *
     * @param multimap The multimap to write in the file;
     * @param fileName The name of the file to create;
     * @throws PlanetCoasterWriterException Exception thrown if an error occur while writing the file (java.io.IOException);
     */
    public static void write_multimap_to_file(final LinkedListMultimap<String, byte[]> multimap, final String fileName) throws PlanetCoasterWriterException {
        write_multimap_to_file(multimap, fileName, null, false);
    }

    /**
     * This method write a LinkedListMultimap to a file, using format "Key - Value" for each line.
     * <br>
     * There is also a keys_to_avoid array, if the user want to not write some keys (used to not write comments for example).
     *
     * @param multimap               The multimap to write in the file;
     * @param fileName               The name of the file to create;
     * @param keys_to_avoid          Array of the keys to skip while writing;
     * @param avoid_only_full_string (True and False are described below),
     *                               if avoid_only_full_string is True = the key in keys_to_avoid must be equal to multimap key,
     *                               if avoid_only_full_string is False = the key in keys_to_avoid could be equal or could contain the multimap key;
     * @throws PlanetCoasterWriterException Exception thrown if an error occur while writing the file (java.io.IOException);
     */
    public static void write_multimap_to_file(final LinkedListMultimap<String, byte[]> multimap, final String fileName, final HashSet<String> keys_to_avoid, final boolean avoid_only_full_string) throws PlanetCoasterWriterException {
        try {
            if (multimap.size() > 0) { //if there is something to write
                boolean consider_keys_to_avoid; //Boolean that indicate if i should consider the keys_to_avoid
                consider_keys_to_avoid = keys_to_avoid != null && keys_to_avoid.size() > 0; //Initialize the boolean
                Window.print_log("Creation of " + fileName + " started...");
                try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
                    for (final String key : multimap.keys()) { //For each key in the multimap
                        if (consider_keys_to_avoid) { //I have to consider the keys_to_avoid
                            boolean avoid_key = false; //Boolean that indicate if i have to skip this key or not
                            if (avoid_only_full_string) {
                                //The key must be equal
                                if (keys_to_avoid.contains(key)) {
                                    avoid_key = true;
                                }
                            } else {
                                //The key could be equal or can contain the multimap key
                                for (final String keys_to_avoid_entry : keys_to_avoid) {
                                    if (key.equals(keys_to_avoid_entry) || key.contains(keys_to_avoid_entry)) {
                                        avoid_key = true;
                                        break; //stop the cycle
                                    }
                                } //end key check cycle
                            }
                            if (!avoid_key) { //I can print that key
                                writer.println(key + " - " + new String(multimap.get(key).get(0), StandardCharsets.UTF_8)); //print string to file
                            }
                        } else { //consider_keys_to_avoid = false, just write normally
                            writer.println(key + " - " + new String(multimap.get(key).get(0), StandardCharsets.UTF_8)); //print string to file
                        }
                    }
                }
                Window.print_log("Creation of " + fileName + " successfully ended!");
            } else {
                Window.print_log("Skipped creation of " + fileName + ", the list is empty...");
            }
        } catch (java.io.IOException e) {
            Window.print_log("Error creating the file:" + e.toString());
            throw new PlanetCoasterWriterException("File creation error-->" + e.getMessage());
        }
    } //end of write_multimap_to_file

} //end_class
