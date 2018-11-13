package bot;

import static org.junit.Assert.*;

import Commands.AddTask;
import Data.Log;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BotTest {
    @Test
    public final void testGetLogForUser_NewUser() { // Новый пользователь, которого еще нет в логе
        Bot bot = new Bot();
        ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> logAllUsers = new ConcurrentHashMap<>();
        assertEquals(0, logAllUsers.size());
        Map<String, Log> log = bot.getLogForUser("Java", logAllUsers);
        assertEquals(1, logAllUsers.size());
        assertEquals(0, log.size());
    }

    @Test
    public final void testGetLogForUser_OldUser() { // Пользователь, который уже пользовался ботом
        Bot bot = new Bot();
        ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> logAllUsers = new ConcurrentHashMap<>();
        logAllUsers.put("Java", new ConcurrentHashMap());
        logAllUsers.get("Java").put("<Date>", new Log("<note>", new Date(2018), new Date(2018)));
        assertEquals(1, logAllUsers.size());
        Map<String, Log> log = bot.getLogForUser("Java", logAllUsers);
        assertEquals(1, logAllUsers.size());
        assertEquals(1, log.size());
    }
}