package bot;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransferTaskTest {
    @Test
    public final void testTransferNote_Default() {   // Стандартные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(outContent);
        Map<String, Log> notes = new HashMap<>();
        String command = "событие 13:40-02.09.2018 12:10";
        AddTask.doCommand(command, notes, outputStream);
        assertEquals("Событие добавлено\n", outContent.toString());
        String note = notes.get("13:40-02.09.2018").task;
        String secondCommand = "13:40-02.09.2018 15:25-03.10.2019";
        outContent.reset();
        TransferTask.doCommand(secondCommand, notes, outputStream);
        assertEquals("Событие перенесено\n", outContent.toString());
        assertTrue(notes.containsKey("15:25-03.10.2019"));
        assertEquals(1, notes.size());
        Log log = notes.get("15:25-03.10.2019");
        String pattern = "HH:mm-dd.MM.yyyy";
        assertEquals(DateWorker.getCorrectDate("15:25-03.10.2019", pattern), log.startDate);
        assertEquals(DateWorker.getCorrectDate("03:35-04.10.2019", pattern), log.endDate);
        assertEquals(note, log.task);
    }

    private boolean WrongFormatTransferNote(String secondCommand, ByteArrayOutputStream outContent) {
        PrintStream outputStream = new PrintStream(outContent);
        Map<String, Log> notes = new HashMap<>();
        String command = "событие 13:40-02.09.2018 12:10";
        AddTask.doCommand(command, notes, outputStream);
        assertEquals("Событие добавлено\n", outContent.toString());
        outContent.reset();
        TransferTask.doCommand(secondCommand, notes, outputStream);
        return notes.containsKey("13:40-02.09.2018");
    }

    @Test
    public final void testTransferNote_WrongFormat() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertTrue(WrongFormatTransferNote("13:40-02.09.2018", outContent));
        // На входе не указывается куда перенести
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatTransferNote("", outContent));
        // На входе пустая строка
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatTransferNote("11340---...-02.09.2018 15:40-03.09.2018", outContent));
        // Первая дата с ошибкой
        assertEquals("Неверный формат даты/времени. [11340---...-02.09.2018]\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatTransferNote("13:40-02.09.2018 11330--..32140.1", outContent));
        // Вторая дата с ошибкой
        assertEquals("Неверный формат даты/времени. [11330--..32140.1]\n", outContent.toString());
    }
}
