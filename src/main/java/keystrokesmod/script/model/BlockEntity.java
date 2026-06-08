package keystrokesmod.script.model;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class BlockEntity {
    public Vec3d position;
    public String type;
    public BlockEntity tileEntity;

    public BlockEntity(BlockEntity tileEntity) {
        this.tileEntity = tileEntity;
        this.position = Vec3d.convert(tileEntity.getPos());
        this.type = tileEntity.getType().getName().getString();
    }

    public Vec3d getPosition() { return position; }
    public String getType() { return type; }
    
    public net.minecraft.block.Block getBlock() {
        if (tileEntity.getWorld() != null) {
            return tileEntity.getWorld().getBlockState(tileEntity.getPos()).getBlock();
        }
        return null;
    }

    @Override
    public String toString() {
        return "BlockEntity(" + type + ")";
    }
}
