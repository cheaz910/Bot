import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;


public class Bot {
    private String NameOfUser = "";

    public void Start() throws FileNotFoundException {
        NameOfUser = GetName();


        DateFormat ff = new SimpleDateFormat("dd-MM-yy");
        Date currentDate = new Date(2018,12,2,16,9,30);
        final String dateString = ff.format(currentDate);
        Map<String, Map<String, ArrayList<String>>> log = new HashMap<>(); // тут нужно получить Map из файла
        log.put("Ilnur", new HashMap<String, ArrayList<String>>());
        log.get("Ilnur").put(dateString, new ArrayList<String>() {{
            add("CompleteTask");
        }});

        String testim = ConvertToJson(log);
        System.out.println(testim);
        System.out.println(ConvertToMap(testim));


        if (!log.containsKey(NameOfUser)) {
            log.put(NameOfUser, new HashMap());
        }


        /**
         * тут нужно загрузить словарь из файла(json) и поместить в переменную log
         * изменить метод CheckNameInFile полностью и проверять имя как ключ в log, иначе добавить
         *
         */



        System.out.println(dateString);
        try {
            System.out.println(ff.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(log);
        ConvertToJson(log);
        // JsonObject jObject = new JsonObject();
        System.out.println(GetGreeting(NameOfUser));
        String command = "";
        Scanner in = new Scanner(System.in);
        while (!"Пока".equals(command)) {
            command = in.nextLine();
            System.out.println("Command:" + command);
            if (command.startsWith("Добавь заметку ")) {
                AddNote(command.substring(15), log);
            }
            else if (command.startsWith("Покажи список дел ")) {
                String result = GetNotesForDay(command.substring(18), log);
                System.out.println(result);
            }
        }
        // Здесь нужно сохранять словарь в файл
    }

    private String GetNotesForDay(String command, Map<String, Map<String, ArrayList<String>>> log) {
        return log.get(NameOfUser).get(command).toString();
    }

    private void AddNote(String command, Map<String, Map<String, ArrayList<String>>> log) {
        final String[] info = command.split(" ");
        Map<String, ArrayList<String>> notes = log.get(NameOfUser);
        if (notes.containsKey(info[0]))
            notes.get(info[0]).add(info[1]);
        else
            notes.put(info[0], new ArrayList() {{add(info[1]);}});
    }

    private String ConvertToJson(Map<String, Map<String, ArrayList<String>>> log) {
        String jsonStr = new Gson().toJson(log, HashMap.class);
        return jsonStr;
    }

    private Map<String, Map<String, ArrayList<String>>> ConvertToMap(String log) {
        Map<String, Map<String, ArrayList<String>>> myMap = new Gson().fromJson(log, HashMap.class);
        return myMap;
    }


    public Boolean CheckNameInFile(String name, Map<String, Map<String, String>> log) throws FileNotFoundException {
        /** Работающее считывание файла
         * File file = new File("Names.txt");
        Scanner scanner = new Scanner(file);
        List<String> list=new ArrayList<>();
        while(scanner.hasNextLine()){
            String str = scanner.nextLine().replaceAll("\n|\r\n", " ");
            list.add(str);
        }
        System.out.println(list);
        scanner.close();
        return list.contains(name);*/
        return true;
    }

    public void AddNameToFile(String name) {
        try(FileWriter writer = new FileWriter("notes3.txt", false))
        {
            // запись всей строки
            String text = "Hello Gold!";
            writer.write(text);
            // запись по символам
            writer.append('\n');
            writer.append('E');

            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }

    public String GetName() {
        System.out.println("Your name?");
        String name;
        Scanner in = new Scanner(System.in);
        name = in.nextLine();
        return name;
    }

    public String GetGreeting(String name) {
        return String.format("Hi, %s!", name);
    }
}
