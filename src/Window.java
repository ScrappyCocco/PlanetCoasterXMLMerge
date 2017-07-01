//IMPORTS
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
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
import java.text.SimpleDateFormat;
import java.util.Date;


public class Window extends JFrame {

    private JLabel labelOldFile;
    private JLabel labelNewFile;
    private JButton elaborate;

    private String path_old_file, path_new_file;
    private boolean done_first_file =false, done_second_file =false;
    private JLabel result;

    /**
     * Draw the main window with buttons and labels...
     */
    private Window(){
        Container background;
        JButton selectOldFile;
        JButton selectNewFile;

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
        labelOldFile=new JLabel("OLD File name");
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

        JPanel elaborate_panel = new JPanel();
        elaborate_panel.add(elaborate);
        JPanel wrapperPanel1 = new JPanel(new GridBagLayout());
        wrapperPanel1.add(elaborate_panel);
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
    /**Listener for old file button*/
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
                if (result == JFileChooser.APPROVE_OPTION) { //if the user choose a file
                    File selectedFile = fileChooser.getSelectedFile();
                    print_log("Selected file: " + selectedFile.getAbsolutePath());
                    labelOldFile.setText(selectedFile.getName());
                    path_old_file =selectedFile.getAbsolutePath();
                    done_first_file =true;
                }
            }catch(Exception a){
                JOptionPane.showMessageDialog(null, "Error opening the file", "ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    //---------------------------------------------------------------------------------------
    /**Listener for new file button*/
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
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    print_log("Selected file: " + selectedFile.getAbsolutePath());
                    labelNewFile.setText(selectedFile.getName());
                    path_new_file =selectedFile.getAbsolutePath();
                    done_second_file =true;
                }
            }catch(Exception a){
                JOptionPane.showMessageDialog(null, "Error opening the file", "ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    //---------------------------------------------------------------------------------------
    /**Listener that merge the 2 files with a thread*/

    class Elaborate_Files implements ActionListener, Runnable{
        public void actionPerformed(ActionEvent e) {
            if(done_first_file && done_second_file) { //only if both are true
                Thread t = new Thread(new Elaborate_Files()); //i can create the new file
                t.start(); //starting the process thread
            }else{ //need to select files first
                result.setText("Select Files First!");
                elaborate.setEnabled(true);
            }
        }

        public void run() {
            elaborate.setEnabled(false);
            result.setText("Working...");
            try{
                PlanetCoasterWriter w = new PlanetCoasterWriter(path_old_file, path_new_file);
                while(!w.has_finished){ //wait for process to finish
                    Thread.sleep(30); //wait 30 milliseconds
                }
                //process ended
                result.setText("Done!");
                elaborate.setEnabled(true);
            }catch (Exception err){ //something happened
                print_log("Error:"+err);
                result.setText("ERROR!<br/>"+err);
                elaborate.setEnabled(true);
            }
        }
    }//Class
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
