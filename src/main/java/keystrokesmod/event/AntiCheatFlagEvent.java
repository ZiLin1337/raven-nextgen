package keystrokesmod.event;

public class AntiCheatFlagEvent extends Event {
    private String flag;
    public AntiCheatFlagEvent(String flag) { this.flag = flag; }
    public String getFlag() { return flag; }
}
