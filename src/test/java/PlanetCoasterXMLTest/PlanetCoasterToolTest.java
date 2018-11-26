package PlanetCoasterXMLTest;

import PlanetCoasterXML.PlanetCoasterDuplicates;
import PlanetCoasterXML.PlanetCoasterMerge;
import PlanetCoasterXML.PlanetCoasterReader;
import PlanetCoasterXML.PlanetCoasterReaderException;
import PlanetCoasterXML.PlanetCoasterWriter;
import PlanetCoasterXML.PlanetCoasterWriterException;
import com.google.common.collect.LinkedListMultimap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlanetCoasterToolTest {

    public static int countLines(String filename) throws java.io.IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

    @BeforeAll
    public static void beforeTest(){
        System.out.println("---BEFORE ALL TESTS---");
        //empty here
    }

    @AfterAll
    public static void diskClear() {
        System.out.println("---AFTER ALL TESTS---");
        //Delete all the testing files from disk
        String[] fileToDelete = {"comments.txt", "Final.xml", "keys.txt", "multimap1.txt", "multimap2.txt", "StringLoss.txt"};
        for(String file : fileToDelete){
            try {
                Files.deleteIfExists(FileSystems.getDefault().getPath(file));
            } catch (IOException x) {
                // File permission problems are caught here.
                System.err.println(x);
            }
        }
        System.out.println("---End of the disk cleaning from test files---");
    }

    @Test
    public void PlanetCoasterReaderTest() throws PlanetCoasterReaderException {
        System.out.println("---PlanetCoasterReaderTest begin---");

        PlanetCoasterReader reader;

        reader = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        assertEquals(6, reader.getLoadedFileMultimap().size(), "PlanetCoasterReaderTest Error 1 from Old_File.xml - loaded_file_multimap size not correct!");
        assertEquals(6, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from Old_File.xml - extractKeys size not correct!");
        assertEquals(0, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from Old_File.xml - extractComments size not correct!");
        assertEquals(6, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from Old_File.xml - extractValues size not correct!");

        reader = new PlanetCoasterReader("Example Files/New_File.xml", true);
        assertEquals(12, reader.getLoadedFileMultimap().size(), "PlanetCoasterReaderTest Error 1 from New_File.xml - loaded_file_multimap size not correct!");
        assertEquals(12, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from New_File.xml - extractKeys size not correct!");
        assertEquals(3, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from New_File.xml - extractComments size not correct!");
        assertEquals(12, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from New_File.xml - extractValues size not correct!");

        reader = new PlanetCoasterReader("Example Files/Old_File_WithDuplicates.xml", false);
        assertEquals(8, reader.getLoadedFileMultimap().size(), "PlanetCoasterReaderTest Error 1 from Old_File_WithDuplicates.xml - loaded_file_multimap size not correct!");
        assertEquals(8, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from Old_File_WithDuplicates.xml - extractKeys size not correct!");
        assertEquals(0, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from Old_File_WithDuplicates.xml - extractComments size not correct!");
        assertEquals(8, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from Old_File_WithDuplicates.xml - extractValues size not correct!");

        reader = new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true);
        assertEquals(15, reader.getLoadedFileMultimap().size(), "PlanetCoasterReaderTest Error 1 from New_File_WithDuplicates.xml - loaded_file_multimap size not correct!");
        assertEquals(15, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from New_File_WithDuplicates.xml - extractKeys size not correct!");
        assertEquals(3, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from New_File_WithDuplicates.xml - extractComments size not correct!");
        assertEquals(15, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from New_File_WithDuplicates.xml - extractValues size not correct!");

        try {
            new PlanetCoasterReader("Example Files/BadXMLFile.xml", false);
        } catch (PlanetCoasterReaderException err) {
            System.out.println("Error received for BadXMLFile:" + err);
        }
        try {
            new PlanetCoasterReader("Example Files/NoARealFile.xml", false);
        } catch (PlanetCoasterReaderException err) {
            System.out.println("Error received for BadXMLFile:" + err);
        }

        System.out.println("---PlanetCoasterReaderTest end---");
    }

    @Test
    public void PlanetCoasterDuplicatesTest() throws PlanetCoasterReaderException {
        System.out.println("---PlanetCoasterDuplicatesTest begin---");

        PlanetCoasterReader reader;
        PlanetCoasterDuplicates duplicates;

        reader = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        duplicates = new PlanetCoasterDuplicates(reader);
        assertFalse(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from Old_File.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(0, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from Old_File.xml - getNumberDuplicates_found not correct!");
        assertEquals(0, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from Old_File.xml - getDuplicatesKeys size not correct!");

        duplicates = new PlanetCoasterDuplicates("Example Files/Old_File.xml");
        assertFalse(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from Old_File.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(0, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from Old_File.xml - getNumberDuplicates_found not correct!");
        assertEquals(0, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from Old_File.xml - getDuplicatesKeys size not correct!");

        reader = new PlanetCoasterReader("Example Files/Old_File_WithDuplicates.xml", false);
        duplicates = new PlanetCoasterDuplicates(reader);
        assertTrue(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from Old_File_WithDuplicates.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(2, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from Old_File_WithDuplicates.xml - getNumberDuplicates_found not correct!");
        assertEquals(2, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from Old_File_WithDuplicates.xml - getDuplicatesKeys size not correct!");

        reader = new PlanetCoasterReader("Example Files/New_File.xml", true);
        duplicates = new PlanetCoasterDuplicates(reader);
        assertFalse(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from New_File.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(0, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from New_File.xml - getNumberDuplicates_found not correct!");
        assertEquals(0, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from New_File.xml - getDuplicatesKeys size not correct!");

        reader = new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true);
        duplicates = new PlanetCoasterDuplicates(reader);
        assertTrue(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from New_File_WithDuplicates.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(3, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from New_File_WithDuplicates.xml - getNumberDuplicates_found not correct!");
        assertEquals(3, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from New_File_WithDuplicates.xml - getDuplicatesKeys size not correct!");

        reader = new PlanetCoasterReader(PlanetCoasterDuplicates.clear_from_duplicates(reader.getLoadedFileMultimap()));
        duplicates = new PlanetCoasterDuplicates(reader);
        assertFalse(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from cleared New_File_WithDuplicates.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(0, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from cleared New_File_WithDuplicates.xml - getNumberDuplicates_found not correct!");
        assertEquals(0, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from cleared New_File_WithDuplicates.xml - getDuplicatesKeys size not correct!");

        System.out.println("---PlanetCoasterDuplicatesTest end---");
    }

    @Test
    public void PlanetCoasterMergeTest() throws PlanetCoasterReaderException {
        System.out.println("---PlanetCoasterMergeTest begin---");

        PlanetCoasterReader reader1, reader2;
        PlanetCoasterMerge merge;

        reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        reader2 = new PlanetCoasterReader("Example Files/New_File.xml", false);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File.xml - getRemovedKeys size not correct!");
        assertEquals(9, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File.xml - getFinalFile size not correct!");

        merge = new PlanetCoasterMerge("Example Files/Old_File.xml", "Example Files/New_File.xml");
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File.xml - getRemovedKeys size not correct!");
        assertEquals(12, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File.xml - getFinalFile size not correct!");

        reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File.xml - getRemovedKeys size not correct!");
        assertEquals(12, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File.xml - getFinalFile size not correct!");

        reader2 = new PlanetCoasterReader("Example Files/New_File_WithRemovedKeys.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(2, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File_WithRemovedKeys.xml - getRemovedKeys size not correct!");
        assertEquals(10, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File_WithRemovedKeys.xml - getFinalFile size not correct!");

        reader2 = new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File_WithDuplicates.xml - getRemovedKeys size not correct!");
        assertEquals(15, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File_WithDuplicates.xml - getFinalFile size not correct!");

        reader2 = new PlanetCoasterReader(PlanetCoasterDuplicates.clear_from_duplicates(reader2.getLoadedFileMultimap()));
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && cleared New_File_WithDuplicates.xml - getRemovedKeys size not correct!");
        assertEquals(12, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && cleared New_File_WithDuplicates.xml - getFinalFile size not correct!");

        reader2 = new PlanetCoasterReader("Example Files/OriginalPlanetCoasterStringData.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(6, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && cleared New_File_WithDuplicates.xml - getRemovedKeys size not correct!");
        assertEquals(24100, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && cleared New_File_WithDuplicates.xml - getFinalFile size not correct!");

        System.out.println("---PlanetCoasterMergeTest end---");
    }

    @Test
    public void PlanetCoasterExtractTest() throws PlanetCoasterReaderException {
        System.out.println("---PlanetCoasterExtractTest begin---");

        PlanetCoasterReader reader1, reader2;
        PlanetCoasterMerge merge;

        reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        ArrayList<byte[]> result = reader1.extractValues();
        assertEquals("Translation First Key - AAA", new String(result.get(0), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 1 from Old_File.xml - Extracted value not correct!");
        assertEquals("Translation Second Key - BBB", new String(result.get(1), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 2 from Old_File.xml - Extracted value not correct!");
        assertEquals("Translation Third Key - FFF", new String(result.get(5), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 3 from Old_File.xml - Extracted value not correct!");

        reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        result = reader2.extractValues();
        //Remember that with isfinalfile = true this include comments so i need to change the index
        assertEquals("Original First Key - A", new String(result.get(1), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 1 from New_File.xml - Extracted value not correct!");
        assertEquals("Original Second Key - B", new String(result.get(2), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 2 from New_File.xml - Extracted value not correct!");
        assertEquals("Original First Key - D", new String(result.get(5), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 3 from New_File.xml - Extracted value not correct!");
        assertEquals("Original Third Key - I", new String(result.get(11), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 4 from New_File.xml - Extracted value not correct!");

        merge = new PlanetCoasterMerge(reader1, reader2);
        result = merge.getFinalFile().extractValues();
        assertEquals("Translation First Key - AAA", new String(result.get(1), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 1 from merge - Extracted value not correct!");
        assertEquals("Translation Second Key - BBB", new String(result.get(2), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 2 from merge - Extracted value not correct!");
        assertEquals("Translation Third Key - CCC", new String(result.get(3), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 3 from merge - Extracted value not correct!");
        assertEquals("Original First Key - G", new String(result.get(9), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 4 from merge - Extracted value not correct!");
        assertEquals("Original Second Key - H", new String(result.get(10), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 5 from merge - Extracted value not correct!");
        assertEquals("Original Third Key - I", new String(result.get(11), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 6 from merge - Extracted value not correct!");

        result = merge.getFinalFile().extractComments();
        assertEquals("Section1", new String(result.get(0), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 1 from merge comments - Extracted value not correct!");
        assertEquals("Section2", new String(result.get(1), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 2 from merge comments - Extracted value not correct!");
        assertEquals("Section3", new String(result.get(2), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 3 from merge comments - Extracted value not correct!");

        System.out.println("---PlanetCoasterExtractTest end---");
    }

    @Test
    public void PlanetCoasterWriteTest() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        System.out.println("---PlanetCoasterWriteTest begin---");

        PlanetCoasterReader reader1, reader2;
        PlanetCoasterMerge merge;

        //Simple test with empty output
        PlanetCoasterWriter.write_string_array_to_file(new ArrayList<String>(), "emptyFile.txt");
        PlanetCoasterWriter.write_byte_array_to_file(new ArrayList<byte[]>(), "emptyFile.txt");
        PlanetCoasterWriter.write_multimap_to_file(LinkedListMultimap.<String, byte[]>create(), "emptyFile.txt");

        //Test of final xml file
        reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        reader2 = new PlanetCoasterReader("Example Files/New_File.xml", false);
        merge = new PlanetCoasterMerge(reader1, reader2);
        Document XMLDOC = PlanetCoasterWriter.generate_xml_output(merge.getFinalFile());
        PlanetCoasterWriter.write_xml_file(XMLDOC, "Final.xml");
        reader1 = new PlanetCoasterReader("Final.xml", true);
        assertEquals(reader2.getLoadedFileMultimap().size(), reader1.getLoadedFileMultimap().size(), "PlanetCoasterWriteTest Error 1 - The size of the file created is not correct");

        //Another test of final xml file
        reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        XMLDOC = PlanetCoasterWriter.generate_xml_output(merge.getFinalFile());
        PlanetCoasterWriter.write_xml_file(XMLDOC, "Final.xml");
        reader1 = new PlanetCoasterReader("Final.xml", true);
        assertEquals(reader2.getLoadedFileMultimap().size(), reader1.getLoadedFileMultimap().size(), "PlanetCoasterWriteTest Error 2 - The size of the file created is not correct");
        assertEquals(reader2.extractComments().size(), reader1.extractComments().size());

        //Test of write file on disk
        PlanetCoasterWriter.write_string_array_to_file(reader2.extractKeys(), "keys.txt");
        assertEquals(12, countLines("keys.txt"), "Number of lines in keys.txt not correct!");

        PlanetCoasterWriter.write_byte_array_to_file(reader2.extractComments(), "comments.txt");
        assertEquals(3, countLines("comments.txt"), "Number of lines in comments.txt not correct!");

        PlanetCoasterWriter.write_multimap_to_file(reader1.getLoadedFileMultimap(), "multimap1.txt");
        assertEquals(12, countLines("multimap1.txt"), "Number of lines in multimap1.txt not correct!");

        //Test avoiding strings
        HashSet<String> avoidString = new HashSet<>();
        avoidString.add("Section1_FirstKey_Element");
        avoidString.add("Section3_FirstKey_Element");
        PlanetCoasterWriter.write_multimap_to_file(reader1.getLoadedFileMultimap(), "multimap1.txt", avoidString, true);
        assertEquals(10, countLines("multimap1.txt"), "Number of lines in multimap1.txt not correct!");

        HashSet<String> avoid = new HashSet<>();
        avoid.add("XMLPARSER-Comment");
        PlanetCoasterWriter.write_multimap_to_file(reader1.getLoadedFileMultimap(), "multimap2.txt", avoid, false);
        assertEquals(9, countLines("multimap2.txt"), "Number of lines in multimap2.txt not correct!");

        //Test with string loss
        reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        reader2 = new PlanetCoasterReader("Example Files/New_File_WithRemovedKeys.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
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

        System.out.println("---PlanetCoasterWriteTest end---");
    }
}
