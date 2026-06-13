package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class BlockListSetting extends Setting {
    private final List<String> blocks = new ArrayList<>();
    public GroupSetting group;
    
    public BlockListSetting(String name) {
        super(name);
    }
    
    public BlockListSetting(GroupSetting group, String name) {
        super(name);
        this.group = group;
    }
    
    public void addBlock(String registryName) {
        if (!blocks.contains(registryName)) {
            blocks.add(registryName);
        }
    }
    
    public void removeBlock(String registryName) {
        blocks.remove(registryName);
    }
    
    public List<String> getBlocks() {
        return blocks;
    }
    
    public List<String> getEnabledBlocks() {
        return new ArrayList<>(blocks);
    }
    
    public void clearBlocks() {
        blocks.clear();
    }
    
    public boolean contains(String blockName) {
        return blocks.contains(blockName);
    }
    
    @Override
    public void loadProfile(JsonObject data) {
    }
}
