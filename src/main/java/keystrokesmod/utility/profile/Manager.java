package keystrokesmod.utility.profile;

import keystrokesmod.Raven;
import java.io.File;

public class Manager {
    private File configDir;

    public Manager() {
        configDir = new File(Raven.mc.runDirectory, "raven-nextgen");
        if (!configDir.exists()) configDir.mkdirs();
    }

    public File getConfigDir() { return configDir; }

    public void saveConfig() {
        // TODO: Save module states, settings, binds
    }

    public void loadConfig() {
        // TODO: Load saved config
    }

    public void saveProfile(String name) {
        // TODO: Save profile
    }

    public void loadProfile(String name) {
        // TODO: Load profile
    }
}
