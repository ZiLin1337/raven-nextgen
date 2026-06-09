package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class ESP extends Module {
    public ESP() {
        super("ESP", category.render);
        registerSetting(new SliderSetting("Mode", 0, new String[]{"Box", "2D", "Mixed"}));
    }
}