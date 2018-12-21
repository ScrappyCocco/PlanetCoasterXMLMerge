package PlanetCoasterXML;

import com.google.common.collect.LinkedListMultimap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlanetCoasterWriterTest {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("---PlanetCoasterMergeTest begin---");
    }

    @AfterClass
    public static void afterClass() {
        diskClear();
        System.out.println("---PlanetCoasterMergeTest end---");
    }

    public static void diskClear() {
        System.out.println("---AFTER ALL TESTS---");
        //Delete all the testing files from disk
        String[] fileToDelete = {"comments.txt", "Final.xml", "keys.txt", "multimap1.txt", "multimap2.txt", "StringLoss.txt"};
        for (String file : fileToDelete) {
            try {
                Files.deleteIfExists(FileSystems.getDefault().getPath(file));
            } catch (IOException x) {
                // File permission problems are caught here.
                System.err.println(x);
            }
        }
        System.out.println("---End of the disk cleaning from test files---");
    }

    public static int countLines(String filename) throws java.io.IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

    //------------------------------------------------------------------
    //BEGIN TEST CODE
    //------------------------------------------------------------------

    @Test
    public void WriteFiles_WriteEmptyFiles() throws PlanetCoasterWriterException {
        PlanetCoasterWriter.write_string_array_to_file(new ArrayList<String>(), "emptyFile.txt");
        PlanetCoasterWriter.write_byte_array_to_file(new ArrayList<byte[]>(), "emptyFile.txt");
        PlanetCoasterWriter.write_multimap_to_file(LinkedListMultimap.<String, byte[]>create(), "emptyFile.txt");
    }

    @Test
    public void WriteFiles_WriteFinalXMLFile_AfterMerge() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", false);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        Document XMLDOC = PlanetCoasterWriter.generate_xml_output(merge.getFinalFile());
        PlanetCoasterWriter.write_xml_file(XMLDOC, "Final.xml");
        reader1 = new PlanetCoasterReader("Final.xml", true);
        assertEquals(reader2.getLoadedFileMultimap().size(), reader1.getLoadedFileMultimap().size(), "PlanetCoasterWriteTest Error 1 - The size of the file created is not correct");
    }

    @Test
    public void WriteFiles_WriteFinalXMLFile_Check_AfterMerge() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        Document XMLDOC = PlanetCoasterWriter.generate_xml_output(merge.getFinalFile());
        PlanetCoasterWriter.write_xml_file(XMLDOC, "Final.xml");
        reader1 = new PlanetCoasterReader("Final.xml", true);
        assertEquals(reader2.getLoadedFileMultimap().size(), reader1.getLoadedFileMultimap().size(), "PlanetCoasterWriteTest Error 2 - The size of the file created is not correct");
        assertEquals(reader2.extractComments().size(), reader1.extractComments().size());
    }

    @Test
    public void WriteFiles_WriteStringArrayToFile() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        PlanetCoasterWriter.write_string_array_to_file(reader2.extractKeys(), "keys.txt");
        assertEquals(12, countLines("keys.txt"), "Number of lines in keys.txt not correct!");
    }

    @Test
    public void WriteFiles_WriteByteArrayToFile() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        PlanetCoasterWriter.write_byte_array_to_file(reader2.extractComments(), "comments.txt");
        assertEquals(3, countLines("comments.txt"), "Number of lines in comments.txt not correct!");
    }

    @Test
    public void WriteFiles_WriteMultimapToFile() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        //Generate final xml file after merge
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        Document XMLDOC = PlanetCoasterWriter.generate_xml_output(merge.getFinalFile());
        PlanetCoasterWriter.write_xml_file(XMLDOC, "Final.xml");
        //Now test the code with the final xml file
        reader1 = new PlanetCoasterReader("Final.xml", true);
        PlanetCoasterWriter.write_multimap_to_file(reader1.getLoadedFileMultimap(), "multimap1.txt");
        assertEquals(12, countLines("multimap1.txt"), "Number of lines in multimap1.txt not correct!");
    }

    @Test
    public void WriteFiles_WriteMultimap_AvoidStrings() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        //Generate final xml file after merge
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        Document XMLDOC = PlanetCoasterWriter.generate_xml_output(merge.getFinalFile());
        PlanetCoasterWriter.write_xml_file(XMLDOC, "Final.xml");
        //Now test the code with the final xml file
        reader1 = new PlanetCoasterReader("Final.xml", true);
        HashSet<String> avoidString = new HashSet<>();
        avoidString.add("Section1_FirstKey_Element");
        avoidString.add("Section3_FirstKey_Element");
        PlanetCoasterWriter.write_multimap_to_file(reader1.getLoadedFileMultimap(), "multimap1.txt", avoidString, true);
        assertEquals(10, countLines("multimap1.txt"), "Number of lines in multimap1.txt not correct!");
    }

    @Test
    public void WriteFiles_WriteMultimap_AvoidComments() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        //Generate final xml file after merge
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        Document XMLDOC = PlanetCoasterWriter.generate_xml_output(merge.getFinalFile());
        PlanetCoasterWriter.write_xml_file(XMLDOC, "Final.xml");
        //Now test the code with the final xml file
        reader1 = new PlanetCoasterReader("Final.xml", true);
        HashSet<String> avoid = new HashSet<>();
        avoid.add("XMLPARSER-Comment");
        PlanetCoasterWriter.write_multimap_to_file(reader1.getLoadedFileMultimap(), "multimap2.txt", avoid, false);
        assertEquals(9, countLines("multimap2.txt"), "Number of lines in multimap2.txt not correct!");
    }

    @Test
    public void WriteFiles_WriteFinalXMLFile_AfterMerge_CheckStringLoss() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File_WithRemovedKeys.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        PlanetCoasterWriter.write_multimap_to_file(merge.getRemovedKeys(), "StringLoss.txt");
        //Check string loss file size
        assertEquals(2, countLines("StringLoss.txt"), "Number of lines in StringLoss.txt not correct!");
        LinkedListMultimap<String, byte[]> StringLossMultimap = merge.getRemovedKeys();
        //Check if contain key
        assertEquals(2, StringLossMultimap.keySet().size(), "Number of keys in the map not valid - 1");
        assertTrue(StringLossMultimap.containsKey("Section2_FirstKey_Element"), "PlanetCoasterWriteTest Error 1 StringLoss - The key is not in the multimap!");
        assertTrue(StringLossMultimap.containsKey("Section2_SecondKey_Element"), "PlanetCoasterWriteTest Error 2 StringLoss - The key is not in the multimap!");
        //Check value for each key
        assertEquals(1, StringLossMultimap.get("Section2_FirstKey_Element").size(), "Number of values for key not valid - 1");
        assertEquals(1, StringLossMultimap.get("Section2_SecondKey_Element").size(), "Number of values for key not valid - 2");
        assertEquals("Translation First Key - DDD", new String(StringLossMultimap.get("Section2_FirstKey_Element").get(0), StandardCharsets.UTF_8), "PlanetCoasterWriteTest Error 3 StringLoss - The value is not in the multimap!");
        assertEquals("Translation Second Key - EEE", new String(StringLossMultimap.get("Section2_SecondKey_Element").get(0), StandardCharsets.UTF_8), "PlanetCoasterWriteTest Error 4 StringLoss - The value is not in the multimap!");
    }
}