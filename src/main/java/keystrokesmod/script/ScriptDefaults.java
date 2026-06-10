package keystrokesmod.script;

import keystrokesmod.script.model.Bridge;
import keystrokesmod.Raven;
import java.util.*;

public class ScriptDefaults {
    public static final Bridge bridge = new Bridge();
    private static final LinkedHashMap<String, keystrokesmod.module.Module> modulesMap = new LinkedHashMap<>();

    public static void reloadModules() {
        modulesMap.clear();
    }

    public static class client {
    }

    public static class world {
    }

    public static class modules {
        public modules(String superName) {
        }
    }
}
