package keystrokesmod.event;

import meteordevelopment.orbit.ICancellable;

public class Event implements ICancellable {
    private boolean canceled;

    @Override
    public void setCancelled(boolean cancelled) {
        this.canceled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
    
    public boolean isCanceled() {
        return canceled;
    }
}
