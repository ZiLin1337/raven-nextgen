package keystrokesmod.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
    public static List<Module> modules = new ArrayList<>();
    public static List<Module> organizedModules = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, Module> modulesByName = new HashMap<>();
    private static final Map<String, Module> modulesByNormalizedName = new HashMap<>();
    private static final Map<Class<?>, Module> modulesByClass = new HashMap<>();

    // Static module references
    public static keystrokesmod.module.impl.combat.KillAura killAura;
    public static keystrokesmod.module.impl.combat.Velocity velocity;
    public static keystrokesmod.module.impl.combat.AntiDebuff antiDebuff;
    public static keystrokesmod.module.impl.combat.Reduce reduce;
    public static keystrokesmod.module.impl.combat.AntiKnockback antiKnockback;
    public static keystrokesmod.module.impl.combat.AutoClicker autoClicker;
    public static keystrokesmod.module.impl.movement.KeepSprint keepSprint;
    public static keystrokesmod.module.impl.movement.Sprint sprint;
    public static keystrokesmod.module.impl.movement.NoSlow noSlow;
    public static keystrokesmod.module.impl.movement.MovementFix movementFix;
    public static keystrokesmod.module.impl.movement.LongJump longJump;
    public static keystrokesmod.module.impl.movement.Timer timer;
    public static keystrokesmod.module.impl.movement.Fly fly;
    public static keystrokesmod.module.impl.movement.InvMove invMove;
    public static keystrokesmod.module.impl.movement.Speed speed;
    public static keystrokesmod.module.impl.player.NoFall noFall;
    public static keystrokesmod.module.impl.player.SafeWalk safeWalk;
    public static keystrokesmod.module.impl.player.FastMine fastMine;
    public static keystrokesmod.module.impl.player.BedAura bedAura;
    public static keystrokesmod.module.impl.player.NoRotate noRotate;
    public static keystrokesmod.module.impl.render.Freelook freelook;
    public static keystrokesmod.module.impl.render.NoHurtCam noHurtCam;
    public static keystrokesmod.module.impl.render.HUD hud;
    public static keystrokesmod.module.impl.render.Weather weather;
    public static keystrokesmod.module.impl.render.PlayerESP playerESP;
    public static keystrokesmod.module.impl.render.MobESP mobESP;
    public static keystrokesmod.module.impl.other.NameHider nameHider;
    public static keystrokesmod.module.impl.render.AntiShuffle antiShuffle;

    public static void register() {
        // Register modules
        addModule(sprint = new keystrokesmod.module.impl.movement.Sprint());
        addModule(noSlow = new keystrokesmod.module.impl.movement.NoSlow());
        addModule(killAura = new keystrokesmod.module.impl.combat.KillAura());
        addModule(velocity = new keystrokesmod.module.impl.combat.Velocity());
        addModule(antiDebuff = new keystrokesmod.module.impl.combat.AntiDebuff());
        addModule(reduce = new keystrokesmod.module.impl.combat.Reduce());
        addModule(antiKnockback = new keystrokesmod.module.impl.combat.AntiKnockback());
        addModule(autoClicker = new keystrokesmod.module.impl.combat.AutoClicker());
        addModule(keepSprint = new keystrokesmod.module.impl.movement.KeepSprint());
        addModule(movementFix = new keystrokesmod.module.impl.movement.MovementFix());
        addModule(longJump = new keystrokesmod.module.impl.movement.LongJump());
        addModule(timer = new keystrokesmod.module.impl.movement.Timer());
        addModule(fly = new keystrokesmod.module.impl.movement.Fly());
        addModule(invMove = new keystrokesmod.module.impl.movement.InvMove());
        addModule(speed = new keystrokesmod.module.impl.movement.Speed());
        addModule(noFall = new keystrokesmod.module.impl.player.NoFall());
        addModule(safeWalk = new keystrokesmod.module.impl.player.SafeWalk());
        addModule(fastMine = new keystrokesmod.module.impl.player.FastMine());
        addModule(bedAura = new keystrokesmod.module.impl.player.BedAura());
        addModule(noRotate = new keystrokesmod.module.impl.player.NoRotate());
        addModule(freelook = new keystrokesmod.module.impl.render.Freelook());
        addModule(noHurtCam = new keystrokesmod.module.impl.render.NoHurtCam());
        addModule(hud = new keystrokesmod.module.impl.render.HUD());
        addModule(weather = new keystrokesmod.module.impl.render.Weather());
        addModule(playerESP = new keystrokesmod.module.impl.render.PlayerESP());
        addModule(mobESP = new keystrokesmod.module.impl.render.MobESP());
        addModule(nameHider = new keystrokesmod.module.impl.other.NameHider());
        addModule(antiShuffle = new keystrokesmod.module.impl.render.AntiShuffle());
        modules.sort(Comparator.comparing(Module::getName));
    }

    public static void addModule(Module module) {
        modules.add(module);
        modulesByName.put(module.getName(), module);
        modulesByNormalizedName.put(normalizeModuleName(module.getName()), module);
        modulesByClass.put(module.getClass(), module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> inCategory(Module.category category) {
        ArrayList<Module> categoryModules = new ArrayList<>();
        for (Module module : this.getModules()) {
            if (module.moduleCategory().equals(category)) {
                categoryModules.add(module);
            }
        }
        return categoryModules;
    }

    public static Module getModule(String moduleName) {
        Module module = modulesByName.get(moduleName);
        if (module != null) {
            return module;
        }
        return modulesByNormalizedName.get(normalizeModuleName(moduleName));
    }

    public static Module getModule(Class<?> clazz) {
        return modulesByClass.get(clazz);
    }

    /**
     * Dynamically scan and register all modules from the classpath.
     * This replaces manual registration and ensures all 80+ modules are available.
     */
    public static void registerAll() {
        // Register modules by class scanning - all concrete Module subclasses
        String basePackage = "keystrokesmod.module.impl";
        String[] categories = {"combat", "movement", "player", "render", "world", "minigames", "fun", "other", "client"};
        for (String cat : categories) {
            String pkg = basePackage + "." + cat;
            try {
                java.io.File dir = new java.io.File(
                    ModuleManager.class.getProtectionDomain().getCodeSource().getLocation().getFile(),
                    pkg.replace('.', '/')
                );
                if (!dir.exists() || !dir.isDirectory()) continue;
                for (java.io.File f : dir.listFiles()) {
                    if (!f.getName().endsWith(".class")) continue;
                    String className = f.getName().replace(".class", "");
                    try {
                        Class<?> clazz = Class.forName(pkg + "." + className);
                        if (Module.class.isAssignableFrom(clazz) && clazz != Module.class) {
                            Module module = (Module) clazz.getDeclaredConstructor().newInstance();
                            addModule(module);
                        }
                    } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}
        }
        modules.sort(Comparator.comparing(Module::getName));
    }

    public static void sort() {
        // TODO: Sort organizedModules
    }

    private static String normalizeModuleName(String moduleName) {
        if (moduleName == null) {
            return "";
        }
        StringBuilder normalized = new StringBuilder(moduleName.length());
        for (int i = 0; i < moduleName.length(); i++) {
            char character = moduleName.charAt(i);
            if (Character.isLetterOrDigit(character)) {
                normalized.append(Character.toLowerCase(character));
            }
        }
        return normalized.toString();
    }
}