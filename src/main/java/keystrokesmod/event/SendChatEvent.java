package keystrokesmod.event;

public class SendChatEvent extends Event {
    private String message;
    
    public SendChatEvent(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}