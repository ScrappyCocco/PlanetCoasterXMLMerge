//IMPORTS
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
 * This class call the Merge to the 2 files and merge them into a single array, then write it into the final file
 * @author ScrappyCocco
 * */
class PlanetCoasterWriter {
    private ArrayList<String> oldKeys;
    private ArrayList<String> newKeys;

    private ArrayList<byte[]> oldUTFTrans, newUTFTrans;
    boolean has_finished = false;
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

        System.out.println("First File:");//second file (old)
        PlanetCoasterMerge oldFile = new PlanetCoasterMerge(oldPath, false);
        System.out.println("Second File:"); //second file (new)
        PlanetCoasterMerge newFile = new PlanetCoasterMerge(newPath, true);
        oldKeys=oldFile.Keys;
        oldUTFTrans=oldFile.utf8_values;

        newKeys=newFile.Keys;
        newUTFTrans=newFile.utf8_values;

        System.out.println("Char utf8_values:"); //Print test - 1
        if(newKeys.indexOf("TrackElementDesc_TK_SP_Immelmann_180Left")==-1){
            throw new Exception("PLANET COASTER KEY NOT FOUND - does it exist?");
        }else {
            System.out.println(new String(newUTFTrans.get(newKeys.indexOf("TrackElementDesc_TK_SP_Immelmann_180Left")), "UTF-8"));
        }
        //----------------------------------------------------------
        System.out.println("Starting array merge");
        merge_arrays();
        System.out.println("End of array merge");
        System.out.println("Copy utf8_values:"); //Print test - 2
        if(newKeys.indexOf("BuildingPartCategory_Building_Signs")==-1){
            throw new Exception("PLANET COASTER KEY NOT FOUND -  - does it exist?");
        }else {
            System.out.println(new String(newUTFTrans.get(newKeys.indexOf("BuildingPartCategory_Building_Signs")), "UTF-8"));
        }
        System.out.println("---------------------------");
        //----------------------------------------------------------
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder builder = dbf.newDocumentBuilder();
            xml_document =builder.newDocument();
        }catch(ParserConfigurationException e){
            e.printStackTrace();
        }
        Element root_element = xml_document.createElement("localisation"); //creating root node
        root_element.setAttribute("xmlns","http://www.planetcoaster.com/CommunityTranslation");
        xml_document.appendChild(root_element); //E la metto come base
        Element translation= xml_document.createElement("translation");
        //----------------------------------------------------------
        Element entry;
        Comment comment;
        int comments_counter=0;
        for(int i=0;i<newKeys.size();i++){
            if(newKeys.get(i).equals("Comment")){
                comments_counter++;
                comment = xml_document.createComment(new String(newUTFTrans.get(i), "UTF-8"));
                translation.appendChild(comment);
            }else {
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
        DOMImplementation impl= xml_document.getImplementation();
        DOMImplementationLS implLS=(DOMImplementationLS)impl.getFeature("LS","3.0");
        LSSerializer ser=implLS.createLSSerializer();
        String output=ser.writeToString(xml_document);
        output=output.replace("UTF-16","UTF-8"); //UTF-8
        output=prettyFormat(output,2);
        System.out.println("Saving the file..." );
        try{
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file_name), "UTF-8"));
            out.write(output);
            out.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("File Saved!\nExecution Ended!" );
        has_finished = true;
    }//Constructor

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
     * This function merge the first array with the second, leaving the translated sentences and adding the sentences from the new file
     */
    private void merge_arrays(){
        System.out.println("---------------------------");
        ArrayList<String> remKeys=new ArrayList<String>();
        ArrayList<String> removed_values=new ArrayList<String>();
        int start_size=oldKeys.size();
        int values_found=0;
        int values_removed=0;
        while(oldKeys.size()>0){
            int pos=newKeys.indexOf(oldKeys.get(0));
            if(pos!=-1){
                newUTFTrans.set(pos, oldUTFTrans.get(0));
                oldKeys.remove(0);
                oldUTFTrans.remove(0);
                values_found++;
            }else{
                values_removed++;
                try {
                    removed_values.add(new String(oldUTFTrans.get(0), "UTF-8"));
                }catch (java.io.UnsupportedEncodingException e){
                    System.out.print("FATAL ERROR");
                }
                remKeys.add(oldKeys.get(0));
                oldUTFTrans.remove(0);
                oldKeys.remove(0);
            }
        }//while
        System.out.println("Start Size:"+start_size+" Found:"+values_found+" Removed:"+values_removed);
        System.out.println("Final Size OldArray:"+oldKeys.size());
        System.out.print("Creating string loss file...");
        try{
            PrintWriter writer = new PrintWriter("StringLoss.txt", "UTF-8");
            if((removed_values.size()!=values_removed||remKeys.size()!=values_removed)){
                System.out.println("\n(Something's strange)\n");
            }
            for(int i=0;i<removed_values.size();i++) {
                writer.println(remKeys.get(i)+" - "+removed_values.get(i));
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("\nError creating the file:"+e);
        }
        System.out.println("Done!");
        System.out.println("---------------------------");
    }
    //----------------------------------------------------------

}
