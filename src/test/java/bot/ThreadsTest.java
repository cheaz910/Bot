package bot;

import Commands.AddTaskGroup;
import Commands.CheckTask;
import Commands.GetTasks;
import Commands.RemoveTasks;
import Data.Log;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ThreadsTest {

    @Test
    public final void testCheckTask() {
        for (Integer i = 0; i < 10000; i++) {
            final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            final PrintStream outStream = new PrintStream(outContent);
            final ConcurrentHashMap<String, Log> tasks = new ConcurrentHashMap<>();
            tasks.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
            tasks.put("13:45-02.09.2018", new Log("событие", new Date(0), new Date(0)));
            Thread thread1 = new Thread() {
                public void run() {
                    CheckTask.doCommand("13:40-02.09.2018", tasks, outStream);
                    CheckTask.doCommand("13:45-02.09.2018", tasks, outStream);
                }};
            Thread thread2 = new Thread() {
                public void run() {
                    RemoveTasks.removeOneTask("13:40-02.09.2018", tasks, outStream);
                    RemoveTasks.removeOneTask("13:45-02.09.2018", tasks, outStream);
                }};

            thread1.start();
            thread2.start();

            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(0, tasks.size());
        }
    }

    @Test
    public final void testGetTasks() {
        for (Integer i = 0; i < 10000; i++) {
            final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            final ConcurrentHashMap<String, Log> tasks = new ConcurrentHashMap<>();
            tasks.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
            tasks.put("13:45-02.09.2018", new Log("событие", new Date(0), new Date(0)));
            final ArrayList<ArrayList<Log>> gotTasks = new ArrayList<>();
            Thread thread1 = new Thread() {
                public void run() {
                    tasks.put("13:44-02.09.2018", new Log("событие", new Date(0), new Date(0)));
                    tasks.put("13:46-02.09.2018", new Log("событие", new Date(0), new Date(0)));
                    tasks.put("13:47-02.09.2018", new Log("событие", new Date(0), new Date(0)));
                    tasks.put("13:48-02.09.2018", new Log("событие", new Date(0), new Date(0)));
                    tasks.put("13:49-02.09.2018", new Log("событие", new Date(0), new Date(0)));
                    gotTasks.add(GetTasks.getTasks("09.18", tasks, "MM.yyyy", new PrintStream(outContent)));
                }};
            Thread thread2 = new Thread() {
                public void run() {
                    RemoveTasks.removeOneTask("13:40-02.09.2018", tasks, new PrintStream(outContent));
                    RemoveTasks.removeOneTask("13:40-02.09.2018", tasks, new PrintStream(outContent));
                    RemoveTasks.removeOneTask("13:45-02.09.2018", tasks, new PrintStream(outContent));
                }};
            Thread thread3 = new Thread() {
                public void run() {
                    RemoveTasks.removeOneTask("13:40-02.09.2018", tasks, new PrintStream(outContent));
                    RemoveTasks.removeOneTask("13:40-02.09.2018", tasks, new PrintStream(outContent));
                    RemoveTasks.removeOneTask("13:45-02.09.2018", tasks, new PrintStream(outContent));
                }};
            thread1.start();
            thread2.start();
            thread3.start();

            try {
                thread1.join();
                thread2.join();
                thread3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(5, tasks.size());
        }
    }

    @Test
    public final void testAddTask() {
        for (Integer i = 0; i < 10000; i++) {
            final ByteArrayOutputStream outputFirstUser = new ByteArrayOutputStream();
            final ByteArrayOutputStream outputSecondUser = new ByteArrayOutputStream();
            final ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> logAllUsers = new ConcurrentHashMap<>();
            logAllUsers.put("user1", new ConcurrentHashMap<String, Log>());
            logAllUsers.put("user2", new ConcurrentHashMap<String, Log>());
            logAllUsers.put("user3", new ConcurrentHashMap<String, Log>());
            final ArrayList<ArrayList<Log>> gotTasks = new ArrayList<>();
            Thread thread1 = new Thread() {
                public void run() {
                    AddTaskGroup.doCommand("событие1 13:44-02.09.2018 01:30 user3", "user1",
                            logAllUsers,
                            new PrintStream(outputFirstUser));
                }};
            Thread thread2 = new Thread() {
                public void run() {
                    AddTaskGroup.doCommand("событие2 13:44-02.09.2018 01:30 user3", "user2",
                            logAllUsers,
                            new PrintStream(outputSecondUser));
                }};

            thread1.start();
            thread2.start();

            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log log = logAllUsers.get("user3").get("13:44-02.09.2018");
            Boolean result = (log.task.equals("событие1") || log.task.equals("событие2"));
            assertTrue(result);
            Boolean resultFirstUser = outputFirstUser.toString().endsWith("На это время уже запланировано событие\n");
            Boolean resultSecondUser = outputSecondUser.toString().endsWith("На это время уже запланировано событие\n");
            assertTrue(""+resultFirstUser + " " + resultSecondUser, (resultFirstUser || resultSecondUser) && (!(resultFirstUser && resultSecondUser)));
        }
    }
}
