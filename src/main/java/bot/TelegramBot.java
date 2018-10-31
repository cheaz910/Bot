package bot;

import Commands.*;
import Data.Log;
import Data.UserInfo;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.List;


class TelegramBot extends TelegramLongPollingBot {
    Bot bot;
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    public PrintStream outputStream = new PrintStream(outContent);
    private PrintStream console = System.out;
    private Map<String, UserInfo> usersInfo = new HashMap<>();
    private static final Map<String, String> responses = new HashMap<String, String> () {{
        put("+", AddTask.help());
        put("-", RemoveTasks.help(""));
        put("перенести", TransferTask.help());
        put("день", GetTasks.help("день"));
        put("месяц", GetTasks.help("месяц"));
        put("-день", RemoveTasks.help("день"));
        put("-месяц", RemoveTasks.help("месяц"));
        put("-год", RemoveTasks.help("год"));
        put("выполнено", CheckTask.help());
    }};

    TelegramBot() {
        this.bot = new Bot(outputStream);
        console.println("Бот загрузился");
    }

    private synchronized void setButtons(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("/start"));
        keyboardFirstRow.add(new KeyboardButton("/stop"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    synchronized void processCommand(String command, Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if (!usersInfo.containsKey(chatId)) {
            usersInfo.put(chatId, new UserInfo(chatId));
        }

        UserInfo userInfo = usersInfo.get(chatId);

        if (command.equals("/stop")) {
            userInfo.isStarted = false;
            userInfo.stage = 0;
            return;
        }

        if (!userInfo.isStarted && !command.equals("/start")) {
            outputStream.println("/start?");
            return;
        }

        Map<String, Log> log = bot.getLogForUser(chatId, bot.logAllUsers);
        command = command.toLowerCase().trim();
        String argument = command;

        if (userInfo.stage > 0) {
            command = userInfo.previousCommand;
        }

        if (responses.containsKey(command)) {
            if (userInfo.stage > 0)
                userInfo.stage -= 1;
            else {
                sendMsg(chatId, responses.get(command));
                userInfo.stage += 1;
                userInfo.previousCommand = command;
                return;
            }
        }

        switch(command  ) {
            case "/start":
                if (userInfo.stage == 0) {
                    userInfo.isStarted = true;
                    outputStream.println("Как вас зовут?");
                    userInfo.stage += 1;
                    userInfo.previousCommand = command;
                }
                else
                {
                    userInfo.name = argument;
                    userInfo.stage -= 1;
                    outputStream.println(String.format("Здравствуй, %s!", userInfo.name));
                    outputStream.println(bot.getHelp());
                }
                break;
            case "+":
                AddTask.doCommand(argument, log, outputStream);
                break;
            case "-":
                RemoveTasks.removeOneTask(argument, log, outputStream);
                break;
            case "перенести":
                TransferTask.doCommand(argument, log, outputStream);
                break;
            case "день":
                GetTasks.doCommand(argument, log, "dd.MM.yyyy", outputStream);
                break;
            case "месяц":
                GetTasks.doCommand(argument, log, "MM.yyyy", outputStream);
                break;
            case "-день":
                RemoveTasks.removeTasksOfDayMonthYear(argument, log, "dd.MM.yyyy", outputStream);
                break;
            case "-месяц":
                RemoveTasks.removeTasksOfDayMonthYear(argument, log, "MM.yyyy", outputStream);
                break;
            case "-год":
                RemoveTasks.removeTasksOfDayMonthYear(argument, log, "yyyy", outputStream);
                break;
            case "выполнено":
                CheckTask.doCommand(argument, log, outputStream);
                break;
            case "спасибо":
                outputStream.println("пожалуйста");
                break;
            case "справка":
                outputStream.println(bot.getHelp());
                break;
            case "сохранить":
                bot.saveInfo();
                break;
            default:
                outputStream.println("Неизвестная команда");
                break;
        }
    }

    /**
     * Метод для приема сообщений.
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        console.println(message);
        processCommand(message, update);
        if (!outContent.toString().isEmpty())
            sendMsg(update.getMessage().getChatId().toString(), outContent.toString());
        outContent.reset();
    }

    /**
     * Метод для настройки сообщения и его отправки.
     * @param chatId id чата
     * @param text Строка, которую необходимо отправить в качестве сообщения.
     */
    private synchronized void sendMsg(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        if (!usersInfo.get(chatId).isStarted) {
            setButtons(sendMessage);
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return "OOP345_bot";
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return "629617285:AAEloQlsAKS0m-ipOCLHdlSCdSSW1nX-Lso";
    }
}