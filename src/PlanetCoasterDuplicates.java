//IMPORTS

import java.io.PrintWriter;
import java.util.ArrayList;

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
        PlanetCoasterReader fileSelected = null;
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
        //For searching for duplicates
        for (int i = 0; i < fileSelected.Keys.size(); i++) {
            String compare_string = fileSelected.Keys.get(i);
            boolean first_compare_is_comment = false;
            if (compare_string.equals("Comment")) { //The string is a comment. i have to get it
                compare_string = new String(fileSelected.utf8_values.get(i), "UTF-8");
                first_compare_is_comment = true;
            }
            for (int k = 0; k < fileSelected.Keys.size(); k++) {
                //removed_values.add(new String(oldUTFTrans.get(0), "UTF-8"));
                //The string is a comment. i have to get it, i compare it ONLY if the other is a comment
                if (fileSelected.Keys.get(k).equals("Comment") && i != k && first_compare_is_comment) {
                    //Getting the comment and comparing it
                    String comment_value = new String(fileSelected.utf8_values.get(k), "UTF-8");
                    //If the two comments are the same and the comment is not in the array
                    if (comment_value.equals(compare_string) && !duplicatesKeys.contains(compare_string)) {
                        duplicatesKeys.add(compare_string);
                    }
                } else if (!first_compare_is_comment) { //if the first is not a comment
                    //If the index is different AND the two keys are the same AND is not already in the final array
                    if (i != k && compare_string.equals(fileSelected.Keys.get(k)) && !duplicatesKeys.contains(compare_string)) {
                        duplicatesKeys.add(compare_string);
                    }
                }
            }//inside-for
        }//big for
    }


    /**
     * This function create the file that list all the duplicate keys that has been found
     *
     * @throws Exception if an error occur, thrown an exception
     */
    private void print_duplicates_to_file() throws Exception {
        try { //print all the lost strings
            if (duplicatesKeys.size() > 0) {
                PrintWriter writer = new PrintWriter("DuplicatesFound.txt", "UTF-8");
                for (String duplicatesKey : duplicatesKeys) {
                    writer.println("\"" + duplicatesKey + "\""); //print string to file
                }
                writer.close();
            } else {
                window_reference.print_log("Skipped creation of DuplicatesFound.txt, no duplicate keys found...");
            }
        } catch (Exception e) {
            window_reference.print_log("Error creating the file:" + e.toString());
            throw new Exception("File creation error-->" + e.getMessage());
        }
    }
    //----------------------------------------------------------
}//end_class
