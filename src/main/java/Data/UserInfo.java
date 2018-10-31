package Data;

public class UserInfo {
    public String chatId;
    public String previousCommand = "";
    public Boolean isStarted = false;
    public Integer stage = 0;
    public String name;

    public UserInfo(String chatId) {
        this.chatId = chatId;
    }
}
