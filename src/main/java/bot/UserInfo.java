package bot;

public class UserInfo {
    String chatId;
    String previousCommand = "";
    Boolean isStarted = false;
    Integer stage = 0;
    String name;

    public UserInfo(String chatId) {
        this.chatId = chatId;
    }
}
