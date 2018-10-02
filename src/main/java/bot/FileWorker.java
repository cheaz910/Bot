package bot;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


class FileWorker {
    static String ReadFile(String nameOfFile) {
        String result = "";
        try {
            result = new String(Files.readAllBytes(Paths.get(nameOfFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    static void WriteFile(String nameOfFile, String text) {
        try (FileWriter writer = new FileWriter(nameOfFile))
        {
            writer.write(text + "\n");
            writer.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}