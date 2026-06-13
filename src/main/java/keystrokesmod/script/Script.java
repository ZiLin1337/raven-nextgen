package keystrokesmod.script;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;

import javax.tools.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Script {
    public String name;
    public Class<?> clazz;
    public Object instance;
    public String scriptName;
    public String codeStr;
    public boolean error = false;
    public int STARTING_LINE;
    public File file;
    
    public Script(String name) {
        this.name = name;
        this.scriptName = "sc_" + name.replace(" ", "").replace(")", "_").replace("(", "_") + "_" + System.currentTimeMillis();
    }
    
    public boolean run() {
        try {
            if (this.scriptName == null || this.codeStr == null) {
                return false;
            }
            
            String compiledDir = Raven.scriptManager.COMPILED_DIR;
            File file = new File(compiledDir);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdir();
            }
            
            if (Raven.scriptManager.compiler == null) {
                Utils.sendMessage("&cJava compiler not available");
                return false;
            }
            
            DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
            StandardJavaFileManager fileManager = Raven.scriptManager.compiler.getStandardFileManager(diagnosticCollector, null, null);
            
            List<String> compilationOptions = new ArrayList<>();
            compilationOptions.add("-d");
            compilationOptions.add(compiledDir);
            compilationOptions.add("-encoding");
            compilationOptions.add("UTF-8");
            
            // 获取当前JAR路径
            String jarPath = getJarPath();
            if (jarPath != null && !jarPath.isEmpty()) {
                compilationOptions.add("-classpath");
                compilationOptions.add(jarPath);
            }
            
            JavaSourceFromString source = new JavaSourceFromString(this.scriptName, this.codeStr, this.STARTING_LINE);
            
            boolean success = Raven.scriptManager.compiler.getTask(
                null, fileManager, diagnosticCollector, compilationOptions, null, List.of(source)
            ).call();
            
            if (!success) {
                this.error = true;
                // 报告编译错误
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics()) {
                    Utils.sendMessage("&cScript error: " + diagnostic.getMessage(null));
                }
                return false;
            }
            
            // 加载编译后的类
            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, Script.class.getClassLoader())) {
                this.clazz = classLoader.loadClass(this.scriptName);
                this.instance = this.clazz.getDeclaredConstructor().newInstance();
            }
            
            fileManager.close();
            return true;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            this.error = true;
            return false;
        }
    }
    
    private String getJarPath() {
        try {
            return Script.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (Exception e) {
            return "";
        }
    }
    
    public boolean invoke(final String methodName, final Object... args) {
        if (this.clazz == null || this.instance == null) {
            return false;
        }
        try {
            Method method = null;
            for (final Method method2 : this.clazz.getDeclaredMethods()) {
                if (method2.getName().equalsIgnoreCase(methodName) && method2.getParameterCount() == args.length) {
                    method = method2;
                    break;
                }
            }
            if (method != null) {
                method.setAccessible(true);
                method.invoke(this.instance, args);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public int getBoolean(final String s, final Object... array) {
        if (this.clazz == null || this.instance == null) {
            return -1;
        }
        try {
            Method method = null;
            for (final Method method2 : this.clazz.getDeclaredMethods()) {
                if (method2.getName().equalsIgnoreCase(s) && method2.getParameterCount() == array.length && method2.getReturnType().equals(Boolean.TYPE)) {
                    method = method2;
                    break;
                }
            }
            if (method != null) {
                method.setAccessible(true);
                final Object invoke = method.invoke(this.instance, array);
                if (invoke instanceof Boolean) {
                    return ((Boolean) invoke) ? 1 : 0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }
    
    public String getString(final String s, final Object... array) {
        if (this.clazz == null || this.instance == null) {
            return null;
        }
        try {
            Method method = null;
            for (final Method method2 : this.clazz.getDeclaredMethods()) {
                if (method2.getName().equalsIgnoreCase(s) && method2.getParameterCount() == array.length && method2.getReturnType().equals(String.class)) {
                    method = method2;
                    break;
                }
            }
            if (method != null) {
                method.setAccessible(true);
                final Object invoke = method.invoke(this.instance, array);
                if (invoke instanceof String) {
                    return (String) invoke;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Object getObject(final String s, final Object... array) {
        if (this.clazz == null || this.instance == null) {
            return null;
        }
        try {
            Method method = null;
            for (final Method method2 : this.clazz.getDeclaredMethods()) {
                if (method2.getName().equalsIgnoreCase(s) && method2.getParameterCount() == array.length) {
                    method = method2;
                    break;
                }
            }
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(this.instance, array);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
