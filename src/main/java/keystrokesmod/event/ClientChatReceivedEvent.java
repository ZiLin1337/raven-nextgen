package keystrokesmod.event;

import net.minecraft.text.Text;

public class ClientChatReceivedEvent extends Event {
    public Text message;

    public ClientChatReceivedEvent(Text message) {
        this.message = message;
    }
}
