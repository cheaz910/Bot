package bot;

import static org.junit.Assert.*;

import Commands.AddTask;
import Data.Log;
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
        Map<String, Log> log = bot.getLogForUser("Java", logAllUsers);
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
        Map<String, Log> log = bot.getLogForUser("Java", logAllUsers);
        assertEquals(1, logAllUsers.size());
        assertEquals(1, log.size());
    }
}