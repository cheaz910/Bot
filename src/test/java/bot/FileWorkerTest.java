package bot;

import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

public class FileWorkerTest {
    @Test
    public final void testFileWorker() {
        String nameOfFile = "testFileWorker.txt";
        File file = new File(nameOfFile);
        assertFalse(file.exists());
        FileWorker.WriteFile(nameOfFile,"1234567890");
        String result = FileWorker.ReadFile(nameOfFile);
        assertEquals("1234567890\n", result);
        file.delete();
    }
}