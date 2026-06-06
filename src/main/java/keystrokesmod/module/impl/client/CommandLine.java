package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.event.SendChatEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;

public class CommandLine extends Module {
    private SliderSetting delay;

    public CommandLine() {
        super("CommandLine", category.client);
        this.registerSetting(delay = new SliderSetting("Command delay", 0, 0, 1000, 10));
    }
}
