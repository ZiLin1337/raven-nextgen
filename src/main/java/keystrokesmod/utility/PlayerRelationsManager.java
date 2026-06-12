package keystrokesmod.utility;

import java.util.HashSet;
import java.util.Set;

public class PlayerRelationsManager {
    private final Set<String> friends = new HashSet<>();
    private final Set<String> enemies = new HashSet<>();
    
    public boolean addFriend(String name) {
        if (enemies.remove(name.toLowerCase())) {
            Utils.sendMessage("&7Removed enemy &b" + name + " &7to add as friend");
        }
        return friends.add(name.toLowerCase());
    }
    
    public boolean removeFriend(String name) {
        return friends.remove(name.toLowerCase());
    }
    
    public boolean isFriend(String name) {
        return friends.contains(name.toLowerCase());
    }
    
    public boolean addEnemy(String name) {
        if (friends.remove(name.toLowerCase())) {
            Utils.sendMessage("&7Removed friend &b" + name + " &7to add as enemy");
        }
        return enemies.add(name.toLowerCase());
    }
    
    public boolean removeEnemy(String name) {
        return enemies.remove(name.toLowerCase());
    }
    
    public boolean isEnemy(String name) {
        return enemies.contains(name.toLowerCase());
    }
    
    public Set<String> getFriends() {
        return friends;
    }
    
    public Set<String> getEnemies() {
        return enemies;
    }
}
