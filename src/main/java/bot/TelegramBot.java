package bot;

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


class TelegramBot extends TelegramLongPollingBot {
    Bot bot;
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream outputStream = new PrintStream(outContent);
    PrintStream console = System.out;
    HashMap<String, UserInfo> usersInfo = new HashMap<>();
    private static final Map<String, String> responses = new HashMap<String, String> () {{
        put("+", "Введите через пробел событие, дату начала и продолжительность события. \n" +
                "Формат ввода: событие HH:mm-dd.MM.yyyy HH:mm, \n" +
                "              или событие HH:mm-dd.MM HH:mm, или событие HH:mm-dd HH:mm ");
        put("-", "Введите дату начала события. Формат ввода: HH:mm-dd.MM.yyyy, или HH:mm-dd.MM, или HH:mm-dd");
        put("перенести", "Введите сначала дату, с которой нужно перенести, " +
                "затем дату, на которую нужно перенести. " +
                "Формат ввода: HH:mm-dd.MM.yyyy HH:mm-dd.MM.yyyy");
        put("день", "Ведите интересующий вас день. Формат ввода: dd.MM.yyyy");
        put("месяц", "Введите интересующий вас месяц. Формат ввода: MM.yyyy");
        put("-день", "Введите день, события которого нужно удалить. Формат ввода: dd.MM.yyyy");
        put("-месяц", "Введите месяц, события которого нужно удалить. Формат ввода: MM.yyyy");
        put("-год", "Введите год, события которого нужно удалить. Формат ввода: yyyy");
        put("посетил", "Введите дату, события которое посетили.\n" +
                "Формат ввода: HH:mm-dd.MM.yyyy, или HH:mm-dd.MM, или HH:mm-dd");
        }};


    public TelegramBot() {
        this.bot = new Bot(outputStream);
        bot.Start();
        console.println("Бот загрузился");
    }



    synchronized void setButtons(SendMessage sendMessage) {
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

        // Вторая строчка клавиатуры
        //KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        //keyboardSecondRow.add(new KeyboardButton("справка"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        //keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    void ProcessCommand(String command, Update update) {
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

        Map<String, Log> log = bot.GetLogForUser(chatId, bot.logAllUsers);
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

        switch(command) {
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
                    outputStream.println(bot.GetGreeting(userInfo.name));
                }
                break;
            case "+":
                bot.AddNote(argument, log);
                break;
            case "-":
                bot.RemoveNote(argument, log);
                break;
            case "перенести":
                bot.TransferNote(argument.split(" "), log);
                break;
            case "день":
                ArrayList<Log> notesForDay = bot.GetNotes(argument, log, "dd.MM.yyyy");
                bot.DisplayListOfNotes(notesForDay);
                break;
            case "месяц":
                ArrayList<Log> notesForMonth = bot.GetNotes(argument, log, "MM.yyyy");
                bot.DisplayListOfNotes(notesForMonth);
                break;
            case "-день":
                bot.RemoveNotesOfDayMonthYear(argument, "dd.MM.yyyy", log);
                break;
            case "-месяц":
                bot.RemoveNotesOfDayMonthYear(argument, "MM.yyyy", log);
                break;
            case "-год":
                bot.RemoveNotesOfDayMonthYear(argument, "yyyy", log);
                break;
            case "посетил":
                bot.CheckNote(argument, log);
                break;
            case "спасибо":
                outputStream.println("пожалуйста");
                break;
            case "справка":
                outputStream.println(bot.GetHelp());
                break;
            case "сохранить":
                bot.SaveInfo();
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
        ProcessCommand(message, update);
        if (!outContent.toString().isEmpty())
            sendMsg(update.getMessage().getChatId().toString(), outContent.toString());
        outContent.reset();
    }

    /**
     * Метод для настройки сообщения и его отправки.
     * @param chatId id чата
     * @param text Строка, которую необходимо отправить в качестве сообщения.
     */
    public synchronized void sendMsg(String chatId, String text) {
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