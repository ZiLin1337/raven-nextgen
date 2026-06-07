package keystrokesmod.module.setting.impl;

import keystrokesmod.module.setting.Setting;
import com.google.gson.JsonObject;

public class NumberSetting extends Setting {
    private double value;
    private final double min, max, step;
    
    public NumberSetting(String name, double defaultValue, double min, double max, double step) {
        super(name);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.step = step;
    }
    
    public double getValue() { return value; }
    public void setValue(double value) { this.value = Math.max(min, Math.min(max, value)); }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }
    
    @Override
    public void loadProfile(JsonObject data) {
        if (data.has(getName())) {
            this.value = data.get(getName()).getAsDouble();
        }
    }
    
    @Override
    public void saveProfile(JsonObject data) {
        data.addProperty(getName(), value);
    }
}