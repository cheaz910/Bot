import java.io.*;
import java.text.DateFormat;
import java.util.*;

import com.github.cliftonlabs.json_simple.JsonObject;


public class Bot {
    private String NameOfUser = "";

    public void Start() throws FileNotFoundException {
        NameOfUser = GetName();
        Boolean t = CheckNameInFile(NameOfUser);
        System.out.println(t);
        JsonObject jObject = new JsonObject();
        System.out.println(GetGreeting(NameOfUser));
        String command = "";
        Scanner in = new Scanner(System.in);
        while (!"bye".equals(command)) {
            command = in.nextLine();
            System.out.println(command);
        }
    }


    public Boolean CheckNameInFile(String name) throws FileNotFoundException {
        File file = new File("Names.txt");
        Scanner scanner = new Scanner(file);
        List<String> list=new ArrayList<>();
        while(scanner.hasNextLine()){
            String str = scanner.nextLine().replaceAll("\n|\r\n", " ");
            list.add(str);
        }
        System.out.println(list);
        scanner.close();
        return list.contains(name);
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
