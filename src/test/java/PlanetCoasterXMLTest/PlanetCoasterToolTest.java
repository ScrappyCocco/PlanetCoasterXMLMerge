package PlanetCoasterXMLTest;

import PlanetCoasterXML.PlanetCoasterDuplicates;
import PlanetCoasterXML.PlanetCoasterMerge;
import PlanetCoasterXML.PlanetCoasterReader;
import PlanetCoasterXML.PlanetCoasterReaderException;
import PlanetCoasterXML.PlanetCoasterWriter;
import PlanetCoasterXML.PlanetCoasterWriterException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import java.io.BufferedReader;
import java.io.FileReader;
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

    @Test
    public void PlanetCoasterReaderTest() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader;

        reader = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        assertEquals(6, reader.loaded_file_multimap.size(), "PlanetCoasterReaderTest Error 1 from Old_File.xml - loaded_file_multimap size not correct!");
        assertEquals(6, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from Old_File.xml - extractKeys size not correct!");
        assertEquals(0, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from Old_File.xml - extractComments size not correct!");
        assertEquals(6, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from Old_File.xml - extractValues size not correct!");

        reader = new PlanetCoasterReader("Example Files/New_File.xml", true);
        assertEquals(12, reader.loaded_file_multimap.size(), "PlanetCoasterReaderTest Error 1 from New_File.xml - loaded_file_multimap size not correct!");
        assertEquals(12, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from New_File.xml - extractKeys size not correct!");
        assertEquals(3, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from New_File.xml - extractComments size not correct!");
        assertEquals(12, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from New_File.xml - extractValues size not correct!");

        reader = new PlanetCoasterReader("Example Files/Old_File_WithDuplicates.xml", false);
        assertEquals(8, reader.loaded_file_multimap.size(), "PlanetCoasterReaderTest Error 1 from Old_File_WithDuplicates.xml - loaded_file_multimap size not correct!");
        assertEquals(8, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from Old_File_WithDuplicates.xml - extractKeys size not correct!");
        assertEquals(0, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from Old_File_WithDuplicates.xml - extractComments size not correct!");
        assertEquals(8, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from Old_File_WithDuplicates.xml - extractValues size not correct!");

        reader = new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true);
        assertEquals(15, reader.loaded_file_multimap.size(), "PlanetCoasterReaderTest Error 1 from New_File_WithDuplicates.xml - loaded_file_multimap size not correct!");
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
    }

    @Test
    public void PlanetCoasterDuplicatesTest() throws PlanetCoasterReaderException, java.io.UnsupportedEncodingException {
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

        reader.loaded_file_multimap = PlanetCoasterDuplicates.clear_from_duplicates(reader.loaded_file_multimap);
        duplicates = new PlanetCoasterDuplicates(reader);
        assertFalse(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from cleared New_File_WithDuplicates.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(0, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from cleared New_File_WithDuplicates.xml - getNumberDuplicates_found not correct!");
        assertEquals(0, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from cleared New_File_WithDuplicates.xml - getDuplicatesKeys size not correct!");
    }

    @Test
    public void PlanetCoasterMergeTest() throws PlanetCoasterReaderException, java.io.UnsupportedEncodingException {
        PlanetCoasterReader reader1, reader2;
        PlanetCoasterMerge merge;

        reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        reader2 = new PlanetCoasterReader("Example Files/New_File.xml", false);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File.xml - getRemovedKeys size not correct!");
        assertEquals(9, merge.getFinalFile().loaded_file_multimap.size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File.xml - getFinalFile size not correct!");

        merge = new PlanetCoasterMerge("Example Files/Old_File.xml", "Example Files/New_File.xml");
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File.xml - getRemovedKeys size not correct!");
        assertEquals(12, merge.getFinalFile().loaded_file_multimap.size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File.xml - getFinalFile size not correct!");

        reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File.xml - getRemovedKeys size not correct!");
        assertEquals(12, merge.getFinalFile().loaded_file_multimap.size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File.xml - getFinalFile size not correct!");

        reader2 = new PlanetCoasterReader("Example Files/New_File_WithRemovedKeys.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(2, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File_WithRemovedKeys.xml - getRemovedKeys size not correct!");
        assertEquals(10, merge.getFinalFile().loaded_file_multimap.size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File_WithRemovedKeys.xml - getFinalFile size not correct!");

        reader2 = new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File_WithDuplicates.xml - getRemovedKeys size not correct!");
        assertEquals(15, merge.getFinalFile().loaded_file_multimap.size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File_WithDuplicates.xml - getFinalFile size not correct!");

        reader2.loaded_file_multimap = PlanetCoasterDuplicates.clear_from_duplicates(reader2.loaded_file_multimap);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && cleared New_File_WithDuplicates.xml - getRemovedKeys size not correct!");
        assertEquals(12, merge.getFinalFile().loaded_file_multimap.size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && cleared New_File_WithDuplicates.xml - getFinalFile size not correct!");

        reader2 = new PlanetCoasterReader("Example Files/OriginalPlanetCoasterStringData.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(6, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && cleared New_File_WithDuplicates.xml - getRemovedKeys size not correct!");
        assertEquals(24100, merge.getFinalFile().loaded_file_multimap.size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && cleared New_File_WithDuplicates.xml - getFinalFile size not correct!");

    }

    @Test
    public void PlanetCoasterWriteTest() throws PlanetCoasterReaderException, PlanetCoasterWriterException, java.io.IOException {
        PlanetCoasterReader reader1, reader2;
        PlanetCoasterMerge merge;

        reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        reader2 = new PlanetCoasterReader("Example Files/New_File.xml", false);
        merge = new PlanetCoasterMerge(reader1, reader2);
        Document XMLDOC = PlanetCoasterWriter.generate_xml_output(merge.getFinalFile());
        PlanetCoasterWriter.write_xml_file(XMLDOC, "Final.xml");
        reader1 = new PlanetCoasterReader("Final.xml", true);
        assertEquals(reader2.loaded_file_multimap.size(), reader1.loaded_file_multimap.size(), "PlanetCoasterWriteTest Error 1 - The size of the file created is not correct");

        reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        merge = new PlanetCoasterMerge(reader1, reader2);
        XMLDOC = PlanetCoasterWriter.generate_xml_output(merge.getFinalFile());
        PlanetCoasterWriter.write_xml_file(XMLDOC, "Final.xml");
        reader1 = new PlanetCoasterReader("Final.xml", true);
        assertEquals(reader2.loaded_file_multimap.size(), reader1.loaded_file_multimap.size(), "PlanetCoasterWriteTest Error 2 - The size of the file created is not correct");
        assertEquals(reader2.extractComments().size(), reader1.extractComments().size());

        PlanetCoasterWriter.write_string_array_to_file(reader2.extractKeys(), "keys.txt");
        assertEquals(12, countLines("keys.txt"), "Number of lines in keys.txt not correct!");

        PlanetCoasterWriter.write_byte_array_to_file(reader2.extractComments(), "comments.txt");
        assertEquals(3, countLines("comments.txt"), "Number of lines in comments.txt not correct!");

        PlanetCoasterWriter.write_multimap_to_file(reader1.loaded_file_multimap, "multimap1.txt");
        assertEquals(12, countLines("multimap1.txt"), "Number of lines in multimap1.txt not correct!");

        HashSet<String> avoid = new HashSet<>();
        avoid.add("XMLPARSER-Comment");
        PlanetCoasterWriter.write_multimap_to_file(reader1.loaded_file_multimap, "multimap2.txt", avoid, false);
        assertEquals(9, countLines("multimap2.txt"), "Number of lines in multimap2.txt not correct!");
    }
}
