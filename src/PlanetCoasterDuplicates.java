//IMPORTS

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//CLASS

/**
 * This class call the Reader to open the file and then check for duplicates in the keys array
 */
public class PlanetCoasterDuplicates {
    //----------------------------------------------------------
    boolean has_finished; //for the thread, to check the end of the execution (default = false)
    private Window window_reference; //reference to main window to use print_log()
    private ArrayList<String> duplicatesKeys; //array that store the duplicates found
    //----------------------------------------------------------

    /**
     * The constructor call the Reader to open the file, then check if every key is duplicate in the array
     *
     * @param filePath the file path of the file to open
     * @param ref      reference to main window to use print_log()
     * @throws Exception if an error occur, thrown an exception
     */
    PlanetCoasterDuplicates(String filePath, Window ref) throws Exception {
        window_reference = ref;
        PlanetCoasterReader fileSelected;
        //true for checking comments
        fileSelected = new PlanetCoasterReader(filePath, true, window_reference);
        //----------------------------------------------------------
        //scan for duplicates
        analyze_duplicates(fileSelected);
        //----------------------------------------------------------
        window_reference.print_log("Found " + duplicatesKeys.size() + " duplicates!");
        print_duplicates_to_file();
        //----------------------------------------------------------
        window_reference.print_log("Duplicates search ended");
        window_reference.print_log("---------------------------");
        has_finished = true;
    }


    /**
     * This function analyze if there are duplicates in the array of keys in the "fileSelected" file
     *
     * @param fileSelected the file to analyze
     * @throws Exception if an error occur during the file scan
     */
    private void analyze_duplicates(PlanetCoasterReader fileSelected) throws Exception {
        duplicatesKeys = new ArrayList<String>();
        Set<String> duplicates = new HashSet<String>();
        String index;
        window_reference.print_log("File keys:" + fileSelected.Keys.size());
        for (int position = 0; position < fileSelected.Keys.size(); ++position) {
            index = fileSelected.Keys.get(position);
            if (index.equals("Comment")) { //The string is a comment. i have to get it
                index = new String(fileSelected.utf8_values.get(position), "UTF-8");
                index = "<!--" + index + "-->"; //Use the xml-comment style to separate comments and keys
            }
            if (duplicates.contains(index)) { //If is already in the Set
                duplicatesKeys.add(index); //Is a duplicate
            } else {
                duplicates.add(index); //Put it in the Set
            }
        }//end_for
    }

    /**
     * Overload of print_duplicates_to_file(String...) without parameters
     * Use default file name "DuplicatesFound.txt"
     *
     * @throws Exception if an error occur during print_duplicates_to_file(String...), thrown an exception
     */
    private void print_duplicates_to_file() throws Exception {
        print_duplicates_to_file("DuplicatesFound.txt");
    }

    /**
     * This function create the file that list all the duplicate keys that has been found
     *
     * @throws Exception if an error occur, thrown an exception
     */
    private void print_duplicates_to_file(String fileName) throws Exception {
        try { //print all the lost strings
            if (duplicatesKeys.size() > 0) {
                window_reference.print_log("Creation of " + fileName + " started...");
                PrintWriter writer = new PrintWriter(fileName, "UTF-8");
                for (String duplicatesKey : duplicatesKeys) {
                    writer.println("\"" + duplicatesKey + "\""); //print string to file
                }
                writer.close();
                window_reference.print_log("Creation of " + fileName + " successfully ended!");
            } else {
                window_reference.print_log("Skipped creation of " + fileName + ", no duplicate keys found...");
            }
        } catch (Exception e) {
            window_reference.print_log("Error creating the file:" + e.toString());
            throw new Exception("File creation error-->" + e.getMessage());
        }
    }
    //----------------------------------------------------------
}//end_class
