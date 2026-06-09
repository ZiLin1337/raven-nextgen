package keystrokesmod.utility;

public class PlayerRelationsManager {
    public static class PlayerEntry {
        private final String key;
        public PlayerEntry(String key) { this.key = key; }
        public String getKey() { return key; }
        public String getValue() { return key; }
    }
    public boolean addEnemy(String name) { return true; }
    public boolean removeEnemy(String name) { return true; }
    public void save() {}
}
