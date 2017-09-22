//IMPORTS
import com.sun.jna.platform.win32.OaIdl;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
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
 * This class call the Merge for the 2 files and merge them into a single array, then write it into the final file
 * */
class PlanetCoasterWriter {
    //Copy array to fast access
    private ArrayList<String> oldKeys, newKeys;
    private ArrayList<byte[]> oldUTFTrans, newUTFTrans;

    boolean has_finished = false; //for the thread, to check the end of the execution
    //----------------------------------------------------------
    private Document xml_document;
    //----------------------------------------------------------

    /**
     * The constructor create the PlanetCoasterMerge for the two files and the merge them
     * @param oldPath the old xml file path
     * @param newPath the new xml file path
     * @throws Exception exceptions for generic errors
     */
    PlanetCoasterWriter(String oldPath,String newPath) throws Exception{
        String output_file_name = "Final.xml";

        PlanetCoasterMerge oldFile, newFile;
        try {
            System.out.println("First File:");//first file (old)
            oldFile = new PlanetCoasterMerge(oldPath, false);
            System.out.println("Second File:"); //second file (new)
            newFile = new PlanetCoasterMerge(newPath, true);
        }catch(Exception err){
            throw new Exception(err.toString());
        }
        //Setting array references to keys and values
        oldKeys=oldFile.Keys;
        oldUTFTrans=oldFile.utf8_values;

        newKeys=newFile.Keys;
        newUTFTrans=newFile.utf8_values;

        System.out.println("Char utf8_values:"); //Print test - 1 (check if a common key exist)
        if(newKeys.indexOf("TrackElementDesc_TK_SP_Immelmann_180Left")==-1){
            throw new Exception("PLANET COASTER KEY NOT FOUND - does it exist?");
        }else {
            System.out.println(new String(newUTFTrans.get(newKeys.indexOf("TrackElementDesc_TK_SP_Immelmann_180Left")), "UTF-8"));
        }
        //----------------------------------------------------------
        System.out.println("Starting array merge");
        try {
            merge_arrays(); //merging the two arrays
        }catch (Exception err){
            err.printStackTrace();
            throw new Exception(err.toString());
        }
        System.out.println("End of array merge");
        //----------------------------------------------------------
        System.out.println("Copy utf8_values:"); //Print test - 2
        if(newKeys.indexOf("BuildingPartCategory_Building_Signs")==-1){ //(check if a common key exist)
            throw new Exception("PLANET COASTER KEY NOT FOUND - does it exist?");
        }else {
            System.out.println(new String(newUTFTrans.get(newKeys.indexOf("BuildingPartCategory_Building_Signs")), "UTF-8"));
        }
        System.out.println("---------------------------");
        //----------------------------------------------------------
        //start creating the new xml
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder builder = dbf.newDocumentBuilder();
            xml_document =builder.newDocument();
        }catch(ParserConfigurationException e){
            e.printStackTrace();
            throw new Exception("Error (ParserConfigurationException) --> " + e.getMessage());
        }
        Element root_element = xml_document.createElement("localisation"); //creating root node
        root_element.setAttribute("xmlns","http://www.planetcoaster.com/CommunityTranslation");
        xml_document.appendChild(root_element); //Setting root child element
        Element translation= xml_document.createElement("translation");
        //----------------------------------------------------------
        //create the xml with elements, comments, etc
        Element entry;
        Comment comment;
        int comments_counter=0;
        for(int i=0;i<newKeys.size();i++){ //for every key
            if(newKeys.get(i).equals("Comment")){ //is a comment?
                comments_counter++;
                comment = xml_document.createComment(new String(newUTFTrans.get(i), "UTF-8"));
                translation.appendChild(comment);
            }else { //is a normal entry
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
        System.out.println("Comments:"+comments_counter);
        root_element.appendChild(translation);
        System.out.println("---------------------------");
        //----------------------------------------------------------
        //write the xml to a file
        DOMImplementation impl= xml_document.getImplementation();
        DOMImplementationLS implLS=(DOMImplementationLS)impl.getFeature("LS","3.0");
        LSSerializer ser=implLS.createLSSerializer();
        String output=ser.writeToString(xml_document);
        output=output.replace("UTF-16","utf-8"); //UTF-8
        output=prettyFormat(output,2); //format the xml file
        System.out.println("Saving the file..." );
        try{
            //write the file using utf-8
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file_name), "UTF-8"));
            out.write(output);
            out.close();
        }catch(IOException e){
            e.printStackTrace();
            throw new Exception("Error (IOException) --> " + e.getMessage());
        }
        System.out.println("File Saved!\nExecution Ended!" );
        has_finished = true;
    }//Constructor

    //----------------------------------------------------------

    /**
     * This function format the xml file
     * @param input the input xml to format
     * @param indent the indentations for formatting the code
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

    //----------------------------------------------------------

    /**
     * This function merge the first array with the second,
     * leaving the translated sentences and adding the sentences from the new file
     */
    private void merge_arrays() throws Exception{
        System.out.println("---------------------------");
        ArrayList<String> remKeys=new ArrayList<String>();
        ArrayList<String> removed_values=new ArrayList<String>();
        int start_size=oldKeys.size();
        int values_found=0;
        int values_removed=0;
        while(oldKeys.size()>0){ //while there are old keys
            int pos=newKeys.indexOf(oldKeys.get(0));
            if(pos!=-1){ //if the old key exist in new file
                //write it
                newUTFTrans.set(pos, oldUTFTrans.get(0));
                oldKeys.remove(0);
                oldUTFTrans.remove(0);
                values_found++;
            }else{
                //the old key doesn't exist
                values_removed++;
                try { //saving the key as removed
                    removed_values.add(new String(oldUTFTrans.get(0), "UTF-8"));
                }catch (java.io.UnsupportedEncodingException e){
                    System.out.print("FATAL ERROR - UnsupportedEncodingException");
                    throw new Exception("Error (UnsupportedEncodingException) --> " + e.getMessage());
                }
                //removing the key
                remKeys.add(oldKeys.get(0));
                oldUTFTrans.remove(0);
                oldKeys.remove(0);
            }
        }//while
        System.out.println("Start Size:"+start_size+" Found:"+values_found+" Removed:"+values_removed);
        System.out.println("Final Size OldArray:"+oldKeys.size());
        System.out.print("Creating string loss file...");
        try{ //print all the lost strings
            if(values_removed>0) {
                PrintWriter writer = new PrintWriter("StringLoss.txt", "UTF-8");
                if ((removed_values.size() != values_removed) || (remKeys.size() != values_removed)) {
                    System.out.println("\n(Something's strange)\n");
                }
                for (int i = 0; i < removed_values.size(); i++) {
                    writer.println("Key: \"" + remKeys.get(i) + "\" - Value: \"" + removed_values.get(i) + "\"");
                }
                writer.close();
            }else{
                System.out.println("\nSkipped creation of StringLoss.txt, no string removed...");
            }
        } catch (Exception e) {
            System.out.println("\nError creating the file:"+e.toString());
            e.printStackTrace();
            throw new Exception("Error --> " + e.getMessage());
        }
        System.out.println("Merge Done!");
        System.out.println("---------------------------");
    }
    //----------------------------------------------------------

}
