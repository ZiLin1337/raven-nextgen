package keystrokesmod.utility;
import keystrokesmod.module.setting.impl.PotionListSetting;
import java.util.*;

public final class PotionSearchIndex {
    private static final int MAX = 100;
    private static List<PotionEntry> all;
    public static final class PotionEntry { public final int id; public final String key, name; public Entry(int id, String key, String name) { this.id=id; this.key=key; this.name=name!=null?name:""; } }
    private PotionSearchIndex() {}
    public static List<PotionEntry> search(String q, PotionListSetting s) { ensure(); List<PotionEntry> r=new ArrayList<PotionEntry>(); for(Entry e:all) { if(!s.containsPotion(e.key) && (e.key.contains(q!=null?q:"".toLowerCase()) || e.name.toLowerCase().contains(q!=null?q:"".toLowerCase()))) r.add(e); } return r; }
    private static void ensure() { if(all!=null) return; all=Collections.unmodifiableList(new ArrayList<PotionEntry>()); }
}
