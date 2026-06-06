package keystrokesmod.utility.profile;
import keystrokesmod.module.Module;
import keystrokesmod.Raven;
import java.io.File;

public class Manager extends Module {
    private File configDir;
    
    public Manager() {
        super("ProfileManager");
        configDir = new File(Raven.mc.runDirectory, "raven-nextgen");
        if (!configDir.exists()) configDir.mkdirs();
    }
    
    public File getConfigDir() { return configDir; }
}
