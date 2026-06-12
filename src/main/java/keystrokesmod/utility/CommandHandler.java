package keystrokesmod.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements IMinecraftInstance {
    public static List<String> responseLines = new ArrayList<>();
    
    public static boolean handleCommand(String message) {
        if (!message.startsWith(".")) return false;
        
        String[] args = message.substring(1).split(" ");
        String command = args[0].toLowerCase();
        
        switch (command) {
            case "help":
                print("&7=== Raven Commands ===");
                print("&7.friend <name> - Add friend");
                print("&7.enemy <name> - Add enemy");
                print("&7.bind <module> <key> - Bind module");
                print("&7.toggle <module> - Toggle module");
                print("&7.settings <module> - Open settings");
                print("&7.profile <save/load/list> <name> - Profile management");
                return true;
                
            case "friend":
                if (args.length < 2) {
                    print("&cUsage: .friend <name>");
                    return true;
                }
                Utils.addFriend(args[1]);
                print("&aAdded friend: &b" + args[1]);
                return true;
                
            case "enemy":
                if (args.length < 2) {
                    print("&cUsage: .enemy <name>");
                    return true;
                }
                Utils.addEnemy(args[1]);
                print("&aAdded enemy: &b" + args[1]);
                return true;
                
            case "unfriend":
                if (args.length < 2) {
                    print("&cUsage: .unfriend <name>");
                    return true;
                }
                Utils.removeFriend(args[1]);
                print("&aRemoved friend: &b" + args[1]);
                return true;
                
            case "unenemy":
                if (args.length < 2) {
                    print("&cUsage: .unenemy <name>");
                    return true;
                }
                Utils.removeEnemy(args[1]);
                print("&aRemoved enemy: &b" + args[1]);
                return true;
                
            case "toggle":
                if (args.length < 2) {
                    print("&cUsage: .toggle <module>");
                    return true;
                }
                // Module toggle logic
                print("&7Toggle: &b" + args[1]);
                return true;
                
            case "bind":
                if (args.length < 3) {
                    print("&cUsage: .bind <module> <key>");
                    return true;
                }
                // Key bind logic
                print("&7Bound &b" + args[1] + " &7to &b" + args[2]);
                return true;
                
            default:
                print("&cUnknown command. Type .help for help.");
                return true;
        }
    }
    
    public static void print(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(message.replace("&7", "§7").replace("&a", "§a").replace("&b", "§b").replace("&c", "§c")), false);
        }
        responseLines.add(message);
    }
    
    public static void print(String message, int index) {
        print(message);
    }
}
