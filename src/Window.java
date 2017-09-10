//IMPORTS
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.Container;
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

    /**
     * Draw the main window with buttons and labels...
     * (this is not an important part of the program)
     */
    private Window(){
        Container background;

        print_log("Program started!");
        print_log("Creating the Window...!");
        setSize(500,500);
        setTitle("PlanetCoaster Translation");
        background=this.getContentPane();
        //----------------------------------------------------------------
        JPanel total_panel=new JPanel();
        total_panel.setLayout(new GridLayout(3,2));
        //----------------------------------------------------------------
        //old file - panel and buttons
        labelOldFile=new JLabel("<html>OLD File name <br/> (Or File to check duplicates)</html>");
        selectOldFile=new JButton("Select Old File");
        selectOldFile.addActionListener(new OldFilePath());
        JPanel oldL=new JPanel();
        oldL.add(labelOldFile);
        JPanel oldS=new JPanel();
        oldS.add(selectOldFile);
        oldL.setBorder(new TitledBorder("Old XML Filename"));
        oldS.setBorder(new TitledBorder("Old XML File Chooser"));
        total_panel.add(oldL);
        total_panel.add(oldS);
        //----------------------------------------------------------------
        //new file - panel and buttons
        labelNewFile=new JLabel("NEW File name");
        selectNewFile=new JButton("Select New File");
        selectNewFile.addActionListener(new NewFilePath());
        JPanel newL=new JPanel();
        newL.add(labelNewFile);
        JPanel newS=new JPanel();
        newS.add(selectNewFile);
        newL.setBorder(new TitledBorder("New XML Filename"));
        newS.setBorder(new TitledBorder("New XML File Chooser"));
        total_panel.add(newL);
        total_panel.add(newS);
        //----------------------------------------------------------------
        //elaborate file - panel and buttons
        elaborate=new JButton("Process Files");
        elaborate.addActionListener(new Elaborate_Files());

        duplicates = new JButton("Check for Duplicates");
        duplicates.addActionListener(new Find_Duplicates());

        JPanel buttons_execute_panel = new JPanel();
        buttons_execute_panel.setLayout(new GridLayout(2,1));
        buttons_execute_panel.add(elaborate);
        buttons_execute_panel.add(duplicates);
        JPanel wrapperPanel1 = new JPanel(new GridBagLayout());
        wrapperPanel1.add(buttons_execute_panel);
        wrapperPanel1.setBorder(BorderFactory.createLineBorder(Color.black));
        total_panel.add(wrapperPanel1);

        result=new JLabel("Ready");
        JPanel state = new JPanel();
        state.add(result);
        state.setBorder(new TitledBorder("Current State"));
        total_panel.add(state);
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
            print_log("Icon \"planet_icon.png\" not found in execution dir...");
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
                print_log("C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Local\\Frontier Developments\\Planet Coaster\\Translations");
                fileChooser.setCurrentDirectory(new File("C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Local\\Frontier Developments\\Planet Coaster\\Translations"));
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
                print_log("C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Local\\Frontier Developments\\Planet Coaster\\Translations");
                fileChooser.setCurrentDirectory(new File("C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Local\\Frontier Developments\\Planet Coaster\\Translations"));
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
    /**Listener that merge the 2 files with a thread
     * (using PlanetCoasterWriter class)
     * */

    class Elaborate_Files implements ActionListener, Runnable{

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
                PlanetCoasterWriter w = new PlanetCoasterWriter(path_old_file, path_new_file);
                while(!w.has_finished){ //wait for process to finish
                    Thread.sleep(30); //wait 30 milliseconds
                }
                //process ended
                result.setText("Done!");
                toggleButtons(true);
            }catch (Exception err){ //something happened
                print_log("Error:"+err);
                result.setText("ERROR!<br/>\n"+err);
                toggleButtons(true);
            }
        }//run_thread
    }//Elaborate_Class
    //---------------------------------------------------------------------------------------
    /**Listener that open a file, and check for keys duplicates, saving them in a TXT file
     * (using PlanetCoasterMerge class)
     * */

    class Find_Duplicates implements ActionListener, Runnable{

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
            toggleButtons(false);
            result.setText("Working...");
            try{
                PlanetCoasterMerge fileSelected = null;
                if(done_first_file) {
                    fileSelected = new PlanetCoasterMerge(path_old_file, false);
                }
                if(fileSelected != null) {
                    ArrayList<String> duplicatesKeys = new ArrayList<String>();
                    //For searching for duplicates
                    for (int i = 0; i < fileSelected.Keys.size(); i++) {
                        String compare_string = fileSelected.Keys.get(i);
                        for (int k = 0; k < fileSelected.Keys.size(); k++) {
                            //If the index is different AND the two keys are the same AND is not already in the final array
                            if(i!=k && compare_string.equals(fileSelected.Keys.get(k)) && !duplicatesKeys.contains(compare_string)){
                                duplicatesKeys.add(compare_string);
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
                            System.out.println("\nSkipped creation of DuplicatesFound.txt, no string removed...");
                        }
                    } catch (Exception e) {
                        System.out.println("\nError creating the file:"+e.toString());
                    }
                }else {//if not null
                    print_log("Error - File merge was null?");
                    result.setText("Error - File merge was null?");
                }
                print_log("Duplicates search ended");
                //process ended
                result.setText("Done!");
                toggleButtons(true);
                System.out.println("---------------------------");
            }catch (Exception err){ //something happened
                print_log("Error:"+err);
                result.setText("ERROR!<br/>\n"+err);
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
