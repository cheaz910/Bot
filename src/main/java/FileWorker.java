import java.io.*;


class FileWorker {
    static String ReadFile(String nameOfFile) {
        StringBuilder result = new StringBuilder();
        try (FileReader reader = new FileReader(nameOfFile))
        {
            int c;
            while((c = reader.read()) != -1){
                result.append((char)c);
            }
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        return result.toString();
    }

    static void WriteFile(String nameOfFile, String text) {
        try (FileWriter writer = new FileWriter(nameOfFile, true))
        {
            writer.write(text + "\n");
            writer.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}