package keystrokesmod.utility.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages profiles for saving/loading module configurations.
 * Replaces old 1.8.9 MinecraftForge + IMinecraftInstance approach with
 * a pure Fabric/MinecraftClient-based implementation.
 */
public class ProfileManager {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File PROFILES_DIR;

    static {
        File configDir = new File(mc.runDirectory, "config/raven");
        if (!configDir.exists()) configDir.mkdirs();
        PROFILES_DIR = new File(configDir, "profiles");
        if (!PROFILES_DIR.exists()) PROFILES_DIR.mkdirs();
    }

    private String currentProfile = "default";
    public java.util.List<Profile> profiles = new java.util.ArrayList<>();

    /**
     * Saves the current module configuration to the given profile name.
     */
    public void save(String profileName) {
        Map<String, Map<String, Object>> profileData = new LinkedHashMap<>();
        for (Module module : Raven.getModuleManager().getModules()) {
            Map<String, Object> settings = new LinkedHashMap<>();
            for (Setting setting : module.getSettings()) {
                if (setting instanceof SliderSetting slider) {
                    settings.put(setting.getName(), slider.getInput());
                } else if (setting instanceof ButtonSetting button) {
                    settings.put(setting.getName(), button.isToggled());
                } else if (setting instanceof ColorSetting color) {
                    settings.put(setting.getName(), color.getColor());
                }
            }
            settings.put("_enabled", module.isEnabled());
            settings.put("_hidden", module.isHidden());
            profileData.put(module.getName(), settings);
        }
        File file = new File(PROFILES_DIR, profileName + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(profileData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.currentProfile = profileName;
    }

    /**
     * Loads module configuration from the given profile name.
     */
    public void load(String profileName) {
        File file = new File(PROFILES_DIR, profileName + ".json");
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
            Map<String, Map<String, Object>> profileData = GSON.fromJson(reader, type);
            if (profileData == null) return;
            for (Module module : Raven.getModuleManager().getModules()) {
                Map<String, Object> settings = profileData.get(module.getName());
                if (settings == null) continue;
                for (Setting setting : module.getSettings()) {
                    Object value = settings.get(setting.getName());
                    if (value == null) continue;
                    if (setting instanceof SliderSetting slider) {
                        if (value instanceof Number number) slider.setValue(number.intValue());
                    } else if (setting instanceof ButtonSetting button) {
                        if (value instanceof Boolean bool) button.setEnabled(bool);
                    } else if (setting instanceof ColorSetting color) {
                        if (value instanceof Number number) color.setColor((number.intValue() >> 16) & 255, (number.intValue() >> 8) & 255, number.intValue() & 255, (number.intValue() >> 24) & 255);
                    }
                }
                Object enabled = settings.get("_enabled");
                if (enabled instanceof Boolean bool) {
                    if (bool) module.enable();
                    else module.disable();
                }
                Object hidden = settings.get("_hidden");
                if (hidden instanceof Boolean bool) {
                    module.setHidden(bool);
                }
            }
            this.currentProfile = profileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a profile file.
     */
    public void delete(String profileName) {
        File file = new File(PROFILES_DIR, profileName + ".json");
        if (file.exists()) file.delete();
    }

    /**
     * Lists all available profiles.
     */
    public String[] list() {
        String[] files = PROFILES_DIR.list((dir, name) -> name.endsWith(".json"));
        if (files == null) return new String[0];
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].replace(".json", "");
        }
        return names;
    }

    /**
     * Returns the current profile name.
     */
    public String getCurrentProfile() {
        return currentProfile;
    }

    /**
     * Sets the current profile name (without loading).
     */
    public void setCurrentProfile(String profileName) {
        this.currentProfile = profileName;
    }

    /**
     * Checks if a profile exists.
     */
    public boolean exists(String profileName) {
        return new File(PROFILES_DIR, profileName + ".json").exists();
    }

    /**
     * Imports a profile from an external JSON file.
     */
    public void importProfile(File sourceFile) throws IOException {
        String name = sourceFile.getName().replace(".json", "");
        File dest = new File(PROFILES_DIR, name + ".json");
        java.nio.file.Files.copy(sourceFile.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Exports current profile to a file.
     */
    public void exportProfile(String profileName, File destination) throws IOException {
        File source = new File(PROFILES_DIR, profileName + ".json");
        if (source.exists()) {
            java.nio.file.Files.copy(source.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }
    public void saveProfile(Profile profile) { if (profile != null) save(profile.getName()); }
    public boolean deleteProfile(String profileName) { delete(profileName); return true; }
    public void loadProfile(String profileName) { load(profileName); }
    public void loadProfiles() {}
    public boolean renameProfile(Profile profile, String name) { if (profile != null) profile.setName(name); return true; }
}
