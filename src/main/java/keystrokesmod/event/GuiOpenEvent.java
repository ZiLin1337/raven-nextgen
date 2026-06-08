package keystrokesmod.event;

import net.minecraft.client.gui.screen.Screen;

public class GuiOpenEvent extends Event {
    public Screen gui;

    public GuiOpenEvent(Screen gui) {
        this.gui = gui;
    }
}
