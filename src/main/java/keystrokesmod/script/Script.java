package keystrokesmod.script;

import keystrokesmod.module.Module;

import java.io.File;
import java.lang.reflect.Method;

public class Script {
    public String name;
    public Class clazz;
    public Object instance;
    public String scriptName;
    public String codeStr;
    public boolean error = false;
    public int STARTING_LINE;
    public File file;
    
    public Script(String name) {
        this.name = name;
        this.scriptName = "sc_" + name.replace(" ", "").replace(")", "_").replace("(", "_");
    }
    
    public boolean invoke(final String methodName, final Object... args) {
        if (this.clazz == null || this.instance == null) {
            return false;
        }
        Method method = null;
        for (final Method method2 : this.clazz.getDeclaredMethods()) {
            if (method2.getName().equalsIgnoreCase(methodName) && method2.getParameterCount() == args.length) {
                method = method2;
                break;
            }
        }
        if (method != null) {
            try {
                method.setAccessible(true);
                method.invoke(this.instance, args);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
