package bot;

import static org.junit.Assert.*;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

public class BotTest {
    @Test
    public final void testGetLogForUser_NewUser() { // Новый пользователь, которого еще нет в логе
        Bot bot = new Bot();
        Map<String, Map<String, Log>> logAllUsers = new HashMap<>();
        assertEquals(0, logAllUsers.size());
        Map<String, Log> log = bot.GetLogForUser("Java", logAllUsers);
        assertEquals(1, logAllUsers.size());
        assertEquals(0, log.size());
    }

    @Test
    public final void testGetLogForUser_OldUser() { // Пользователь, который уже пользовался ботом
        Bot bot = new Bot();
        Map<String, Map<String, Log>> logAllUsers = new HashMap<>();
        logAllUsers.put("Java", new HashMap());
        logAllUsers.get("Java").put("<Date>", new Log("<note>", new Date(2018), new Date(2018)));
        assertEquals(1, logAllUsers.size());
        Map<String, Log> log = bot.GetLogForUser("Java", logAllUsers);
        assertEquals(1, logAllUsers.size());
        assertEquals(1, log.size());
    }

    @Test
    public final void testJsonConverts() { // Проверка перевода из json в map и наоборот
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(outContent);
        Bot bot = new Bot(outputStream);
        Map<String, Map<String, Log>> notes = new HashMap<>();
        notes.put("Ilnur", new HashMap<String, Log>());
        String command = "событие 15:30-02.09.2018 01:40";
        AddTask.doCommand(command, notes.get("Ilnur"), outputStream);
        assertEquals("Событие добавлено\n", outContent.toString());
        String json = bot.ConvertToJson(notes);
        Map<String, Map<String, Log>> secondNotes = bot.ConvertToMap(json);
        assertEquals(notes.get("Ilnur").keySet(), secondNotes.get("Ilnur").keySet());
        Log log = notes.get("Ilnur").get("15:30-02.09.2018");
        Log secondLog = secondNotes.get("Ilnur").get("15:30-02.09.2018");
        assertEquals(log.startDate, secondLog.startDate);
    }
}