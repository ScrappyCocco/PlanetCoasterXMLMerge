package PlanetCoasterXML;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlanetCoasterDuplicatesTest {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("---PlanetCoasterDuplicatesTest begin---");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("---PlanetCoasterDuplicatesTest end---");
    }

    @Test
    public void CheckDuplicates_FromOldXMLFile() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterDuplicates duplicates = new PlanetCoasterDuplicates(reader);
        assertFalse(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from Old_File.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(0, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from Old_File.xml - getNumberDuplicates_found not correct!");
        assertEquals(0, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from Old_File.xml - getDuplicatesKeys size not correct!");
    }

    @Test
    public void CheckDuplicates_BuildDirectlyFromOldXMLFile() throws PlanetCoasterReaderException {
        PlanetCoasterDuplicates duplicates = new PlanetCoasterDuplicates("Example Files/Old_File.xml");
        assertFalse(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from Old_File.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(0, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from Old_File.xml - getNumberDuplicates_found not correct!");
        assertEquals(0, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from Old_File.xml - getDuplicatesKeys size not correct!");
    }

    @Test
    public void CheckDuplicates_FromOldXMLFile_WithDuplicates() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/Old_File_WithDuplicates.xml", false);
        PlanetCoasterDuplicates duplicates = new PlanetCoasterDuplicates(reader);
        assertTrue(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from Old_File_WithDuplicates.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(2, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from Old_File_WithDuplicates.xml - getNumberDuplicates_found not correct!");
        assertEquals(2, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from Old_File_WithDuplicates.xml - getDuplicatesKeys size not correct!");
    }

    @Test
    public void CheckDuplicates_FromNewXMLFile() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/New_File.xml", true);
        PlanetCoasterDuplicates duplicates = new PlanetCoasterDuplicates(reader);
        assertFalse(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from New_File.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(0, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from New_File.xml - getNumberDuplicates_found not correct!");
        assertEquals(0, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from New_File.xml - getDuplicatesKeys size not correct!");
    }

    @Test
    public void CheckDuplicates_FromNewXMLFile_WithDuplicates() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true);
        PlanetCoasterDuplicates duplicates = new PlanetCoasterDuplicates(reader);
        assertTrue(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from New_File_WithDuplicates.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(3, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from New_File_WithDuplicates.xml - getNumberDuplicates_found not correct!");
        assertEquals(3, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from New_File_WithDuplicates.xml - getDuplicatesKeys size not correct!");
    }

    @Test
    public void CheckDuplicates_ClearFileFromDuplicates() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader = new PlanetCoasterReader(PlanetCoasterDuplicates.clear_from_duplicates(new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true).getLoadedFileMultimap()));
        PlanetCoasterDuplicates duplicates = new PlanetCoasterDuplicates(reader);
        assertFalse(duplicates.file_has_duplicates(), "PlanetCoasterDuplicatesTest Error 1 from cleared New_File_WithDuplicates.xml - getNumberDuplicates_found boolean not correct!");
        assertEquals(0, duplicates.getNumberDuplicates_found(), "PlanetCoasterDuplicatesTest Error 2 from cleared New_File_WithDuplicates.xml - getNumberDuplicates_found not correct!");
        assertEquals(0, duplicates.getDuplicatesKeys().size(), "PlanetCoasterDuplicatesTest Error 3 from cleared New_File_WithDuplicates.xml - getDuplicatesKeys size not correct!");
    }
}