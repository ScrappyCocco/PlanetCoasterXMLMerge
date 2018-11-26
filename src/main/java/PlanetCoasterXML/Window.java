//PACKAGE

package PlanetCoasterXML;

//IMPORTS

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;

//CLASS

/**
 * This class create the main window with the buttons to choose the 2 files and to create the new file;
 * This class has a listener for each button, plus some threads for the processing.
 */
final class Window extends JFrame {

    /**
     * Java Logger used to log few things about errors and information
     */
    private static final Logger LOGGER = Logger.getLogger( Window.class.getName() );

    /**
     * Labels used in the window to tell the user the status of the program.
     */
    private final JLabel labelOldFile, labelNewFile, result, label_first_file_status, label_second_file_status;

    /**
     * Buttons in the window.
     */
    private final JButton elaborate, selectOldFile, selectNewFile, duplicates, print_text;

    /**
     * Fields that save the path to the loaded files.
     */
    private String path_old_file, path_new_file;

    /**
     * Fields that represent the loaded files.
     */
    private PlanetCoasterReader first_file, second_file;

    /**
     * Field used to check if the file has been loaded.
     */
    private boolean done_first_file = false, done_second_file = false;

    //default windows translation location
    //These variables are read from config.json using loadVariablesFromJson()
    /**
     * Default path to open when the user click "Open file", the rest of it is in config.json.
     */
    private String default_path = "C:\\Users\\" + System.getProperty("user.name") + "\\";

    /**
     * Url to check the last program version, loaded from config.json.
     */
    private String github_api_url;

    /**
     * Current program version.
     */
    private String version;

    /**
     * Program default font.
     */
    private final String font = "Verdana";
    /**
     * Program default font size.
     */
    private final int fontSize = 14;

    /**
     * Draw the main window with buttons and labels...
     * <br>
     * (this constructor is not an important part of the program, it just create the UI).
     */
    private Window() {
        //First load variables
        loadVariablesFromJson();
        //----------------------------------------------------------------
        //Start creating the window
        Container background;
        print_log("Executing Window()");
        print_log("Creating the Window...!");
        setSize(500, 600);
        setTitle("PlanetCoaster Translation Manager - v" + version);
        background = this.getContentPane();
        //----------------------------------------------------------------
        JPanel total_panel = new JPanel();
        total_panel.setLayout(new GridLayout(3, 1));
        //----------------------------------------------------------------
        try { //Set system UI
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            print_log("Cannot set system UI - Error:" + ex);
        }
        //----------------------------------------------------------------
        //old file - panel and buttons
        labelOldFile = new JLabel("OLD File name");
        labelOldFile.setFont(new Font(font, Font.PLAIN, fontSize));
        label_first_file_status = new JLabel("...");
        label_first_file_status.setFont(new Font(font, Font.PLAIN, fontSize));
        selectOldFile = new JButton("Select Old File");
        selectOldFile.setFont(new Font(font, Font.PLAIN, fontSize));
        selectOldFile.addActionListener(new XMLFilePath(true, this));
        JPanel old_file_label_panel = new JPanel();
        old_file_label_panel.add(labelOldFile);
        JPanel old_file_select_panel = new JPanel();
        old_file_select_panel.add(selectOldFile);
        JPanel oldFileStatus = new JPanel();
        oldFileStatus.add(label_first_file_status);
        //Setting the panels
        JPanel old_total_panel = new JPanel();
        old_total_panel.setLayout(new GridLayout(3, 1));
        old_total_panel.add(old_file_label_panel);
        old_total_panel.add(old_file_select_panel);
        old_total_panel.add(oldFileStatus);
        old_total_panel.setBorder(BorderFactory.createTitledBorder(null, "Old XML File", TitledBorder.CENTER, TitledBorder.TOP, new Font(font, Font.BOLD, fontSize), Color.black));
        total_panel.add(old_total_panel);
        //----------------------------------------------------------------
        //new file - panel and buttons
        labelNewFile = new JLabel("NEW File name");
        labelNewFile.setFont(new Font(font, Font.PLAIN, fontSize));
        label_second_file_status = new JLabel("...");
        label_second_file_status.setFont(new Font(font, Font.PLAIN, fontSize));
        selectNewFile = new JButton("Select New File");
        selectNewFile.setFont(new Font(font, Font.PLAIN, fontSize));
        selectNewFile.addActionListener(new XMLFilePath(false, this));
        JPanel new_file_label_panel = new JPanel();
        new_file_label_panel.add(labelNewFile);
        JPanel new_file_select_panel = new JPanel();
        new_file_select_panel.add(selectNewFile);
        JPanel newFileStatus = new JPanel();
        newFileStatus.add(label_second_file_status);
        //Setting the panels
        JPanel new_total_panel = new JPanel();
        new_total_panel.setLayout(new GridLayout(3, 1));
        new_total_panel.add(new_file_label_panel);
        new_total_panel.add(new_file_select_panel);
        new_total_panel.add(newFileStatus);
        new_total_panel.setBorder(BorderFactory.createTitledBorder(null, "New XML File", TitledBorder.CENTER, TitledBorder.TOP, new Font(font, Font.BOLD, fontSize), Color.black));
        total_panel.add(new_total_panel);
        //----------------------------------------------------------------
        //elaborate button - panel and buttons
        elaborate = new JButton("Merge Files");
        elaborate.addActionListener(new Elaborate_Files());
        elaborate.setFont(new Font(font, Font.PLAIN, fontSize));
        //duplicates button - panel and buttons
        duplicates = new JButton("Check for Duplicates");
        duplicates.addActionListener(new Find_Duplicates());
        duplicates.setFont(new Font(font, Font.PLAIN, fontSize));
        //print file button - panel and buttons
        print_text = new JButton("Print Elements to text file");
        print_text.addActionListener(new PrintElements(this));
        print_text.setFont(new Font(font, Font.PLAIN, fontSize));
        //Setting button total panel
        JPanel buttons_execute_panel = new JPanel();
        buttons_execute_panel.setLayout(new GridLayout(3, 1));
        buttons_execute_panel.add(elaborate);
        buttons_execute_panel.add(duplicates);
        buttons_execute_panel.add(print_text);
        JPanel wrapperPanel1 = new JPanel(new GridBagLayout());
        wrapperPanel1.add(buttons_execute_panel);
        //Result label
        result = new JLabel("Ready");
        result.setFont(new Font(font, Font.PLAIN, fontSize));
        JPanel state = new JPanel();
        state.add(result);
        //Window total panel
        JPanel result_total_panel = new JPanel();
        result_total_panel.setLayout(new GridLayout(2, 1));
        result_total_panel.add(state);
        result_total_panel.add(wrapperPanel1);
        result_total_panel.setBorder(BorderFactory.createTitledBorder(null, "Execution and current state", TitledBorder.CENTER, TitledBorder.TOP, new Font(font, Font.BOLD, fontSize), Color.black));
        total_panel.add(result_total_panel);
        //----------------------------------------------------------------
        background.add(total_panel);
        print_log("Window Fully Created!");
        //----------------------------------------------------------------
        //Execute the version control, check if on Github there's a newer version than the current one
        programVersionControl();
        //----------------------------------------------------------------
        setResizable(false);
        setLocationRelativeTo(null);
        //----------------------------------------------------------------
        //Setting program icon
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("planet_icon.png");
            if (in != null) {
                BufferedImage myImg = ImageIO.read(in);
                setIconImage(myImg);
                in.close();
                print_log("Window icon successfully loaded!");
            } else {
                print_log("Cannot load window icon....");
            }
        } catch (java.io.IOException e) {
            print_log("File is null - " + e.toString());
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        print_log("Program ready");
    } //END OF Constructor

    //------------------------------------------------------------------------------------------------------------
    //BEGIN OF USEFUL METHODS

    /**
     * Method that enable/disable the buttons during files processing.
     *
     * @param active is the buttons should be active or not;
     */
    private void toggleButtons(final boolean active) {
        selectOldFile.setEnabled(active);
        selectNewFile.setEnabled(active);
        elaborate.setEnabled(active);
        duplicates.setEnabled(active);
        print_text.setEnabled(active);
    } //END OF toggle_buttons


    /**
     * Method that display an error box, called when an error occurs.
     *
     * @param errorMessage the error message to display in the box;
     */
    private void displayError(final String errorMessage) {
        result.setText("<html><i>An error occurred</i></html>");
        JLabel error_label = new JLabel(errorMessage);
        error_label.setFont(new Font(font, Font.BOLD, fontSize + 2));
        LOGGER.log(Level.SEVERE, "displayError() called: " + errorMessage, errorMessage);
        JOptionPane.showMessageDialog(this,
                error_label,
                "An error occurred",
                JOptionPane.ERROR_MESSAGE);
    } //END OF displayError


    /**
     * Method that display an info box, called when an event need to display information.
     *
     * @param infoMessage the information message to display in the box;
     */
    private void displayInfo(final String infoMessage) {
        JLabel text_label = new JLabel("<html>" + infoMessage + "<br/>(Check console log for more)</html>");
        text_label.setFont(new Font(font, Font.PLAIN, fontSize));
        LOGGER.log(Level.INFO, "displayInfo() called: " + infoMessage, infoMessage);
        JOptionPane.showMessageDialog(this,
                text_label);
    }


    /**
     * Static method to pretty print the log with time.
     *
     * @param s String to print;
     */
    static void print_log(final String s) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
        System.out.println(ft.format(date) + "-->" + s);
        LOGGER.log(Level.FINE, "print_log() called: " + s, s);
    } //END OF print_log


    /**
     * This method check the program version on github and compare it with the current version.
     * <br>
     * If the github version is newer a notification appears;
     */
    private void programVersionControl() {
        print_log("---STARTING programVersionControl---");
        JSONParser parser = new JSONParser();
        try {
            print_log("Downloading github JSON...");
            URL oracle = new URL(github_api_url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            JSONObject json_downloaded = (JSONObject) parser.parse(in.readLine());
            print_log("Downloading completed");
            double current_version = Double.parseDouble(version);
            double github_version = Double.parseDouble(json_downloaded.get("tag_name").toString());
            print_log("Remote version found:" + github_version + " this version is:" + version);
            if (github_version == current_version) {
                print_log("Remote version is the same of this one, no alert sent...");
            } else if (github_version > current_version) {
                print_log("Remote version is different, sending alert...");
                displayInfo("You have version:" + version + " Github version found:" + github_version + ". Consider downloading the newest version!");
            } else {
                print_log("Current version is newer than github, no alert sent...");
            }
        } catch (Exception ex) {
            print_log("VERSION CHECK ERROR - " + ex.toString());
        }
        print_log("---END OF programVersionControl---");
    } //END OF programVersionControl


    /**
     * This method load some variables from the file config.json.
     */
    private void loadVariablesFromJson() {
        try {
            print_log("---Starting Json Variables init---");
            JSONParser parser = new JSONParser();
            BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("config.json")));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            JSONObject json_loaded = (JSONObject) parser.parse(sb.toString());
            version = json_loaded.get("program_version").toString();
            default_path += json_loaded.get("default_path").toString();
            github_api_url = json_loaded.get("github_version_api_url").toString();
            print_log("---Completed Json Variables init---");
        } catch (java.io.IOException err) {
            print_log("Error (IOException):" + err);
            displayError(err.toString());
        } catch (org.json.simple.parser.ParseException err) {
            print_log("Error (ParseException):" + err);
            displayError(err.toString());
        }
    } //END OF loadVariablesFromJson

    /**
     * This function show a popup to let the user choose the first or the second file.
     *
     * @return an integer that represent the file chosen, 0 is the first file, 1 the second one;
     */
    private int choose_old_or_new_file() {
        String[] options = {"First file (Old XML)", "Second file (New XML)"};
        return JOptionPane.showOptionDialog(this,
                "Which file would you like to analyze and print?",
                "Choose the file",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]); //default button title
    }

    /**
     * This function use the other manage_choose_old_or_new_file.
     * <br>
     * Currently this is not used, it's here for convenience because it could be useful.
     *
     * @return The PlanetCoasterReader corresponding to the first or to the second file;
     */
    private PlanetCoasterReader manage_choose_old_or_new_file() {
        return manage_choose_old_or_new_file(false);
    }

    /**
     * This function use choose_old_or_new_file() to let the user choose the first or the second file.
     * <br>
     * If the file is not valid (not loaded) an error occur, and this function return a NULL value.
     *
     * @param display_errors if the method should use displayError() to show errors to the user;
     * @return The PlanetCoasterReader corresponding to the first or to the second file, NULL if the file is not valid (because is not loaded);
     */
    private PlanetCoasterReader manage_choose_old_or_new_file(final boolean display_errors) {
        int file_chosen = choose_old_or_new_file();
        if (file_chosen == 0) { //First file
            if (done_first_file) {
                return first_file;
            } else {
                print_log("User chose a file(first file) that's not ready... sending error message");
                if (display_errors) {
                    displayError("This file is not loaded, load it first!");
                }
                return null;
            }
        } else if (file_chosen == 1) { //Second file
            if (done_second_file) {
                return second_file;
            } else {
                print_log("User chose a file(second file) that's not ready... sending error message");
                if (display_errors) {
                    displayError("This file is not loaded, load it first!");
                }
                return null;
            }
        } else { //No file chosen
            return null;
        }
    }


    //------------------------------------------------------------------------------------------------------------
    //BEGIN OF LISTENER FOR WINDOW BUTTONS


    /**
     * Listener for choose the XML file.
     * <br>
     * This class ask the user to chose a file, then load the file and check for duplicates;
     */
    private class XMLFilePath implements ActionListener, Runnable {
        private final boolean isFirstFile;
        private final Window window_reference; //Used as parent frame to display messages

        /**
         * Constructor for inner class that open the xml
         *
         * @param isFirst if the user is opening the first file
         * @param win_ref window reference to call methods and to set window parent
         */
        XMLFilePath(final boolean isFirst, final Window win_ref) {
            window_reference = win_ref;
            isFirstFile = isFirst;
        }

        /**
         * This listener open the file-chooser window to choose the file to analyze.
         *
         * @param e the button that called the action;
         */
        public void actionPerformed(final ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(); //create the file chooser
            javax.swing.filechooser.FileFilter f1;
            if (isFirstFile) {
                f1 = new FileNameExtensionFilter("OLD Xml File (.xml)", "xml");
            } else {
                f1 = new FileNameExtensionFilter("NEW Xml File (.xml)", "xml");
            }
            fileChooser.addChoosableFileFilter(f1); //add the file filter
            fileChooser.setFileFilter(f1); //set the current filter
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false); //can select only a file
            //Try to open the frontier directory
            print_log("Trying to open the default path: " + default_path);
            fileChooser.setCurrentDirectory(new File(default_path));
            int result = fileChooser.showOpenDialog(labelOldFile);
            //check if the result is the "approve" button
            if (result == JFileChooser.APPROVE_OPTION) { //if the user choose a file
                File selectedFile = fileChooser.getSelectedFile(); //Take that file
                print_log("Selected file: " + selectedFile.getAbsolutePath());
                if (isFirstFile) {
                    done_first_file = false;
                    labelOldFile.setText(selectedFile.getName());
                    path_old_file = selectedFile.getAbsolutePath();
                    label_first_file_status.setText("Loading file...");
                } else {
                    done_second_file = false;
                    labelNewFile.setText(selectedFile.getName());
                    path_new_file = selectedFile.getAbsolutePath();
                    label_second_file_status.setText("Loading file...");
                }
                Thread t = new Thread(this); //i can create the file
                t.start(); //starting the process thread
            }
        } //button_pressed

        /**
         * This method manage the exceptions that may occur in the thread.
         * <br>
         * Basically it display an error message that say why the file has not loaded.
         */
        private void manage_exception() {
            if (isFirstFile) {
                done_first_file = false;
                label_first_file_status.setText("File load failed...");
            } else {
                done_second_file = false;
                label_second_file_status.setText("File load failed...");
            }
        }

        /**
         * Thread execution for loading the files.
         * <br>
         * Loading the first file:
         * If duplicate keys are found, a warning message is sent.
         * <br>
         * Loading the second file:
         * If duplicate keys are found, the program ask the user if he want to remove the duplicates;
         * In that case, it overwrite the file loaded map;
         * Otherwise, a warning message is sent.
         */
        public void run() {
            try {
                if (isFirstFile) { //LOADING THE FIRST FILE
                    first_file = new PlanetCoasterReader(path_old_file, false); //load the file
                    if (new PlanetCoasterDuplicates(first_file).file_has_duplicates()) { //Does the file has duplicates?
                        //Sending warning message for duplicates
                        JLabel text_label = new JLabel("<html>The first file(Old XML) has duplicate keys, you should manually remove them...<br/> You can use the duplicate function of this program to have a list!</html>");
                        text_label.setFont(new Font(font, Font.PLAIN, fontSize));
                        print_log("The first file has duplicates... sending warning message...");
                        JOptionPane.showMessageDialog(window_reference,
                                text_label,
                                "Duplicate keys found in Old XML File",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        print_log("No duplicates found in the first file!");
                    }
                    done_first_file = true;
                    label_first_file_status.setText("Loading completed!");
                    print_log("First file completely loaded");
                } else { //LOADING THE SECOND FILE
                    second_file = new PlanetCoasterReader(path_new_file, true); //load the file
                    if (new PlanetCoasterDuplicates(second_file).file_has_duplicates()) { //Does the file has duplicates?
                        print_log("The second file has duplicates... sending warning message...");
                        JLabel text_label = new JLabel("<html>The second file(New XML) has duplicate keys, would you like to remove them?<br/> (This will not edit the file, the keys will be removed from my copy of the file)</html>");
                        text_label.setFont(new Font(font, Font.PLAIN, fontSize));
                        //Ask the user if he want to remove the duplicates
                        int result = JOptionPane.showConfirmDialog(
                                window_reference,
                                text_label,
                                "Duplicate keys found in New XML File",
                                JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) { //Removing the duplicates
                            print_log("User accepted to remove the duplicates");
                            print_log("Start size:" + second_file.getLoadedFileMultimap().size());
                            //Overwrite the loaded file
                            int before_size = second_file.getLoadedFileMultimap().size();
                            second_file = new PlanetCoasterReader(PlanetCoasterDuplicates.clear_from_duplicates(second_file.getLoadedFileMultimap()));
                            print_log("Final size:" + second_file.getLoadedFileMultimap().size());
                            print_log("Overwriting second file loaded map...");
                            displayInfo((before_size - second_file.getLoadedFileMultimap().size()) + " duplicates removed! Your file is ready and clean.");
                        } else { //Leaving the duplicates
                            print_log("User refused to remove the duplicates, sending alert");
                            text_label.setText("<html>Remember you are doing a merge with a file with duplicates<br/>This will generate an end file with duplicates...</html>");
                            //Sending warning message
                            JOptionPane.showMessageDialog(window_reference,
                                    text_label,
                                    "Duplicate keys found in New XML File",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        print_log("No duplicates found in the second file!");
                    }
                    done_second_file = true;
                    label_second_file_status.setText("Loading completed!");
                    print_log("Second file completely loaded");
                }
            } catch (PlanetCoasterReaderException err) {
                manage_exception();
                print_log("Error (PlanetCoasterWriterException):" + err.toString());
                displayError("Error (PlanetCoasterWriterException):" + err.toString());
            } catch (Exception err) {
                manage_exception();
                print_log("Error (Generic Exception):" + err.toString());
                displayError("Error (Generic Exception):" + err.toString());
            }
        }
    } //END OF XMLFilePath_class


    /**
     * Listener that merge the 2 files with a thread.
     * (using PlanetCoasterWriter class).
     */
    private class Elaborate_Files implements ActionListener, Runnable {

        /**
         * Default empty constructor
         */
        Elaborate_Files() {
        }

        /**
         * Function called when the elaborate button is pressed.
         *
         * @param e The action that action happened;
         */
        public void actionPerformed(final ActionEvent e) {
            if (done_first_file && done_second_file) { //only if both are true
                Thread t = new Thread(this); //i can create the new file
                t.start(); //starting the process thread
            } else { //need to select files first
                result.setText("Select Files First!");
                toggleButtons(true);
            }
        } //button_pressed

        /**
         * Thread that merge the two files and create the new file.
         */
        public void run() {
            toggleButtons(false);
            result.setText("Working...");
            try {
                //Create the merger, that merge the two files
                PlanetCoasterMerge merger = new PlanetCoasterMerge(first_file, second_file);
                //Prepare the xml document
                Document xml_document = PlanetCoasterWriter.generate_xml_output(merger.getFinalFile());
                //Write the xml document
                PlanetCoasterWriter.write_xml_file(xml_document, "Final.xml");
                //Create string loss
                PlanetCoasterWriter.write_multimap_to_file(merger.getRemovedKeys(), "StringLoss.txt");
                //process ended
                result.setText("Done!");
                displayInfo("Merge completed!");
            } catch (PlanetCoasterWriterException err) {
                print_log("Error (PlanetCoasterWriterException):" + err.toString());
                displayError(err.toString());
            } catch (java.io.IOException err) {
                print_log("Error (IOException):" + err.toString());
                displayError(err.toString());
            }
            toggleButtons(true);
        } //run_thread
    } //END OF Elaborate_Class


    /**
     * Listener that open a file, and check for keys duplicates, saving them in a TXT file.
     * (using PlanetCoasterReader class).
     */
    private class Find_Duplicates implements ActionListener, Runnable {
        private PlanetCoasterReader file_choice;

        /**
         * Default empty constructor
         */
        Find_Duplicates() {
        }

        /**
         * Function called when the duplicates button is pressed.
         *
         * @param e The action that action happened;
         */
        public void actionPerformed(final ActionEvent e) {
            if (done_first_file || done_second_file) { //only if both are true
                file_choice = manage_choose_old_or_new_file(true);
                if (file_choice != null) {
                    Thread t = new Thread(this); //i can create the new file
                    t.start(); //starting the process thread
                }
            } else { //need to select files first
                result.setText("Select at least one file first!");
                toggleButtons(true);
            }
        } //button_pressed

        /**
         * Thread that read a planet coaster file and check for Keys duplicates.
         */
        public void run() {
            toggleButtons(false);
            result.setText("Working...");
            try {
                //Check the file for PlanetCoasterDuplicates
                PlanetCoasterDuplicates duplicates_searcher = new PlanetCoasterDuplicates(file_choice);
                //Write the duplicates to file
                PlanetCoasterWriter.write_string_array_to_file(duplicates_searcher.getDuplicatesKeys(), "Duplicates.txt");
                //process ended
                result.setText("End of duplicates search!");
                displayInfo("End of duplicates search! Found:" + duplicates_searcher.getNumberDuplicates_found() + " duplicates!");
            } catch (PlanetCoasterWriterException err) {
                print_log("Error (PlanetCoasterWriterException):" + err);
                displayError(err.toString());
            } catch (Exception err) {
                print_log("Error (Generic Exception):" + err);
                displayError(err.toString());
            }
            toggleButtons(true);
        } //run_thread
    } //END OF Duplicates_Class

    /**
     * Listener that print the elements of a file.
     */
    private class PrintElements implements ActionListener, Runnable {
        private final Window window_reference;
        private PlanetCoasterReader file_choice;

        /**
         * Constructor for PrintElements inner class.
         *
         * @param win_ref Window reference for methods and window parents;
         */
        PrintElements(Window win_ref) {
            window_reference = win_ref;
        }

        final String[] options = {"Keys Only", "Entries Only", "Comments Only", "Keys and Entries"};

        /**
         * Function called when the print button is pressed.
         *
         * @param e The action that action happened;
         */
        public void actionPerformed(ActionEvent e) {
            if (!(done_first_file || done_second_file)) {
                result.setText("Select at least one file first!");
                return;
            }
            file_choice = manage_choose_old_or_new_file(true);
            if (file_choice == null) {
                return;
            }
            Thread t = new Thread(this); //i can start printing
            t.start(); //starting the process thread
        }

        /**
         * Thread that ask the user what to print and then print the content to a file.
         */
        public void run() {
            toggleButtons(false);
            result.setText("Working...");
            int element_ask_result = JOptionPane.showOptionDialog(window_reference,
                    "<html>What would you like to print? Elements will be printed to a txt file<br/>Remember that comments are loaded only in the New XML file</html>",
                    "Choose the element to print",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]); //default button title
            try {
                boolean file_created = false;
                switch (element_ask_result) {
                    case -1:
                        print_log("No option chosen, skipping");
                        break;
                    case 0://Keys Only
                        print_log("User choice: Keys Only");
                        PlanetCoasterWriter.write_string_array_to_file(file_choice.extractKeys(), "Keys.txt");
                        file_created = true;
                        break;
                    case 1://Entries Only
                        print_log("User choice: Entries Only");
                        PlanetCoasterWriter.write_byte_array_to_file(file_choice.extractValues(), "Values.txt");
                        file_created = true;
                        break;
                    case 2://Comments Only
                        print_log("User choice: Comments Only");
                        PlanetCoasterWriter.write_byte_array_to_file(file_choice.extractComments(), "Comments.txt");
                        file_created = true;
                        break;
                    case 3://Keys and Entries
                        print_log("User choice: Keys and Entries");
                        PlanetCoasterWriter.write_multimap_to_file(file_choice.getLoadedFileMultimap(), "KeysAndEntries.txt");
                        file_created = true;
                        break;
                    default:
                        print_log("Case " + element_ask_result + " not controlled!");
                        break;
                } //end switch
                if (file_created) {
                    print_log("File print completed!");
                    displayInfo("File printed!");
                }
                result.setText("Print ended");
            } catch (PlanetCoasterWriterException err) {
                print_log("Error (PlanetCoasterWriterException):" + err);
                displayError(err.toString());
            }
            toggleButtons(true);
        }
    } //END OF PrintElements

    //------------------------------------------------------------------------------------------------------------
    //MAIN FUNCTION

    /**
     * Main execution of the program.
     *
     * @param args main args;
     */
    public static void main(String[] args) {
        System.out.println("---Execution started---");
        System.out.println("---Using Java version " + System.getProperty("java.version") + "---");
        new Window();
    }

    //---------------------------------------------------------------------------------------
}
