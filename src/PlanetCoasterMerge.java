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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

//CLASS
/**
 * This class read a single planet coaster xml file storing the data in two arrays: Keys and Values
 * @author ScrappyCocco
 * */
class PlanetCoasterMerge {
    ArrayList<String> Keys; //The translation Keys
    ArrayList<byte[]> utf8_values; //The translation values
    private boolean isnewFile;

    /**
     * The constructor method open the file and call a method to fill the arrays
     * @param file_top_open the path with the filename to open
     * @param isfinalfile if is the final file i need to consider comments
     * @throws Exception throw an invalid UTF-8 charset exception (from scan_current_node())
     */
    PlanetCoasterMerge(String file_top_open, boolean isfinalfile) throws Exception{
        Element root; //The root element of the xml file
        Document document_file;
        isnewFile=isfinalfile;
        Keys=new ArrayList<String>();
        utf8_values = new ArrayList<byte[]>();
        //-------------------------------------------------------------------------
        try{
            //Opening the file
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder domParser = dbf.newDocumentBuilder();
            document_file=domParser.parse(new File(file_top_open));
            root=document_file.getDocumentElement();
            Node start_node=root.getFirstChild();
            //Choosing the First node
            if(!start_node.getNodeName().equals("translation")){
                start_node=start_node.getNextSibling();
                if(!start_node.getNodeName().equals("translation")){
                    throw new Exception("Bad File!");
                }
            }
            scan_current_node(start_node); //starting from the root
        }catch(SAXParseException e){
            System.out.println("Error-->"+e.getMessage());
            System.exit(1); //1 error reading the xml
        }catch(FileNotFoundException e){
            System.out.println("Error file not found-->"+e.getMessage());
            System.exit(1); //1 error opening the xml
        }catch(Exception e){ //generic error
            System.out.println("Generic Error-->"+e.getMessage()+"-");
            e.printStackTrace();
            System.exit(1); //
        }
    }//Constructor

    /**
     * This method scan all the nodes under the root
     * @param node the root
     * @throws UnsupportedEncodingException if the string format is not valid
     */
    private void scan_current_node(Node node) throws UnsupportedEncodingException{
        System.out.println("---------------------------");
        System.out.println("We're in the XML File");
        NodeList child_nodes_list=node.getChildNodes(); //Entering root sons
        int childs_count = child_nodes_list.getLength();
        System.out.println("Childs:"+childs_count); //Counting childrens
        for(int i=0;i<childs_count;i++){ //For every child
            node=child_nodes_list.item(i); //I get the node
            if(node.getNodeName().equals("entry")) { //if is an entry
                NamedNodeMap node_att = node.getAttributes(); //Checking tag attrs
                if (node_att.getLength() > 0) { //The entry has attrs, save them
                    Keys.add(((Attr) node_att.item(0)).getValue());
                    utf8_values.add(((Attr) node_att.item(1)).getValue().getBytes(Charset.forName("UTF-8")));
                }
            }else{ //if is a comment
                if(isnewFile) {
                    if (node.getNodeType() == Element.COMMENT_NODE) {
                        Comment comment = (Comment) node;
                        Keys.add("Comment");
                        utf8_values.add(comment.getData().getBytes(Charset.forName("UTF-8")));
                    }
                }
            }
        }
        System.out.println("Arrays Created!");
        System.out.println("---------------------------");
    }
    //----------------------------------------------------------

}
