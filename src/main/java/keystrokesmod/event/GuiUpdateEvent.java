package keystrokesmod.event;

import net.minecraft.client.gui.screen.Screen;

public class GuiUpdateEvent extends Event {
    private Screen guiScreen;
    private boolean opened;
    public GuiUpdateEvent(Screen guiScreen, boolean opened) { this.guiScreen = guiScreen; this.opened = opened; }
    public Screen getScreen() { return guiScreen; }
    public boolean isOpened() { return opened; }
}
