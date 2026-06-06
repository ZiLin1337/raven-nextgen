package keystrokesmod.script;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptManager {
    private final File scriptDir;
    public Map<String, Script> scripts = new ConcurrentHashMap<>();

    public ScriptManager() {
        scriptDir = new File("raven-nextgen/scripts");
        if (!scriptDir.exists()) scriptDir.mkdirs();
    }

    public void loadScripts() {
        // TODO: Load JS scripts from scriptDir
    }

    public void unloadScripts() {
        scripts.clear();
    }

    public Script getScript(String name) {
        return scripts.get(name);
    }
}
