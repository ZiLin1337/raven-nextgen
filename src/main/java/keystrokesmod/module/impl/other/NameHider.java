package keystrokesmod.module.impl.other;
import keystrokesmod.module.Module;
public class NameHider extends Module {
    public NameHider() {
        super("NameHider", category.other);
    }
    public static String getFakeName(String text) {
        return text;
    }
}
