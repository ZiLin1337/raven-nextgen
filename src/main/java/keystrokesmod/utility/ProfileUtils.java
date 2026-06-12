package keystrokesmod.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProfileUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path profilesDir = Paths.get(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "raven", "profiles");
    
    public static void saveProfile(String name, JsonObject data) {
        try {
            Files.createDirectories(profilesDir);
            Path file = profilesDir.resolve(name + ".json");
            Files.writeString(file, gson.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static JsonObject loadProfile(String name) {
        try {
            Path file = profilesDir.resolve(name + ".json");
            if (Files.exists(file)) {
                return gson.fromJson(Files.readString(file), JsonObject.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void deleteProfile(String name) {
        try {
            Path file = profilesDir.resolve(name + ".json");
            Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String[] listProfiles() {
        try {
            if (Files.exists(profilesDir)) {
                return Files.list(profilesDir)
                    .filter(p -> p.toString().endsWith(".json"))
                    .map(p -> p.getFileName().toString().replace(".json", ""))
                    .toArray(String[]::new);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }
}
