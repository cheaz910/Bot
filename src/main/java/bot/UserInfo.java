package bot;

class UserInfo {
    String chatId;
    String previousCommand = "";
    Boolean isStarted = false;
    Integer stage = 0;
    String name;

    UserInfo(String chatId) {
        this.chatId = chatId;
    }
}
