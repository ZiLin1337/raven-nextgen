package keystrokesmod.event;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class CollisionEvent extends Event {
    public BlockPos pos;
    public BlockState state;
    public Box boundingBox;
    private boolean cancelled;

    public CollisionEvent(BlockPos pos, BlockState state, Box boundingBox) {
        this.pos = pos;
        this.state = state;
        this.boundingBox = boundingBox;
    }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}