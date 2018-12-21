package PlanetCoasterXML;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlanetCoasterMergeTest {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("---PlanetCoasterMergeTest begin---");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("---PlanetCoasterMergeTest end---");
    }

    //------------------------------------------------------------------
    //STANDARD MERGE TEST
    //------------------------------------------------------------------

    @Test
    public void ExecuteMerge_OldFileWithNewFile() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", false);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File.xml - getRemovedKeys size not correct!");
        assertEquals(9, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File.xml - getFinalFile size not correct!");
    }

    @Test
    public void ExecuteMerge_OldFileWithNewFile_BuildDirectly() throws PlanetCoasterReaderException {
        PlanetCoasterMerge merge = new PlanetCoasterMerge("Example Files/Old_File.xml", "Example Files/New_File.xml");
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File.xml - getRemovedKeys size not correct!");
        assertEquals(12, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File.xml - getFinalFile size not correct!");
    }

    @Test
    public void ExecuteMerge_OldFileWithNewFile_WithComments() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File.xml - getRemovedKeys size not correct!");
        assertEquals(12, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File.xml - getFinalFile size not correct!");
    }

    @Test
    public void ExecuteMerge_OldFileWithNewFile_WithRemovedKeys() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File_WithRemovedKeys.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(2, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File_WithRemovedKeys.xml - getRemovedKeys size not correct!");
        assertEquals(10, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File_WithRemovedKeys.xml - getFinalFile size not correct!");
    }

    @Test
    public void ExecuteMerge_OldFileWithNewFile_WithDuplicates() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && New_File_WithDuplicates.xml - getRemovedKeys size not correct!");
        assertEquals(15, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && New_File_WithDuplicates.xml - getFinalFile size not correct!");
    }

    @Test
    public void ExecuteMerge_OldFileWithNewFile_ClearedFromDuplicates() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader(PlanetCoasterDuplicates.clear_from_duplicates(new PlanetCoasterReader("Example Files/New_File_WithDuplicates.xml", true).getLoadedFileMultimap()));
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(0, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && cleared New_File_WithDuplicates.xml - getRemovedKeys size not correct!");
        assertEquals(12, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && cleared New_File_WithDuplicates.xml - getFinalFile size not correct!");
    }

    @Test
    public void ExecuteMerge_OldFileWitOriginalFile() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/OriginalPlanetCoasterStringData.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        assertEquals(6, merge.getRemovedKeys().size(), "PlanetCoasterMergeTest Error 1 from Old_File.xml && cleared New_File_WithDuplicates.xml - getRemovedKeys size not correct!");
        assertEquals(24100, merge.getFinalFile().getLoadedFileMultimap().size(), "PlanetCoasterMergeTest Error 2 from Old_File.xml && cleared New_File_WithDuplicates.xml - getFinalFile size not correct!");
    }

    //------------------------------------------------------------------
    //EXTRACT VALUES AFTER MERGE TESTS
    //------------------------------------------------------------------

    @Test
    public void ExecuteMerge_OldFileWithNewFile_ExtractValues() throws PlanetCoasterReaderException {
        PlanetCoasterReader reader1 = new PlanetCoasterReader("Example Files/Old_File.xml", false);
        PlanetCoasterReader reader2 = new PlanetCoasterReader("Example Files/New_File.xml", true);
        PlanetCoasterMerge merge = new PlanetCoasterMerge(reader1, reader2);
        ArrayList<byte[]> result = merge.getFinalFile().extractValues();
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
    }
}