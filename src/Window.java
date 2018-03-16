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
import java.text.SimpleDateFormat;
import java.util.Date;

//CLASS

/**
 * This class create the main window with the buttons to choose the 2 files and to create the new file
 * This class has a listener for each button, plus a thread for the processing.
 */
public class Window extends JFrame {

    private final JLabel labelOldFile, labelNewFile, result;
    private final JButton elaborate, selectOldFile, selectNewFile, duplicates;

    private String path_old_file, path_new_file;
    private boolean done_first_file = false, done_second_file = false;

    //default windows translation location
    private final String default_path = "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Local\\Frontier Developments\\Planet Coaster\\Translations";
    private final String version = "1.15";

    private final String font = "Verdana";
    private final int fontSize = 14;

    /**
     * Draw the main window with buttons and labels...
     * (this constructor is not an important part of the program, it just create the UI)
     */
    private Window() {
        Container background;

        print_log("Executing Window()");
        print_log("Creating the Window...!");
        setSize(500, 500);
        setTitle("PlanetCoaster Translation Manager - v" + version);
        background = this.getContentPane();
        //----------------------------------------------------------------
        JPanel total_panel = new JPanel();
        total_panel.setLayout(new GridLayout(3, 1));
        //----------------------------------------------------------------
        try { //Set system UI
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            print_log("Error:" + ex);
        }
        //old file - panel and buttons
        labelOldFile = new JLabel("<html>OLD File name <br/> (Or File to check duplicates)</html>");
        labelOldFile.setFont(new Font(font, Font.PLAIN, fontSize));

        selectOldFile = new JButton("Select Old File");
        selectOldFile.setFont(new Font(font, Font.PLAIN, fontSize));
        selectOldFile.addActionListener(new XMLFilePath(true));
        JPanel oldL = new JPanel();
        oldL.add(labelOldFile);
        JPanel oldS = new JPanel();
        oldS.add(selectOldFile);

        JPanel old_total_panel = new JPanel();
        old_total_panel.setLayout(new GridLayout(2, 1));
        old_total_panel.add(oldL);
        old_total_panel.add(oldS);
        old_total_panel.setBorder(BorderFactory.createTitledBorder(null, "Old XML File", TitledBorder.CENTER, TitledBorder.TOP, new Font(font, Font.BOLD, fontSize), Color.black));
        total_panel.add(old_total_panel);
        //----------------------------------------------------------------
        //new file - panel and buttons
        labelNewFile = new JLabel("NEW File name");
        labelNewFile.setFont(new Font(font, Font.PLAIN, fontSize));
        selectNewFile = new JButton("Select New File");
        selectNewFile.setFont(new Font(font, Font.PLAIN, fontSize));
        selectNewFile.addActionListener(new XMLFilePath(false));
        JPanel newL = new JPanel();
        newL.add(labelNewFile);
        JPanel newS = new JPanel();
        newS.add(selectNewFile);

        JPanel new_total_panel = new JPanel();
        new_total_panel.setLayout(new GridLayout(2, 1));
        new_total_panel.add(newL);
        new_total_panel.add(newS);
        new_total_panel.setBorder(BorderFactory.createTitledBorder(null, "New XML File", TitledBorder.CENTER, TitledBorder.TOP, new Font(font, Font.BOLD, fontSize), Color.black));
        total_panel.add(new_total_panel);
        //----------------------------------------------------------------
        //elaborate file - panel and buttons
        elaborate = new JButton("Process Files");
        elaborate.addActionListener(new Elaborate_Files(this));
        elaborate.setFont(new Font(font, Font.PLAIN, fontSize));

        duplicates = new JButton("Check for Duplicates");
        duplicates.addActionListener(new Find_Duplicates(this));
        duplicates.setFont(new Font(font, Font.PLAIN, fontSize));

        JPanel buttons_execute_panel = new JPanel();
        buttons_execute_panel.setLayout(new GridLayout(2, 1));
        buttons_execute_panel.add(elaborate);
        buttons_execute_panel.add(duplicates);
        JPanel wrapperPanel1 = new JPanel(new GridBagLayout());
        wrapperPanel1.add(buttons_execute_panel);

        result = new JLabel("Ready");
        result.setFont(new Font(font, Font.PLAIN, fontSize));
        JPanel state = new JPanel();
        state.add(result);

        JPanel result_total_panel = new JPanel();
        result_total_panel.setLayout(new GridLayout(2, 1));
        result_total_panel.add(state);
        result_total_panel.add(wrapperPanel1);
        result_total_panel.setBorder(BorderFactory.createTitledBorder(null, "Execution and current state", TitledBorder.CENTER, TitledBorder.TOP, new Font(font, Font.BOLD, fontSize), Color.black));
        total_panel.add(result_total_panel);
        //----------------------------------------------------------------
        background.add(total_panel);
        print_log("Window Fully Created!");
        setResizable(false);
        setLocationRelativeTo(null);
        //Setting program icon
        try {
            setIconImage(ImageIO.read(new File("planet_icon.png")));
        } catch (IOException exc) {
            print_log("Icon \"planet_icon.png\" not found in execution dir... Skipping");
            exc.printStackTrace();
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //---------------------------------------------------------------------------------------

    /**
     * Listener for choose the XML file
     */
    class XMLFilePath implements ActionListener {
        boolean isFirstFile;

        XMLFilePath(boolean isFirst) {
            isFirstFile = isFirst;
        }

        /**
         * This listener open the file-chooser window to choose the file to analyze
         *
         * @param e the button that called the action
         */
        public void actionPerformed(ActionEvent e) {
            try {
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
                        labelOldFile.setText(selectedFile.getName());
                        path_old_file = selectedFile.getAbsolutePath();
                        done_first_file = true;
                    } else {
                        labelNewFile.setText(selectedFile.getName());
                        path_new_file = selectedFile.getAbsolutePath();
                        done_second_file = true;
                    }
                }
            } catch (Exception a) {
                JOptionPane.showMessageDialog(null, "Error opening the file", "ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        }//button_pressed
    }//XMLFilePath_class_end
    //---------------------------------------------------------------------------------------

    /**
     * Function that enable/disable the buttons during files processing
     *
     * @param active is the buttons should be active or not
     */
    private void toggleButtons(boolean active) {
        elaborate.setEnabled(active);
        selectOldFile.setEnabled(active);
        selectNewFile.setEnabled(active);
        duplicates.setEnabled(active);
    }//toggle_buttons

    //---------------------------------------------------------------------------------------

    /**
     * Function that display an error box, called when an error occurs
     *
     * @param errorMessage the error message to display in the box
     */
    private void displayError(String errorMessage) {
        result.setText("<html><i>An error occurred</i></html>");
        JLabel error_label = new JLabel(errorMessage);
        error_label.setFont(new Font(font, Font.BOLD, fontSize + 2));
        JOptionPane.showMessageDialog(new JFrame(),
                error_label,
                "An error occurred",
                JOptionPane.ERROR_MESSAGE);
    }
    //---------------------------------------------------------------------------------------

    /**
     * Listener that merge the 2 files with a thread
     * (using PlanetCoasterWriter class)
     */

    class Elaborate_Files implements ActionListener, Runnable {
        Window window_reference; //reference to main window to use print_log()

        Elaborate_Files(Window ref) {
            window_reference = ref;
        }

        //Function called when the elaborate button is pressed
        public void actionPerformed(ActionEvent e) {
            if (done_first_file && done_second_file) { //only if both are true
                Thread t = new Thread(new Elaborate_Files(window_reference)); //i can create the new file
                t.start(); //starting the process thread
            } else { //need to select files first
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
            try {
                try {
                    PlanetCoasterWriter w = new PlanetCoasterWriter(path_old_file, path_new_file, window_reference);
                    while (!w.has_finished) { //wait for process to finish
                        Thread.sleep(30); //wait 30 milliseconds
                    }
                    //process ended
                    result.setText("Done!");
                } catch (Exception err) {
                    print_log("Error:" + err);
                    displayError(err.toString());
                }
                toggleButtons(true);
            } catch (Exception err) { //something happened
                print_log("Error:" + err);
                displayError(err.toString());
                toggleButtons(true);
            }
        }//run_thread
    }//Elaborate_Class
    //---------------------------------------------------------------------------------------

    /**
     * Listener that open a file, and check for keys duplicates, saving them in a TXT file
     * (using PlanetCoasterReader class)
     */

    class Find_Duplicates implements ActionListener, Runnable {
        Window window_reference; //reference to main window to use print_log()

        Find_Duplicates(Window ref) {
            window_reference = ref;
        }

        //Function called when the duplicates button is pressed
        public void actionPerformed(ActionEvent e) {
            if (done_first_file) { //only if both are true
                Thread t = new Thread(new Find_Duplicates(window_reference)); //i can create the new file
                t.start(); //starting the process thread
            } else { //need to select files first
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
            try {
                try {
                    PlanetCoasterDuplicates w = new PlanetCoasterDuplicates(path_old_file, window_reference);
                    while (!w.has_finished) { //wait for process to finish
                        Thread.sleep(30); //wait 30 milliseconds
                    }
                    //process ended
                    result.setText("Done!");
                } catch (Exception err) {
                    print_log("Error:" + err);
                    displayError(err.toString());
                }
                toggleButtons(true);
            } catch (Exception err) { //something happened
                print_log("Error:" + err);
                displayError(err.toString());
                toggleButtons(true);
            }
        }//run_thread
    }//Duplicates_Class
    //---------------------------------------------------------------------------------------

    /**
     * Function to pretty print the log with time
     *
     * @param s String to print
     */
    protected void print_log(String s) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
        System.out.println(ft.format(date) + "-->" + s);
    }//Stampa
    //---------------------------------------------------------------------------------------

    /**
     * Main execution of the program
     *
     * @param args main args
     */
    public static void main(String[] args) {
        System.out.println("---Execution started---");
        new Window();
    }

    //---------------------------------------------------------------------------------------
}
