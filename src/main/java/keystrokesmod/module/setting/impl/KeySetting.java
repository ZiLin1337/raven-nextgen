package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

public class KeySetting extends Setting {
    private int keyCode;
    public boolean isBinding;
    private GroupSetting group;

    public KeySetting(String name, int keyCode) {
        super(name);
        this.keyCode = keyCode;
    }

    public KeySetting(GroupSetting group, String name, int keyCode) {
        super(name);
        this.group = group;
        this.keyCode = keyCode;
    }

    public int getKeyCode() { return keyCode; }
    public void setKeyCode(int keyCode) { this.keyCode = keyCode; }
    public boolean isPressed() { return keyCode != 0 && org.lwjgl.glfw.GLFW.glfwGetKey(
            net.minecraft.client.MinecraftClient.getInstance().getWindow().getHandle(), keyCode) == 1; }

    @Override
    public void loadProfile(JsonObject data) {
        String profileKey = group == null ? getName() : group.getName() + "." + getName();
        if (data.has(profileKey)) {
            try { keyCode = data.getAsJsonPrimitive(profileKey).getAsInt(); } catch (Exception e) {}
        }
    }
    public GroupSetting getGroup() { return group; }
}
