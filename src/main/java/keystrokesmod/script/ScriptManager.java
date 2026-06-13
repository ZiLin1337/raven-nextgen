package keystrokesmod.script;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ScriptManager {
    private static final char[] INVALID_SCRIPT_NAME_CHARS = new char[]{'\\', '/', ':', '*', '?', '"', '<', '>', '|'};
    
    public LinkedHashMap<Script, Module> scripts = new LinkedHashMap<>();
    public JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    public boolean deleteTempFiles = true;
    public File directory;
    public String COMPILED_DIR;
    
    public ScriptManager() {
        directory = new File(Raven.mc.runDirectory, "raven-nextgen/scripts");
        COMPILED_DIR = new File(Raven.mc.runDirectory, "raven-nextgen/scripts/compiled").getAbsolutePath();
    }
    
    public String createScript(String requestedName) {
        String scriptName = normalizeScriptName(requestedName);
        String validationError = validateScriptName(scriptName, null);
        if (validationError != null) {
            Utils.sendMessage("&c" + validationError);
            return null;
        }
        try {
            if (!directory.exists() && !directory.mkdirs()) {
                Utils.sendMessage("&cFailed to create scripts folder.");
                return null;
            }
            Files.write(new File(directory, scriptName + ".java").toPath(), 
                buildDefaultScriptTemplate(scriptName).getBytes(StandardCharsets.UTF_8));
            loadScripts();
            return scriptName;
        } catch (Exception e) {
            Utils.sendMessage("&cFailed to create script: &b" + scriptName);
            e.printStackTrace();
            return null;
        }
    }
    
    public void loadScripts() {
        for (Module module : scripts.values()) {
            module.disable();
        }
        scripts.clear();
        
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        File[] scriptFiles = directory.listFiles((dir, name) -> name.endsWith(".java"));
        if (scriptFiles == null || scriptFiles.length == 0) {
            return;
        }
        
        for (File file : scriptFiles) {
            try {
                String code = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                Script script = new Script(file.getName().replace(".java", ""));
                script.codeStr = code;
                script.file = file;
                
                // Create a wrapper module for the script
                Module module = new ScriptModule(script);
                scripts.put(script, module);
            } catch (Exception e) {
                Utils.sendMessage("&cFailed to load script: &b" + file.getName());
                e.printStackTrace();
            }
        }
    }
    
    public Module getModule(Script script) {
        return scripts.get(script);
    }
    
    public Script getScript(Module module) {
        for (Map.Entry<Script, Module> entry : scripts.entrySet()) {
            if (entry.getValue() == module) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public void deleteScript(String name) {
        File file = new File(directory, name + ".java");
        if (file.exists()) {
            file.delete();
        }
        scripts.entrySet().removeIf(entry -> entry.getKey().name.equals(name));
    }
    
    public List<String> getScriptNames() {
        List<String> names = new ArrayList<>();
        for (Script script : scripts.keySet()) {
            names.add(script.name);
        }
        return names;
    }
    
    public List<String> suggestScriptNames(String query) {
        String loweredQuery = query == null ? "" : query.toLowerCase();
        List<String> scriptNames = new ArrayList<>();
        for (Script script : scripts.keySet()) {
            if (script.name.toLowerCase().startsWith(loweredQuery)) {
                scriptNames.add(script.name);
            }
        }
        return scriptNames;
    }
    
    private String normalizeScriptName(String name) {
        if (name == null) return "";
        return name.trim();
    }
    
    private String validateScriptName(String name, Script excludeScript) {
        if (name == null || name.isEmpty()) {
            return "Script name cannot be empty";
        }
        for (char c : INVALID_SCRIPT_NAME_CHARS) {
            if (name.indexOf(c) != -1) {
                return "Script name contains invalid character: " + c;
            }
        }
        for (Script script : scripts.keySet()) {
            if (script != excludeScript && script.name.equalsIgnoreCase(name)) {
                return "Script '" + name + "' already exists";
            }
        }
        return null;
    }
    
    private String buildDefaultScriptTemplate(String name) {
        return "package keystrokesmod.script.user;\n" +
               "\n" +
               "import keystrokesmod.script.api.*;\n" +
               "import keystrokesmod.script.api.events.*;\n" +
               "\n" +
               "public class " + name + " extends ModuleScript {\n" +
               "    \n" +
               "    public " + name + "() {\n" +
               "        super(\"" + name + "\", Category.MISC);\n" +
               "    }\n" +
               "    \n" +
               "    @Override\n" +
               "    public void onEnable() {\n" +
               "        // Called when the module is enabled\n" +
               "    }\n" +
               "    \n" +
               "    @Override\n" +
               "    public void onDisable() {\n" +
               "        // Called when the module is disabled\n" +
               "    }\n" +
               "    \n" +
               "    @Override\n" +
               "    public void onTick() {\n" +
               "        // Called every tick\n" +
               "    }\n" +
               "}\n";
    }
}
