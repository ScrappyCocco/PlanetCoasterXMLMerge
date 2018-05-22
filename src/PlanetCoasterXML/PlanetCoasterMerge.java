//PACKAGE

package PlanetCoasterXML;

//IMPORTS

import com.google.common.collect.LinkedListMultimap;

//CLASS

/**
 * This class merge two PlanetCoasterReader using their keys
 * <br>
 * It's basically the class that take the translations from the old file and put them in the new file.
 */
public class PlanetCoasterMerge {
    //----------------------------------------------------------
    /**
     * Reference to first and second file that will used in the merge
     */
    private final PlanetCoasterReader first_file, second_file;
    /**
     * A LinkedListMultimap that contains the string removed during the merge
     */
    private LinkedListMultimap<String, byte[]> string_loss_multimap;
    //----------------------------------------------------------

    /**
     * The constructor ask for two PlanetCoasterReader to merge using their keys
     * <br>
     * The result of the merge can be accessed with getFinalFile()
     *
     * @param first_input_file  The first file (old xml file) to use in the merge
     * @param second_input_file The second file (new xml file) to use in the merge
     * @throws java.io.UnsupportedEncodingException Exception thrown if an error occur decoding a value
     */
    public PlanetCoasterMerge(PlanetCoasterReader first_input_file, PlanetCoasterReader second_input_file) throws java.io.UnsupportedEncodingException {
        //Saving values for later
        first_file = first_input_file;
        second_file = second_input_file;
        //----------------------------------------------------------
        //Print test - 1 - COMMENT THIS IF YOU DON'T WANT IT
        keys_check(0);
        //----------------------------------------------------------
        //merging the two arrays
        Window.print_log("Starting array merge");
        merge_arrays();
        Window.print_log("End of array merge");
        //----------------------------------------------------------
        //Print test - 2 - COMMENT THIS IF YOU DON'T WANT IT
        keys_check(1);
        Window.print_log("---------------------------");
        //----------------------------------------------------------
        Window.print_log("Merge Completed!");
    }

    /**
     * This constructor use the other constructor, after creating the Readers from the file paths
     * <br>
     * Currently this is not used, it's here for convenience because it could be useful
     *
     * @param oldFilePath The path for the first file to load, used to create a PlanetCoasterReader
     * @param newFilePath The path for the second file to load, used to create a PlanetCoasterReader
     * @throws PlanetCoasterReaderException         Exception thrown if an error occur creating the two PlanetCoasterReader for the two files
     * @throws java.io.UnsupportedEncodingException Exception thrown if an error occur decoding a value
     */
    public PlanetCoasterMerge(String oldFilePath, String newFilePath) throws PlanetCoasterReaderException, java.io.UnsupportedEncodingException {
        this(new PlanetCoasterReader(oldFilePath, false), new PlanetCoasterReader(newFilePath, true));
    }//Constructor

    /**
     * This function return the final file, created editing the second file during the merge
     *
     * @return The final file, resulted from merging the first and the second
     */
    public PlanetCoasterReader getFinalFile() {
        return second_file;
    }

    /**
     * This function return a LinkedListMultimap with all the strings lost during the merge
     * <br>
     * (Keys in the old file but not present in the new one)
     *
     * @return A LinkedListMultimap with all the strings lost during the merge
     */
    public LinkedListMultimap<String, byte[]> getRemovedKeys() {
        return string_loss_multimap;
    }

    /**
     * This method was initially done to check if the user was using a PlanetCoaster XML file, blocking the merge otherwise
     * <br>
     * Now it just check if the file has a PlanetCoaster XML entry, but it does not block the merge
     *
     * @param index Used to check before/after merge two different keys in the file
     * @throws java.io.UnsupportedEncodingException Exception thrown if an error occur decoding a value
     */
    private void keys_check(int index) throws java.io.UnsupportedEncodingException {
        switch (index) {
            case 0:
                Window.print_log("Char utf8_values:");
                if (second_file.loaded_file_multimap.get("TrackElementDesc_TK_SP_Immelmann_180Left").size() == 0) {
                    Window.print_log("PRE-MERGE: PLANET COASTER KEY NOT FOUND - Is the user using PlanetCoaster XML file?");
                } else {
                    Window.print_log(new String(second_file.loaded_file_multimap.get("TrackElementDesc_TK_SP_Immelmann_180Left").get(0), "UTF-8"));
                }
                break;
            case 1:
                Window.print_log("Char utf8_values:");
                if (second_file.loaded_file_multimap.get("BuildingPartCategory_Building_Signs").size() == 0) {
                    Window.print_log("AFTER-MERGE: PLANET COASTER KEY NOT FOUND - Is the user using PlanetCoaster XML file?");
                } else {
                    Window.print_log(new String(second_file.loaded_file_multimap.get("BuildingPartCategory_Building_Signs").get(0), "UTF-8"));
                }
                break;
        }
    }

    /**
     * This method merge the first LinkedListMultimap with the second,
     * leaving the translated sentences and adding the sentences from the new file
     * <br>
     * The result is stored in the second file
     */
    private void merge_arrays() {
        Window.print_log("---------------------------");
        int start_size = first_file.loaded_file_multimap.size();
        string_loss_multimap = LinkedListMultimap.create(); //Create the string loss multimap
        int values_found = 0; //Number of keys in the first file found in the second file
        int values_removed = 0; //Number of keys in the first file not found in the second file (keys loss counter)
        for (final String key : first_file.loaded_file_multimap.keys()) { //For each key of the first file
            //If the key from the first file is found in the second file, let's count it
            if (second_file.loaded_file_multimap.get(key).size() > 0) {
                values_found++;
                //Merge the old value in the new file
                second_file.loaded_file_multimap.get(key).set(0, first_file.loaded_file_multimap.get(key).get(0));
            } else { //not found in new file, counting a removed key and adding it
                values_removed++;
                string_loss_multimap.put(key, first_file.loaded_file_multimap.get(key).get(0));
            }
        }
        Window.print_log("Start Size:" + start_size + " Found:" + values_found + " Removed:" + values_removed);
        Window.print_log("New Merge Done!");
        Window.print_log("---------------------------");
    }

}//end_class
