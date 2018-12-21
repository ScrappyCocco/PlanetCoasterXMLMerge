package PlanetCoasterXML;

import com.google.common.collect.LinkedListMultimap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlanetCoasterReaderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        System.out.println("---PlanetCoasterReaderTest begin---");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("---PlanetCoasterReaderTest end---");
    }

    //------------------------------------------------------------------
    //BUILD READER TESTS
    //------------------------------------------------------------------

    @Test
    public void BuildObject_FromXMLFile_ConstructorTest() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        assertEquals(6, reader.getLoadedFileMultimap().size(), "PlanetCoasterReaderTest Error 1 from Old_File.xml - loaded_file_multimap size not correct!");
        assertEquals(6, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from Old_File.xml - extractKeys size not correct!");
        assertEquals(0, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from Old_File.xml - extractComments size not correct!");
        assertEquals(6, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from Old_File.xml - extractValues size not correct!");
    }

    @Test
    public void BuildObject_FromFinalXMLFile_ConstructorTest() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/New_File.xml", true);
        assertEquals(12, reader.getLoadedFileMultimap().size(), "PlanetCoasterReaderTest Error 1 from New_File.xml - loaded_file_multimap size not correct!");
        assertEquals(12, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from New_File.xml - extractKeys size not correct!");
        assertEquals(3, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from New_File.xml - extractComments size not correct!");
        assertEquals(12, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from New_File.xml - extractValues size not correct!");
    }

    @Test
    public void BuildObject_FromXMLFileWithDuplicates_ConstructorTest() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/Old_File_WithDuplicates.xml", false);
        assertEquals(8, reader.getLoadedFileMultimap().size(), "PlanetCoasterReaderTest Error 1 from Old_File_WithDuplicates.xml - loaded_file_multimap size not correct!");
        assertEquals(8, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from Old_File_WithDuplicates.xml - extractKeys size not correct!");
        assertEquals(0, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from Old_File_WithDuplicates.xml - extractComments size not correct!");
        assertEquals(8, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from Old_File_WithDuplicates.xml - extractValues size not correct!");
    }

    @Test
    public void BuildObject_FromFinalXMLFileWithDuplicates_ConstructorTest() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true);
        assertEquals(15, reader.getLoadedFileMultimap().size(), "PlanetCoasterReaderTest Error 1 from New_File_WithDuplicates.xml - loaded_file_multimap size not correct!");
        assertEquals(15, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from New_File_WithDuplicates.xml - extractKeys size not correct!");
        assertEquals(3, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from New_File_WithDuplicates.xml - extractComments size not correct!");
        assertEquals(15, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from New_File_WithDuplicates.xml - extractValues size not correct!");
    }

    @Test
    public void BuildObject_FromAnotherReader_ConstructorTest() throws PlanetCoasterReaderException {
        PlanetCoasterReader readerToCopy = new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true);
        PlanetCoasterReader reader = new PlanetCoasterReader(readerToCopy);
        assertEquals(15, reader.getLoadedFileMultimap().size(), "PlanetCoasterReaderTest Error 1 from New_File_WithDuplicates.xml - loaded_file_multimap size not correct!");
        assertEquals(15, reader.extractKeys().size(), "PlanetCoasterReaderTest Error 2 from New_File_WithDuplicates.xml - extractKeys size not correct!");
        assertEquals(3, reader.extractComments().size(), "PlanetCoasterReaderTest Error 3 from New_File_WithDuplicates.xml - extractComments size not correct!");
        assertEquals(15, reader.extractValues().size(), "PlanetCoasterReaderTest Error 4 from New_File_WithDuplicates.xml - extractValues size not correct!");
    }

    @Test
    public void BuildObject_FromBadXMLFile_ExceptionExpected() throws PlanetCoasterReaderException {
        thrown.expect(PlanetCoasterReaderException.class);
        new PlanetCoasterReader("Example Files/BadXMLFile.xml", false);
    }

    @Test
    public void BuildObject_FromNoFile_ExceptionExpected() throws PlanetCoasterReaderException {
        thrown.expect(PlanetCoasterReaderException.class);
        new PlanetCoasterReader("Example Files/NoARealFile.xml", false);
    }

    @Test
    public void BuildObject_FromNull_ExceptionExpected() throws PlanetCoasterReaderException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No file to open given...");
        new PlanetCoasterReader(null, false);
    }

    @Test
    public void BuildObject_FromEmptyList_ExceptionExpected() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No Multimap to copy given...");
        LinkedListMultimap<String, byte[]> emptyMultimap = LinkedListMultimap.create();
        new PlanetCoasterReader(emptyMultimap);
    }

    @Test
    public void BuildObject_FromMultimap_ConstructorTest() {
        LinkedListMultimap<String, byte[]> multimap = LinkedListMultimap.create();
        multimap.put("Key1", "Value1".getBytes());
        multimap.put("Key1", "Value2".getBytes());
        multimap.put("Key2", "Value3".getBytes());
        new PlanetCoasterReader(multimap);
    }

    //------------------------------------------------------------------
    //EXTRACT VALUES FROM READER TESTS
    //------------------------------------------------------------------

    @Test
    public void ExtractValues_FromOldXMLFile() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        ArrayList<byte[]> result = reader.extractValues();
        assertEquals("Translation First Key - AAA", new String(result.get(0), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 1 from Old_File.xml - Extracted value not correct!");
        assertEquals("Translation Second Key - BBB", new String(result.get(1), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 2 from Old_File.xml - Extracted value not correct!");
        assertEquals("Translation Third Key - FFF", new String(result.get(5), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 3 from Old_File.xml - Extracted value not correct!");

    }

    @Test
    public void ExtractValues_FromNewXMLFile() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/New_File.xml", true);
        ArrayList<byte[]> result = reader.extractValues();
        //Remember that with isfinalfile = true this include comments so i need to change the index
        assertEquals("Original First Key - A", new String(result.get(1), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 1 from New_File.xml - Extracted value not correct!");
        assertEquals("Original Second Key - B", new String(result.get(2), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 2 from New_File.xml - Extracted value not correct!");
        assertEquals("Original First Key - D", new String(result.get(5), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 3 from New_File.xml - Extracted value not correct!");
        assertEquals("Original Third Key - I", new String(result.get(11), StandardCharsets.UTF_8), "PlanetCoasterExtractTest Error 4 from New_File.xml - Extracted value not correct!");
    }
}