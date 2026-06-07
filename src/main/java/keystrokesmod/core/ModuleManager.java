package keystrokesmod.module.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Module Manager for 1.21.4
 */
public class ModuleManager {
    private static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new ArrayList<>();
    
    public static ModuleManager getINSTANCE() { return INSTANCE; }
    
    public void register(Module module) {
        modules.add(module);
    }
    
    public void unregister(Module module) {
        modules.remove(module);
    }
    
    public List<Module> getModules() { return modules; }
    
    public List<Module> getModulesByCategory(Module.Category category) {
        return modules.stream()
            .filter(m -> m.getCategory() == category)
            .collect(Collectors.toList());
    }
    
    public Module getModule(String name) {
        return modules.stream()
            .filter(m -> m.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
    
    public void enableAll() {
        modules.forEach(Module::enable);
    }
    
    public void disableAll() {
        modules.forEach(Module::disable);
    }
}