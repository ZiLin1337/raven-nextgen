package keystrokesmod.module.setting.impl;
import keystrokesmod.module.setting.Setting;
public class TextSetting extends Setting {
    private String value;
    
    public TextSetting(String name, String defaultValue) {
        super(name);
        this.value = defaultValue;
    }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getText() { return value; }
    public void setText(String text) { this.value = text; }
}