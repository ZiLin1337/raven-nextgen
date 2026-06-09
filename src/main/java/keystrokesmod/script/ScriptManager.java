package keystrokesmod.script;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptManager {
    private final File scriptDir;
    public Map<String, Script> scripts = new ConcurrentHashMap<>();
    public final String COMPILED_DIR = "raven-nextgen/scripts/compiled";
    public final java.util.List<String> imports = new java.util.ArrayList<>();
    public javax.tools.JavaCompiler compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
    public String jarPath = "";
    public File directory;

    public ScriptManager() {
        scriptDir = new File("raven-nextgen/scripts");
        directory = scriptDir;
        if (!scriptDir.exists()) scriptDir.mkdirs();
        new File(COMPILED_DIR).mkdirs();
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
