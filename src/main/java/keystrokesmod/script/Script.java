package keystrokesmod.script;

import keystrokesmod.Raven;
import keystrokesmod.script.model.JavaSourceFromString;

import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class Script {
    private final String scriptName;
    private final String codeStr;
    private final int STARTING_LINE = 1;
    private boolean compiled = false;
    private Object instance;

    public Script(String scriptName, String codeStr) {
        this.scriptName = scriptName;
        this.codeStr = codeStr;
    }

    public void compile() {
        // TODO: Script compilation not implemented yet
        // 原本的编译逻辑被注释掉，避免引用不存在的 scriptManager 字段
        compiled = false;
    }

    public void load() {
        // TODO: Script loading not implemented yet
    }

    public void unload() {
        // TODO: Script unloading not implemented yet
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getCodeStr() {
        return codeStr;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public Object getInstance() {
        return instance;
    }
}
