package keystrokesmod.module.impl.client;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;

public class Settings extends Module {
    public static ButtonSetting sendMessage;

    public Settings() {
        super("Settings", category.client);
        sendMessage = new ButtonSetting("Send messages", false);
        registerSetting(sendMessage);
    }
}
