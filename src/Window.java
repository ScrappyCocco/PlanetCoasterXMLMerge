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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//CLASS
/**
 * This class create the main window with the buttons to choose the 2 files and to create the new file
 * This class has a listener for each button, plus a thread for the processing.
 * */
public class Window extends JFrame {

    private JLabel labelOldFile, labelNewFile, result;
    private JButton elaborate,selectOldFile,selectNewFile, duplicates;

    private String path_old_file, path_new_file;
    private boolean done_first_file =false, done_second_file =false;

    private String default_path = "C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Local\\Frontier Developments\\Planet Coaster\\Translations";
    private String version = "1.13";

    private String font = "Verdana";
    private int fontSize = 14;

    /**
     * Draw the main window with buttons and labels...
     * (this is not an important part of the program)
     */
    private Window(){
        Container background;

        print_log("Program started!");
        print_log("Creating the Window...!");
        setSize(500,500);
        setTitle("PlanetCoaster Translation Manager - v" + version);
        background=this.getContentPane();
        //----------------------------------------------------------------
        JPanel total_panel=new JPanel();
        total_panel.setLayout(new GridLayout(3,1));
        //----------------------------------------------------------------
        try { //Set system UI
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            print_log("Error:" + ex);
        }
        //old file - panel and buttons
        labelOldFile=new JLabel("<html>OLD File name <br/> (Or File to check duplicates)</html>");
        labelOldFile.setFont(new Font(font, Font.PLAIN, fontSize));

        selectOldFile=new JButton("Select Old File");
        selectOldFile.setFont(new Font(font, Font.PLAIN, fontSize));
        selectOldFile.addActionListener(new OldFilePath());
        JPanel oldL=new JPanel();
        oldL.add(labelOldFile);
        JPanel oldS=new JPanel();
        oldS.add(selectOldFile);

        JPanel old_total_panel=new JPanel();
        old_total_panel.setLayout(new GridLayout(2,1));
        old_total_panel.add(oldL);
        old_total_panel.add(oldS);
        old_total_panel.setBorder(BorderFactory.createTitledBorder(null, "Old XML File", TitledBorder.CENTER, TitledBorder.TOP, new Font(font,Font.BOLD,fontSize), Color.black));
        total_panel.add(old_total_panel);
        //----------------------------------------------------------------
        //new file - panel and buttons
        labelNewFile=new JLabel("NEW File name");
        labelNewFile.setFont(new Font(font, Font.PLAIN, fontSize));
        selectNewFile=new JButton("Select New File");
        selectNewFile.setFont(new Font(font, Font.PLAIN, fontSize));
        selectNewFile.addActionListener(new NewFilePath());
        JPanel newL=new JPanel();
        newL.add(labelNewFile);
        JPanel newS=new JPanel();
        newS.add(selectNewFile);

        JPanel new_total_panel=new JPanel();
        new_total_panel.setLayout(new GridLayout(2,1));
        new_total_panel.add(newL);
        new_total_panel.add(newS);
        new_total_panel.setBorder(BorderFactory.createTitledBorder(null, "New XML File", TitledBorder.CENTER, TitledBorder.TOP, new Font(font,Font.BOLD,fontSize), Color.black));
        total_panel.add(new_total_panel);
        //----------------------------------------------------------------
        //elaborate file - panel and buttons
        elaborate=new JButton("Process Files");
        elaborate.addActionListener(new Elaborate_Files());
        elaborate.setFont(new Font(font, Font.PLAIN, fontSize));

        duplicates = new JButton("Check for Duplicates");
        duplicates.addActionListener(new Find_Duplicates());
        duplicates.setFont(new Font(font, Font.PLAIN, fontSize));

        JPanel buttons_execute_panel = new JPanel();
        buttons_execute_panel.setLayout(new GridLayout(2,1));
        buttons_execute_panel.add(elaborate);
        buttons_execute_panel.add(duplicates);
        JPanel wrapperPanel1 = new JPanel(new GridBagLayout());
        wrapperPanel1.add(buttons_execute_panel);

        result=new JLabel("Ready");
        result.setFont(new Font(font, Font.PLAIN, fontSize));
        JPanel state = new JPanel();
        state.add(result);

        JPanel result_total_panel=new JPanel();
        result_total_panel.setLayout(new GridLayout(2,1));
        result_total_panel.add(state);
        result_total_panel.add(wrapperPanel1);
        result_total_panel.setBorder(BorderFactory.createTitledBorder(null, "Execution and current state", TitledBorder.CENTER, TitledBorder.TOP, new Font(font,Font.BOLD,fontSize), Color.black));
        total_panel.add(result_total_panel);
        //----------------------------------------------------------------
        background.add(total_panel);
        print_log("Window Fully Created!");
        setResizable(false);
        setLocationRelativeTo(null);
        //Setting program icon
        try {
            setIconImage(ImageIO.read(new File("planet_icon.png")));
        }
        catch (IOException exc) {
            print_log("Icon \"planet_icon.png\" not found in execution dir... Skipping");
            exc.printStackTrace();
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //---------------------------------------------------------------------------------------
    /**Listener for OLD file button chooser*/
    class OldFilePath implements ActionListener {
        /**
         * This listener open the file-chooser window to choose the old file to analyze
         * @param e the button that called the action
         */
        public void actionPerformed(ActionEvent e){
            try{
                JFileChooser fileChooser = new JFileChooser(); //create the file chooser
                javax.swing.filechooser.FileFilter f1 = new FileNameExtensionFilter("OLD Xml File", "xml");
                fileChooser.addChoosableFileFilter(f1); //add the file filter
                fileChooser.setFileFilter(f1); //set the current filter
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false); //can select only a file
                //Try to open the frontier directory
                print_log("Trying to open the default path: "+default_path);
                fileChooser.setCurrentDirectory(new File(default_path));
                int result = fileChooser.showOpenDialog(labelOldFile);
                //check if the result is the "approve" button
                if (result == JFileChooser.APPROVE_OPTION) { //if the user choose a file
                    File selectedFile = fileChooser.getSelectedFile(); //Take that file
                    print_log("Selected file: " + selectedFile.getAbsolutePath());
                    labelOldFile.setText(selectedFile.getName());
                    path_old_file =selectedFile.getAbsolutePath();
                    done_first_file =true;
                }
            }catch(Exception a){
                JOptionPane.showMessageDialog(null, "Error opening the file", "ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        }//button_pressed
    }//OldFilePath_class_end
    //---------------------------------------------------------------------------------------
    /**Listener for NEW file button chooser*/
    class NewFilePath implements ActionListener {
        /**
         * This listener open the file-chooser window to choose the new file to analyze
         * @param e the button that called the action
         */
        public void actionPerformed(ActionEvent e){
            try{
                JFileChooser fileChooser = new JFileChooser();
                javax.swing.filechooser.FileFilter f1 = new FileNameExtensionFilter("NEW Xml File", "xml"); //filters
                fileChooser.addChoosableFileFilter(f1);
                fileChooser.setFileFilter(f1);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                print_log("Trying to open the default path: "+default_path);
                fileChooser.setCurrentDirectory(new File(default_path));
                int result = fileChooser.showOpenDialog(labelNewFile);
                if (result == JFileChooser.APPROVE_OPTION) { //if the user choose a file
                    File selectedFile = fileChooser.getSelectedFile(); //Take that file
                    print_log("Selected file: " + selectedFile.getAbsolutePath());
                    labelNewFile.setText(selectedFile.getName());
                    path_new_file =selectedFile.getAbsolutePath();
                    done_second_file =true;
                }
            }catch(Exception a){
                JOptionPane.showMessageDialog(null, "Error opening the file", "ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        }//button_pressed
    }//NewFilePath_class_end
    //---------------------------------------------------------------------------------------
    /**
     * Function that enable/disable the buttons during files processing
     * @param active is the buttons should be active or not
     */
    private void toggleButtons(boolean active){
        elaborate.setEnabled(active);
        selectOldFile.setEnabled(active);
        selectNewFile.setEnabled(active);
        duplicates.setEnabled(active);
    }//toggle_buttons

    //---------------------------------------------------------------------------------------

    /**
     * Function that display an error box, called when an error occurs
     * @param errorMessage the error message to display in the box
     */
    private void displayError(String errorMessage){
        result.setText("<html><i>An error occurred</i></html>");
        JLabel error_label = new JLabel(errorMessage);
        error_label.setFont(new Font(font, Font.BOLD, fontSize+2));
        JOptionPane.showMessageDialog(new JFrame(),
                error_label,
                "An error occurred",
                JOptionPane.ERROR_MESSAGE);
    }
    //---------------------------------------------------------------------------------------
    /**Listener that merge the 2 files with a thread
     * (using PlanetCoasterWriter class)
     * */

    class Elaborate_Files implements ActionListener, Runnable{
        //Function called when the elaborate button is pressed
        public void actionPerformed(ActionEvent e) {
            if(done_first_file && done_second_file) { //only if both are true
                Thread t = new Thread(new Elaborate_Files()); //i can create the new file
                t.start(); //starting the process thread
            }else{ //need to select files first
                result.setText("Select Files First!");
                toggleButtons(true);
            }
        }//button_pressed

        /**
         * Thread that merge the two files and create the new file
         * The thread wait until PlanetCoasterWriter has finished (without blocking the program)
         */
        public void run() {
            toggleButtons(false);
            result.setText("Working...");
            try{
                try {
                    PlanetCoasterWriter w = new PlanetCoasterWriter(path_old_file, path_new_file);
                    while (!w.has_finished) { //wait for process to finish
                        Thread.sleep(30); //wait 30 milliseconds
                    }
                    //process ended
                    result.setText("Done!");
                }catch(Exception err){
                    print_log("Error:"+err);
                    displayError(err.toString());
                }
                toggleButtons(true);
            }catch (Exception err){ //something happened
                print_log("Error:"+err);
                displayError(err.toString());
                toggleButtons(true);
            }
        }//run_thread
    }//Elaborate_Class
    //---------------------------------------------------------------------------------------
    /**Listener that open a file, and check for keys duplicates, saving them in a TXT file
     * (using PlanetCoasterMerge class)
     * */

    class Find_Duplicates implements ActionListener, Runnable{
        //Function called when the duplicates button is pressed
        public void actionPerformed(ActionEvent e) {
            if(done_first_file) { //only if both are true
                Thread t = new Thread(new Find_Duplicates()); //i can create the new file
                t.start(); //starting the process thread
            }else{ //need to select files first
                result.setText("Select the File to find Duplicates!");
                toggleButtons(true);
            }
        }//button_pressed

        /**
         * Thread that read a planet coaster file and check for Keys duplicates
         * The thread wait until the file creation has ended
         */
        public void run() {
            boolean errors = false;
            toggleButtons(false);
            result.setText("Working...");
            try{
                PlanetCoasterMerge fileSelected = null;
                try {
                    if (done_first_file) {
                        //true for checking comments
                        fileSelected = new PlanetCoasterMerge(path_old_file, true);
                    }
                }catch(Exception err){ //Probably the xml file is not valid
                    errors=true;
                    fileSelected = null;
                    print_log("Error:"+err);
                    displayError(err.toString());
                }
                if(fileSelected != null) {
                    ArrayList<String> duplicatesKeys = new ArrayList<String>();
                    //For searching for duplicates
                    for (int i = 0; i < fileSelected.Keys.size(); i++) {
                        String compare_string = fileSelected.Keys.get(i);
                        if(compare_string.equals("Comment")){ //The string is a comment. i have to get it
                            compare_string = new String(fileSelected.utf8_values.get(i), "UTF-8");
                        }
                        for (int k = 0; k < fileSelected.Keys.size(); k++) {
                            //removed_values.add(new String(oldUTFTrans.get(0), "UTF-8"));
                            if(fileSelected.Keys.get(k).equals("Comment") && i != k){ //The string is a comment. i have to get it
                                //Getting the comment and comparing it
                                String comment_value = new String(fileSelected.utf8_values.get(k), "UTF-8");
                                if(comment_value.equals(compare_string) && !duplicatesKeys.contains(compare_string)){ //If the two comments are the same and the comment is not in the array
                                    duplicatesKeys.add(compare_string);
                                }
                            }else {
                                //If the index is different AND the two keys are the same AND is not already in the final array
                                if (i != k && compare_string.equals(fileSelected.Keys.get(k)) && !duplicatesKeys.contains(compare_string)) {
                                    duplicatesKeys.add(compare_string);
                                }
                            }
                        }//inside-for
                    }//big for
                    print_log("Found " + duplicatesKeys.size() + " duplicates!");
                    try{ //print all the lost strings
                        if(duplicatesKeys.size()>0) {
                            PrintWriter writer = new PrintWriter("DuplicatesFound.txt", "UTF-8");
                            for (String duplicatesKey : duplicatesKeys) {
                                writer.println("\"" + duplicatesKey + "\""); //print string to file
                            }
                            writer.close();
                        }else{
                            print_log("\nSkipped creation of DuplicatesFound.txt, no string removed...");
                        }
                    } catch (Exception e) {
                        errors=true;
                        print_log("\nError creating the file:"+e.toString());
                    }
                }else {//if not null
                    if(!errors) {
                        errors = true;
                        print_log("Error - File merge was null? Check log");
                        displayError("Error - File merge was null? Check log");
                    }
                }
                print_log("Duplicates search ended");
                //process ended
                if(!errors)
                {
                    result.setText("Done!");
                }
                toggleButtons(true);
                System.out.println("---------------------------");
            }catch (Exception err){ //something happened
                print_log("Error:"+err);
                displayError(err.toString());
                toggleButtons(true);
            }
        }//run_thread
    }//Duplicates_Class
    //---------------------------------------------------------------------------------------
    /**Function to pretty print the log with time
     @param s String to print*/
    private void print_log(String s){
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        System.out.println(ft.format(date)+"-->"+s);
    }//Stampa
    //---------------------------------------------------------------------------------------

    public static void main(String[] args){
        new Window();
    }

    //---------------------------------------------------------------------------------------
}
