package bot;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GetTaskTest {
    @Test
    public final void testGetNotes_Default() { // Стандартные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(outContent);
        Map<String, Log> tasks = new HashMap<>();
        String command = "событие 15:30-02.09.2018 01:00";
        AddTask.doCommand(command, tasks, outputStream);
        String secondCommand = "событие 10:30-02.11.2018 01:00";
        AddTask.doCommand(secondCommand, tasks, outputStream);
        assertEquals("Событие добавлено\nСобытие добавлено\n", outContent.toString());
        outContent.reset();
        GetTasks.doCommand("11.2018", tasks, "MM.yyyy", outputStream);
        assertEquals("Cобытие: событие Начало события: 10:30-02.11.2018 " +
                "Конец события: 11:30-02.11.2018 Выполнено: Нет\n", outContent.toString());
        outContent.reset();
        GetTasks.doCommand("02.09.2018", tasks, "dd.MM.yyyy", outputStream);
        assertEquals("Cобытие: событие Начало события: 15:30-02.09.2018 " +
                "Конец события: 16:30-02.09.2018 Выполнено: Нет\n", outContent.toString());
    }
}
