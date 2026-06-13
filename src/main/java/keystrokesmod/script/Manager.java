package keystrokesmod.script;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.TextSetting;
import keystrokesmod.utility.Utils;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Manager extends Module {
    private final TextSetting createScriptName;
    private ButtonSetting loadScripts, openFolder, createScript;
    private long lastLoad = 0;
    
    public Manager() {
        super("Manager", Module.category.scripts);
        this.registerSetting(createScriptName = new TextSetting("Script name", "", "Type a script name...", 32, this::createScript));
        this.registerSetting(createScript = new ButtonSetting("Create script", () -> {
            createScript();
        }));
        this.registerSetting(loadScripts = new ButtonSetting("Load scripts", () -> {
            if (Raven.scriptManager.compiler == null) {
                Utils.sendMessage("&cCompiler error, JDK not found");
            } else {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - lastLoad > 1500) {
                    lastLoad = currentTimeMillis;
                    Raven.scriptManager.loadScripts();
                    if (Raven.scriptManager.scripts.isEmpty()) {
                        Utils.sendMessage("&7No scripts found.");
                    } else {
                        Utils.sendMessage("&7Loaded &b" + Raven.scriptManager.scripts.size() + " &7script" + 
                            (Raven.scriptManager.scripts.size() == 1 ? "" : "s") + ".");
                    }
                } else {
                    Utils.sendMessage("&cYou are on cooldown.");
                }
            }
        }));
        this.registerSetting(openFolder = new ButtonSetting("Open folder", () -> {
            try {
                Desktop.getDesktop().open(Raven.scriptManager.directory);
            } catch (IOException ex) {
                Raven.scriptManager.directory.mkdirs();
                Utils.sendMessage("&cError locating folder, recreated.");
            }
        }));
        this.canBeEnabled = false;
        this.ignoreOnSave = true;
    }
    
    private void createScript() {
        if (Raven.scriptManager == null) {
            return;
        }
        String scriptName = Raven.scriptManager.createScript(createScriptName.getText());
        if (scriptName != null) {
            createScriptName.setText("");
            Utils.sendMessage("&7Created script: &b" + scriptName);
        }
    }
}
