//PACKAGE

package PlanetCoasterXML;

//IMPORTS

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.common.collect.LinkedListMultimap;

//CLASS

/**
 * This class read a single planet coaster xml file storing the data in a LinkedListMultimap;
 * I cannot use a normal HashMap because i need duplicate keys, and to keep the insertion order.
 * <br>
 * The Keys are Strings because the translations Keys are simple strings.
 * <br>
 * The translated phrase is stored into a byte[] because i can store it in UTF-8 (or another encoding if i want to change).
 */
public class PlanetCoasterReader {
    //----------------------------------------------------------
    /**
     * LinkedListMultimap that contains the loaded file.
     */
    private LinkedListMultimap<String, byte[]> loaded_file_multimap;

    /**
     * Boolean that represent if is the new file, if so i need to store comments.
     */
    private final boolean isnewFile;
    //----------------------------------------------------------

    /**
     * The constructor method open the file and call a method to fill the LinkedListMultimap.
     *
     * @param file_to_open the path with the filename to open;
     * @param isfinalfile  if is the final file i need to consider comments;
     * @throws PlanetCoasterReaderException throw an invalid UTF-8 charset exception (from scan_node()) or a generic Exception if the XML file is not valid;
     */
    public PlanetCoasterReader(final String file_to_open, final boolean isfinalfile) throws PlanetCoasterReaderException {
        if(file_to_open == null){
            throw new IllegalArgumentException("No file to open given...");
        }
        Element root; //The root element of the xml file
        Document document_file;
        isnewFile = isfinalfile; //if is the new file i need to store comments
        //-------------------------------------------------------------------------
        try {
            //Opening the file
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder domParser = dbf.newDocumentBuilder();
            document_file = domParser.parse(new File(file_to_open));
            root = document_file.getDocumentElement();
            Node start_node = root.getFirstChild();
            //Choosing the First node
            if (!start_node.getNodeName().equals("translation")) { //The first node is not "translation"
                start_node = start_node.getNextSibling(); //Check the node after the first
                if (!start_node.getNodeName().equals("translation")) { //The xml file is not correct
                    //can't find the start node, throw an error
                    throw new PlanetCoasterReaderException("Bad PlanetCoaster-XML File! Check your XML file");
                }
            }
            //create a LinkedListMultimap with the size of xml childs
            loaded_file_multimap = LinkedListMultimap.create(start_node.getChildNodes().getLength());
            //scan the xml and fill the multimap
            scan_node(start_node); //starting from the root
        } catch (SAXParseException e) { //error reading the xml file
            throw new PlanetCoasterReaderException("Error(SAXParseException)-->" + e.getMessage());
        } catch (FileNotFoundException e) { //error opening the file
            throw new PlanetCoasterReaderException("Error(FileNotFoundException)-->" + e.getMessage());
        } catch (Exception e) { //generic error
            throw new PlanetCoasterReaderException("Generic Error-->" + e.getMessage());
        }
    } //Constructor

    /**
     * Copy constructor for PlanetCoasterReader.
     *
     * @param copyReader The other PlanetCoasterReader to copy.
     */
    public PlanetCoasterReader(final PlanetCoasterReader copyReader){
        if(copyReader == null){
            throw new IllegalArgumentException("No reader to copy given...");
        }
        isnewFile = copyReader.isnewFile;
        loaded_file_multimap = LinkedListMultimap.create(copyReader.loaded_file_multimap);
    }

    /**
     * Constructor that copy a user LinkedListMultimap into the reader.
     *
     * @param copyMultimap The user created LinkedListMultimap to insert into this reader.
     */
    public PlanetCoasterReader(final LinkedListMultimap<String, byte[]> copyMultimap){
        if(copyMultimap == null || copyMultimap.size() == 0){
            throw new IllegalArgumentException("No Multimap to copy given...");
        }
        isnewFile = false;
        loaded_file_multimap = LinkedListMultimap.create(copyMultimap);
    }

    /**
     * This method scan all the nodes under the root.
     * <br>
     * While scanning all the nodes, this method fill the two arrays with all the values.
     *
     * @param inputNode the root node, where i start to scan;
     * @throws PlanetCoasterReaderException if the string format is not valid;
     */
    private void scan_node(final Node inputNode) throws PlanetCoasterReaderException {
        Window.print_log("---------------------------");
        Node node = inputNode;
        Window.print_log("Inside the XML File");
        NodeList child_nodes_list = node.getChildNodes(); //Entering root childs
        int childs_count = child_nodes_list.getLength(); //Counting childrens
        int comment_index = 1;
        Window.print_log("XML Childs:" + childs_count);
        try {
            for (int i = 0; i < childs_count; i++) { //For every child
                node = child_nodes_list.item(i); //I get the node
                if (node.getNodeName().equals("entry")) { //if is an entry
                    NamedNodeMap node_att = node.getAttributes(); //Checking tag attrs
                    if (node_att.getLength() > 0) { //The entry has attrs, save them
                        loaded_file_multimap.put(((Attr) node_att.item(0)).getValue(), ((Attr) node_att.item(1)).getValue().getBytes(StandardCharsets.UTF_8));
                    }
                } else { //if is a comment
                    if (isnewFile) { //store comments only if is the new file
                        if (node.getNodeType() == Element.COMMENT_NODE) {
                            Comment comment = (Comment) node;
                            //i use as key "XMLPARSER-Comment" and an index to make every comment different
                            //it's better to not store comments with the same key
                            loaded_file_multimap.put("XMLPARSER-Comment " + comment_index, comment.getData().getBytes(StandardCharsets.UTF_8));
                            comment_index++;
                        }
                    } //is_new_file
                } //else_is_a_comment
            } //for_en
        } catch (Exception e) {
            Window.print_log("An error occurred during the scan of the xml file!");
            throw new PlanetCoasterReaderException("Scan node error-->" + e.getMessage());
        }
        Window.print_log("Reader Arrays Created!");
        Window.print_log("---------------------------");
    }

    /**
     * Method that return a copy of this file loaded LinkedListMultimap.
     *
     * @return A copy of the LinkedListMultimap loaded in this class.
     */
    public final LinkedListMultimap<String, byte[]> getLoadedFileMultimap(){
        return LinkedListMultimap.create(loaded_file_multimap);
    }

    /**
     * This function extract all the keys from the loaded file, returning them.
     *
     * @return an ArrayList with all the keys in the loaded file;
     */
    public final ArrayList<String> extractKeys() {
        return new ArrayList<String>(loaded_file_multimap.keys());
    }

    /**
     * This function extract all the entries from the loaded file, returning them.
     * REMEMBER that if "isfinalfile" is true this include comments.
     *
     * @return an ArrayList with all the values (entries) in the loaded file;
     */
    public final ArrayList<byte[]> extractValues() {
        ArrayList<byte[]> return_array = new ArrayList<byte[]>();
        for (final String key : loaded_file_multimap.keys()) {
            //I want to add all the entries that has a key
            return_array.add(loaded_file_multimap.get(key).get(0));
        }
        return return_array;
    }

    /**
     * This function extract all the comments from the loaded file, returning them.
     * <br>
     * Remember that the reader has "isnewFile", this means the comments are loaded only in the second file.
     * <br>
     * (You can simply change that in the Window class if you want, setting it at true).
     *
     * @return an ArrayList with all the comments in the loaded file;
     */
    public final ArrayList<byte[]> extractComments() {
        ArrayList<byte[]> return_array = new ArrayList<byte[]>();
        for (final String key : loaded_file_multimap.keys()) {
            if (key.contains("XMLPARSER-Comment")) {
                return_array.add(loaded_file_multimap.get(key).get(0));
            }
        }
        return return_array;
    }

} //end_class
