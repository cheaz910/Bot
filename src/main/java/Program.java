import java.io.FileNotFoundException;

/**
 * Asks user for a name, then greets the user.
 */
public class Program {
    public static void main(String[] args ) throws FileNotFoundException {
        Bot bot = new Bot();
        bot.Start();
    }
}