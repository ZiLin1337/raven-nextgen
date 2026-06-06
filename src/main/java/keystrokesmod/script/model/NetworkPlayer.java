package keystrokesmod.script.model;

// import com.mojang.authlib.GameProfile; // unused
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

public class NetworkPlayer {
    public String name;
    public String displayName;
    public int latency;
    public GameProfile gameProfile;
    public PlayerListEntry playerInfo;

    public NetworkPlayer(PlayerListEntry playerInfo) {
        this.playerInfo = playerInfo;
        this.name = playerInfo.getProfile().getName();
        this.displayName = getName();
        this.latency = playerInfo.getLatency();
        this.gameProfile = playerInfo.getProfile();
    }

    public String getName() { return name; }

    public int getLatency() { return latency; }

    public GameProfile getGameProfile() { return gameProfile; }

    public String getFormattedDisplayName() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && gameProfile.getId().equals(mc.player.getUuid())) {
            return mc.player.getDisplayName().getString();
        }
        Team team = mc.world.getScoreboard().getPlayerTeam(name);
        return Team.decorateName(team, Text.literal(name)).getString();
    }

    public static NetworkPlayer convert(PlayerListEntry playerInfo) {
        return playerInfo == null ? null : new NetworkPlayer(playerInfo);
    }
}
