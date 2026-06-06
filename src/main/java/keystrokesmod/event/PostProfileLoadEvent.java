package keystrokesmod.event;

public class PostProfileLoadEvent extends Event {
    private String profileName;
    public PostProfileLoadEvent(String profileName) { this.profileName = profileName; }
    public String getProfileName() { return profileName; }
}
