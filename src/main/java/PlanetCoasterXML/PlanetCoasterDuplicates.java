//PACKAGE

package PlanetCoasterXML;

//IMPORTS

import com.google.common.collect.LinkedListMultimap;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//CLASS

/**
 * This class call the Reader to open the file and then check for duplicates keys.
 */
public class PlanetCoasterDuplicates {
    //----------------------------------------------------------
    /**
     * Array that store the duplicates keys found
     */
    private ArrayList<String> duplicatesKeys;
    /**
     * Boolean that represent if there are duplicates in the given file
     */
    private boolean duplicates_found;
    //----------------------------------------------------------

    /**
     * This constructor take a PlanetCoasterReader in input, and check if there is any duplicate key
     *
     * @param file The input file to check for duplicates
     */
    public PlanetCoasterDuplicates(PlanetCoasterReader file) {
        //----------------------------------------------------------
        //scan for duplicates
        analyze_duplicates(file);
        //----------------------------------------------------------
        Window.print_log("Found " + duplicatesKeys.size() + " duplicates!");
        //----------------------------------------------------------
        Window.print_log("Duplicates search ended");
        Window.print_log("---------------------------");
    }

    /**
     * This constructor use the other constructor, after creating the Readers from the file path
     * <br>
     * Currently this is not used, it's here for convenience because it could be useful
     *
     * @param filePath the path of the file to load
     * @throws PlanetCoasterReaderException         Exception thrown if an error occur creating the PlanetCoasterReader
     */
    public PlanetCoasterDuplicates(String filePath) throws PlanetCoasterReaderException{
        this(new PlanetCoasterReader(filePath, true));
    }

    /**
     * This function return a boolean that indicate if the file has duplicates or not
     *
     * @return A boolean that indicate if the file has duplicates or nor
     */
    public boolean file_has_duplicates() {
        return duplicates_found;
    }

    /**
     * This function return an integer value that indicate the number of duplicates found
     *
     * @return An integer value that indicate the number of duplicates found
     */
    public int getNumberDuplicates_found() {
        return duplicatesKeys.size();
    }

    /**
     * This function return an ArrayList that contains all the duplicate keys found
     *
     * @return An ArrayList that contains all the duplicate keys found
     */
    public ArrayList<String> getDuplicatesKeys() {
        return duplicatesKeys;
    }

    /**
     * This method analyze if there are duplicates in the keys of the "fileSelected" file
     *
     * @param fileSelected the file to analyze
     */
    private void analyze_duplicates(PlanetCoasterReader fileSelected) {
        duplicatesKeys = new ArrayList<String>();
        Set<String> duplicates = new HashSet<String>(); //Using an HashSet for convenience and speed
        String current_value;
        Window.print_log("File keys:" + fileSelected.loaded_file_multimap.size());
        //for each key in the selected file
        for (final String xml_key : fileSelected.loaded_file_multimap.keys()) {
            current_value = xml_key;
            if (xml_key.contains("XMLPARSER-Comment")) { //The string is a comment. i have to get it
                current_value = new String(fileSelected.loaded_file_multimap.get(xml_key).get(0), StandardCharsets.UTF_8);
                current_value = "<!--" + current_value + "-->"; //Use the xml-comment style to separate comments and keys
            }
            if (duplicates.contains(current_value)) { //If is already in the Set
                duplicatesKeys.add(current_value); //Is a duplicate, store it in the array
                if (!duplicates_found) { //Set that we found a duplicate
                    duplicates_found = true;
                }
            } else { //Not a duplicate
                duplicates.add(current_value); //Put it in the Set
            }
        }//end_for
    }

    /**
     * This function edit a LinkedListMultimap removing all duplicate keys
     * <br>
     * This should be used only with an original file
     * <br>
     * If the file has been edited from someone (translated in part for example) this could lead to losing some important entries
     * (Because the cycle when find a key, start removing all his duplicates)
     *
     * @param input_map The LinkedListMultimap to clear from duplicates
     * @return A copy of the input LinkedListMultimap without duplicates
     */
    public static LinkedListMultimap<String, byte[]> clear_from_duplicates(LinkedListMultimap<String, byte[]> input_map) {
        Window.print_log("Starting file cleaning...");
        for (final String key : input_map.keys()) { //for each key in the input file
            if (input_map.get(key).size() > 1) { //if there's more than 1 key
                //For each duplicate key, remove that key and his value
                for (int i = 1; i < input_map.get(key).size(); i++) {
                    input_map.remove(key, input_map.get(key).get(i));
                }
            }
        }
        Window.print_log("File cleaned!");
        return input_map;
    }

}//end_class
