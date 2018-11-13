package bot;

import static org.junit.Assert.*;

import Commands.AddTask;
import Data.Log;
import org.junit.Test;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TelegramBotTest {
    Update GetUpdateById(Long id) {
        Update update = new Update();
        try {
            Field message_field = update.getClass().getDeclaredField("message");
            Message message = new Message();
            Chat chat = new Chat();
            Field id_field = chat.getClass().getDeclaredField("id");
            id_field.setAccessible(true);
            id_field.set(chat, id);
            Field chat_field = message.getClass().getDeclaredField("chat");
            chat_field.setAccessible(true);
            chat_field.set(message, chat);
            message_field.setAccessible(true);
            message_field.set(update, message);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return update;
    }

    @Test
    public final void testTelegramBot_TwoUsers() {
        final TelegramBot botapi = new TelegramBot();
        final Update update1 = GetUpdateById(1L);
        final Update update2 = GetUpdateById(2L);
        botapi.bot.logAllUsers = new ConcurrentHashMap<>();

        Thread thread1 = new Thread() {
            public void run() {
                botapi.processCommand("/start", update1);
                botapi.processCommand("123", update1);
                botapi.processCommand("+", update1);
                botapi.processCommand("aaaaaaa 16:11-05.05.2018 02:30", update1);
                botapi.processCommand("-", update1);
                botapi.processCommand("15:22-05.05.2018", update1);
            }
        };
        Thread thread2 = new Thread() {
            public void run() {
                botapi.processCommand("/start", update2);
                botapi.processCommand("123", update2);
                botapi.processCommand("+", update2);
                botapi.processCommand("bbbbbbb 15:22-05.05.2018 01:30", update2);
            }
        };

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, Log> logFirstUser = botapi.bot.logAllUsers.get("1");
        Map<String, Log> logSecondUser = botapi.bot.logAllUsers.get("2");
        assertEquals(2, botapi.bot.logAllUsers.size());
        assertEquals(1, logFirstUser.size());
        assertEquals(1, logSecondUser.size());
        assertEquals("aaaaaaa", logFirstUser.get("16:11-05.05.2018").task);
        assertEquals("bbbbbbb", logSecondUser.get("15:22-05.05.2018").task);
    }
}