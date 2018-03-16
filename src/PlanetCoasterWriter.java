//IMPORTS

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
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
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
 * This class call the Reader for the 2 files and merge them into a single array, then write it into the final file
 */
class PlanetCoasterWriter {
    //----------------------------------------------------------
    //Copy array to fast access
    private ArrayList<String> oldKeys, newKeys; //old file arrays
    private ArrayList<byte[]> oldUTFTrans, newUTFTrans; //new file arrays
    boolean has_finished; //for the thread, to check the end of the execution (default = false)
    private Window window_reference; //reference to main window to use print_log()
    //----------------------------------------------------------

    /**
     * The constructor create the PlanetCoasterReader for the two files and the merge them
     *
     * @param oldPath the old xml file path
     * @param newPath the new xml file path
     * @param ref     reference to main window to use print_log()
     * @throws Exception exceptions for generic errors
     */
    PlanetCoasterWriter(String oldPath, String newPath, Window ref) throws Exception {
        window_reference = ref;
        Document xml_document;
        String output_file_name = "Final.xml";
        //----------------------------------------------------------
        //reading the two files
        PlanetCoasterReader oldFile, newFile;
        window_reference.print_log("First File:");//first file (old)
        oldFile = new PlanetCoasterReader(oldPath, false, ref);
        window_reference.print_log("Second File:"); //second file (new)
        newFile = new PlanetCoasterReader(newPath, true, ref);
        //----------------------------------------------------------
        //Setting array references to keys and values
        oldKeys = oldFile.Keys;
        oldUTFTrans = oldFile.utf8_values;

        newKeys = newFile.Keys;
        newUTFTrans = newFile.utf8_values;
        //----------------------------------------------------------
        //Print test - 1 (check if a common key exist)
        window_reference.print_log("Char utf8_values:");
        if (newKeys.indexOf("TrackElementDesc_TK_SP_Immelmann_180Left") == -1) {
            throw new Exception("PLANET COASTER KEY NOT FOUND - does it exist?");
        } else {
            window_reference.print_log(new String(newUTFTrans.get(newKeys.indexOf("TrackElementDesc_TK_SP_Immelmann_180Left")), "UTF-8"));
        }
        //----------------------------------------------------------
        //merging the two arrays
        window_reference.print_log("Starting array merge");
        merge_arrays();
        window_reference.print_log("End of array merge");
        //----------------------------------------------------------
        //Print test - 2
        window_reference.print_log("Copy utf8_values:");
        if (newKeys.indexOf("BuildingPartCategory_Building_Signs") == -1) {
            throw new Exception("PLANET COASTER KEY NOT FOUND - does it exist?");
        } else {
            window_reference.print_log(new String(newUTFTrans.get(newKeys.indexOf("BuildingPartCategory_Building_Signs")), "UTF-8"));
        }
        window_reference.print_log("---------------------------");
        //----------------------------------------------------------
        //start creating the new xml
        xml_document = generate_xml_output();
        //write xml to file
        write_xml_file(xml_document, output_file_name);
        //----------------------------------------------------------
        window_reference.print_log("Execution Ended!");
        has_finished = true;
    }//Constructor


    /**
     * This function generate the xml Document from the newKeys/newUTFTrans arrays
     *
     * @return The generated XML document
     * @throws Exception if an error occur, throws an exception
     */
    private Document generate_xml_output() throws Exception {
        Document xml_document;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            xml_document = builder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new Exception("Error (ParserConfigurationException) --> " + e.getMessage());
        }
        Element root_element = xml_document.createElement("localisation"); //creating root node
        root_element.setAttribute("xmlns", "http://www.planetcoaster.com/CommunityTranslation");
        xml_document.appendChild(root_element); //Setting root child element
        Element translation = xml_document.createElement("translation");
        //----------------------------------------------------------
        //create the xml with elements, comments, etc
        Element entry;
        Comment comment;
        int comments_counter = 0;
        for (int i = 0; i < newKeys.size(); i++) { //for every key
            if (newKeys.get(i).equals("Comment")) { //is a comment?
                comments_counter++;
                comment = xml_document.createComment(new String(newUTFTrans.get(i), "UTF-8"));
                translation.appendChild(comment);
            } else { //is a normal entry
                entry = xml_document.createElement("entry"); //creating the node (tag)
                Attr key = xml_document.createAttribute("key"); //creating the attr
                key.setValue(newKeys.get(i));
                entry.setAttributeNode(key);
                Attr translation_attr = xml_document.createAttribute("translation"); //creating the attr
                translation_attr.setValue(new String(newUTFTrans.get(i), "UTF-8"));
                entry.setAttributeNode(translation_attr);
                translation.appendChild(entry);
            }
        }
        window_reference.print_log("Comments:" + comments_counter);
        root_element.appendChild(translation);
        window_reference.print_log("---------------------------");
        return xml_document;
    }


    /**
     * This function write the XML document to an XML file
     *
     * @param xml_document     the xml document to write
     * @param output_file_name the output file name
     * @throws Exception if an error occur during the creation of the file
     */
    private void write_xml_file(Document xml_document, String output_file_name) throws Exception {
        DOMImplementation impl = xml_document.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer ser = implLS.createLSSerializer();
        String output = ser.writeToString(xml_document);
        output = output.replace("UTF-16", "utf-8"); //UTF-8
        output = prettyFormat(output, 2); //format the xml file
        window_reference.print_log("Saving the file...");
        //write the file using utf-8
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file_name), "UTF-8"));
        out.write(output);
        out.close();
        window_reference.print_log("File Saved!");
    }


    /**
     * This function format the xml file (NOT MINE)
     *
     * @param input  the input xml to format
     * @param indent the indentations for formatting the code (always 2 in this program)
     * @return the new xml code formatted
     */
    private String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }


    /**
     * This function merge the first array with the second,
     * leaving the translated sentences and adding the sentences from the new file
     */
    private void merge_arrays() throws Exception {
        window_reference.print_log("---------------------------");
        ArrayList<String> remKeys = new ArrayList<String>();
        ArrayList<String> removed_values = new ArrayList<String>();
        int start_size = oldKeys.size();
        int values_found = 0;
        int values_removed = 0;
        while (oldKeys.size() > 0) { //while there are old keys
            int pos = newKeys.indexOf(oldKeys.get(0));
            if (pos != -1) { //if the old key exist in new file
                //write it
                newUTFTrans.set(pos, oldUTFTrans.get(0));
                oldKeys.remove(0);
                oldUTFTrans.remove(0);
                values_found++;
            } else {
                //the old key doesn't exist
                values_removed++;
                try { //saving the key as removed
                    removed_values.add(new String(oldUTFTrans.get(0), "UTF-8"));
                } catch (java.io.UnsupportedEncodingException e) {
                    window_reference.print_log("FATAL ERROR - UnsupportedEncodingException");
                    throw new Exception("Error (UnsupportedEncodingException) --> " + e.getMessage());
                }
                //removing the key
                remKeys.add(oldKeys.get(0));
                oldUTFTrans.remove(0);
                oldKeys.remove(0);
            }
        }//while
        window_reference.print_log("Start Size:" + start_size + " Found:" + values_found + " Removed:" + values_removed);
        window_reference.print_log("Final Size OldArray:" + oldKeys.size());
        window_reference.print_log("Creating string loss file...");
        try { //print all the lost strings
            if (values_removed > 0) {
                PrintWriter writer = new PrintWriter("StringLoss.txt", "UTF-8");
                if ((removed_values.size() != values_removed) || (remKeys.size() != values_removed)) {
                    window_reference.print_log("\n(Something's strange happened (removed_values container)\n");
                }
                for (int i = 0; i < removed_values.size(); i++) {
                    writer.println("Key: \"" + remKeys.get(i) + "\" - Value: \"" + removed_values.get(i) + "\"");
                }
                writer.close();
            } else {
                window_reference.print_log("\nSkipped creation of StringLoss.txt, no string removed...");
            }
        } catch (Exception e) {
            window_reference.print_log("\nError creating the file:" + e.toString());
            throw new Exception("Error --> " + e.getMessage());
        }
        window_reference.print_log("Merge Done!");
        window_reference.print_log("---------------------------");
    }
    //----------------------------------------------------------
}//end_class
