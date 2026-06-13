package keystrokesmod.utility.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.*;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class ProfileManager {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File PROFILES_DIR;
    private static final char[] INVALID_PROFILE_NAME_CHARS = new char[]{'\\', '/', ':', '*', '?', '"', '<', '>', '|'};
    
    static {
        File configDir = new File(mc.runDirectory, "config/raven");
        if (!configDir.exists()) configDir.mkdirs();
        PROFILES_DIR = new File(configDir, "profiles");
        if (!PROFILES_DIR.exists()) PROFILES_DIR.mkdirs();
    }
    
    private String currentProfile = "default";
    public java.util.List<Profile> profiles = new java.util.ArrayList<>();
    
    public ProfileManager() {
        loadProfiles();
        if (profiles.isEmpty()) {
            createProfile("default", 0);
        }
    }
    
    /**
     * Save current configuration to profile
     */
    public void save(String profileName) {
        Map<String, Map<String, Object>> profileData = new LinkedHashMap<>();
        
        for (Module module : Raven.getModuleManager().getModules()) {
            Map<String, Object> settings = new LinkedHashMap<>();
            
            for (Setting setting : module.getSettings()) {
                saveSetting(settings, setting);
            }
            
            settings.put("_enabled", module.isEnabled());
            settings.put("_hidden", module.isHidden());
            settings.put("_keybind", module.getKeycode());
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
    
    private void saveSetting(Map<String, Object> settings, Setting setting) {
        String key = setting.getName();
        
        if (setting instanceof SliderSetting slider) {
            settings.put(key, slider.getInput());
        } else if (setting instanceof ButtonSetting button) {
            settings.put(key, button.isToggled());
        } else if (setting instanceof ColorSetting color) {
            settings.put(key, color.getColor());
        } else if (setting instanceof TextSetting text) {
            settings.put(key, text.getText());
        } else if (setting instanceof KeySetting keySetting) {
            settings.put(key, keySetting.getKeyCode());
        } else if (setting instanceof BlockListSetting blockList) {
            settings.put(key, new ArrayList<>(blockList.getEnabledBlocks()));
        } else if (setting instanceof ItemListSetting itemList) {
            settings.put(key, new ArrayList<>(itemList.getEnabledItems()));
        } else if (setting instanceof StringListSetting stringList) {
            settings.put(key, new ArrayList<>(stringList.getEnabledStrings()));
        } else if (setting instanceof PlayerListSetting playerList) {
            settings.put(key, new ArrayList<>(playerList.getEnabledPlayers()));
        } else if (setting instanceof PotionListSetting potionList) {
            settings.put(key, new ArrayList<>(potionList.getEnabledPotions()));
        } else if (setting instanceof InventoryItemListSetting invList) {
            settings.put(key, new ArrayList<>(invList.getEnabledItems()));
        }
    }
    
    /**
     * Load configuration from profile
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
                    loadSetting(settings, setting);
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
                
                Object keybind = settings.get("_keybind");
                if (keybind instanceof Number number) {
                    module.setBind(number.intValue());
                }
            }
            this.currentProfile = profileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadSetting(Map<String, Object> settings, Setting setting) {
        String key = setting.getName();
        Object value = settings.get(key);
        if (value == null) return;
        
        if (setting instanceof SliderSetting slider) {
            if (value instanceof Number number) slider.setValue(number.intValue());
        } else if (setting instanceof ButtonSetting button) {
            if (value instanceof Boolean bool) button.setEnabled(bool);
        } else if (setting instanceof ColorSetting color) {
            if (value instanceof Number number) {
                int c = number.intValue();
                color.setColor((c >> 16) & 255, (c >> 8) & 255, c & 255, (c >> 24) & 255);
            }
        } else if (setting instanceof TextSetting text) {
            if (value instanceof String str) text.setText(str);
        } else if (setting instanceof KeySetting keySetting) {
            if (value instanceof Number number) keySetting.setKeyCode(number.intValue());
        } else if (setting instanceof BlockListSetting blockList) {
            if (value instanceof List<?> list) {
                blockList.clearBlocks();
                for (Object item : list) {
                    if (item instanceof String str) blockList.addBlock(str);
                }
            }
        } else if (setting instanceof ItemListSetting itemList) {
            if (value instanceof List<?> list) {
                itemList.clearItems();
                for (Object item : list) {
                    if (item instanceof String str) itemList.addItem(str);
                }
            }
        } else if (setting instanceof StringListSetting stringList) {
            if (value instanceof List<?> list) {
                stringList.clearStrings();
                for (Object item : list) {
                    if (item instanceof String str) stringList.addString(str);
                }
            }
        } else if (setting instanceof PlayerListSetting playerList) {
            if (value instanceof List<?> list) {
                playerList.clearPlayers();
                for (Object item : list) {
                    if (item instanceof String str) playerList.addPlayer(str);
                }
            }
        } else if (setting instanceof PotionListSetting potionList) {
            if (value instanceof List<?> list) {
                potionList.clearPotions();
                for (Object item : list) {
                    if (item instanceof String str) potionList.addPotion(str);
                }
            }
        } else if (setting instanceof InventoryItemListSetting invList) {
            if (value instanceof List<?> list) {
                invList.clearItems();
                for (Object item : list) {
                    if (item instanceof String str) invList.addItem(str);
                }
            }
        }
    }
    
    /**
     * Create a new profile
     */
    public Profile createProfile(String requestedName, int bind) {
        String profileName = normalizeProfileName(requestedName);
        String validationError = validateProfileName(profileName, null);
        if (validationError != null) {
            return null;
        }
        Profile profile = new Profile(profileName, bind);
        saveProfile(profile);
        profiles.add(profile);
        return profile;
    }
    
    private String normalizeProfileName(String name) {
        if (name == null) return "";
        return name.trim();
    }
    
    private String validateProfileName(String name, Profile excludeProfile) {
        if (name == null || name.isEmpty()) {
            return "Profile name cannot be empty";
        }
        for (char c : INVALID_PROFILE_NAME_CHARS) {
            if (name.indexOf(c) != -1) {
                return "Profile name contains invalid character: " + c;
            }
        }
        for (Profile profile : profiles) {
            if (profile != excludeProfile && profile.getName().equalsIgnoreCase(name)) {
                return "Profile '" + name + "' already exists";
            }
        }
        return null;
    }
    
    /**
     * Get profile by name
     */
    public Profile getProfile(String profileName) {
        for (Profile profile : profiles) {
            if (profile.getName().equalsIgnoreCase(profileName)) {
                return profile;
            }
        }
        return null;
    }
    
    /**
     * Save profile to file
     */
    public void saveProfile(Profile profile) {
        if (profile != null) save(profile.getName());
    }
    
    public void saveProfile(String profileName) {
        save(profileName);
    }
    
    /**
     * Load profile from file
     */
    public void loadProfile(String profileName) {
        load(profileName);
    }
    
    /**
     * Delete profile
     */
    public boolean deleteProfile(String profileName) {
        File file = new File(PROFILES_DIR, profileName + ".json");
        if (file.exists()) {
            file.delete();
            profiles.removeIf(p -> p.getName().equalsIgnoreCase(profileName));
            return true;
        }
        return false;
    }
    
    /**
     * Rename profile
     */
    public boolean renameProfile(Profile profile, String newName) {
        if (profile == null) return false;
        String oldName = profile.getName();
        String validationError = validateProfileName(newName, profile);
        if (validationError != null) {
            return false;
        }
        File oldFile = new File(PROFILES_DIR, oldName + ".json");
        File newFile = new File(PROFILES_DIR, newName + ".json");
        if (oldFile.exists() && !newFile.exists()) {
            oldFile.renameTo(newFile);
            profile.setName(newName);
            return true;
        }
        return false;
    }
    
    /**
     * List all profiles
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
     * Load all profiles from disk
     */
    public void loadProfiles() {
        profiles.clear();
        String[] profileNames = list();
        for (String name : profileNames) {
            Profile profile = new Profile(name, 0);
            profiles.add(profile);
        }
    }
    
    /**
     * Search profiles by name (autocomplete)
     */
    public List<String> suggestProfileNames(String query) {
        String loweredQuery = query == null ? "" : query.toLowerCase();
        List<String> profileNames = new ArrayList<>();
        for (Profile profile : profiles) {
            if (profile.getName().toLowerCase().startsWith(loweredQuery)) {
                profileNames.add(profile.getName());
            }
        }
        return profileNames;
    }
    
    /**
     * Check if profile exists
     */
    public boolean exists(String profileName) {
        return new File(PROFILES_DIR, profileName + ".json").exists();
    }
    
    /**
     * Get current profile name
     */
    public String getCurrentProfile() {
        return currentProfile;
    }
    
    /**
     * Set current profile name
     */
    public void setCurrentProfile(String profileName) {
        this.currentProfile = profileName;
    }
    
    /**
     * Import profile from external file
     */
    public void importProfile(File sourceFile) throws IOException {
        String name = sourceFile.getName().replace(".json", "");
        File dest = new File(PROFILES_DIR, name + ".json");
        java.nio.file.Files.copy(sourceFile.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        loadProfiles();
    }
    
    /**
     * Export profile to external file
     */
    public void exportProfile(String profileName, File destination) throws IOException {
        File source = new File(PROFILES_DIR, profileName + ".json");
        if (source.exists()) {
            java.nio.file.Files.copy(source.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
