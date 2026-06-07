package keystrokesmod.script.model;

import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class Block {
    public String name;
    public String displayName;
    public Vec3d position;
    public boolean isAir;
    public float hardness;
    public float resistance;
    public int lightLevel;

    public Block(net.minecraft.block.Block block, BlockPos blockPos) {
        this.name = block.getTranslationKey();
        this.displayName = block.getName().getString();
        this.position = Vec3d.convert(blockPos);
        BlockState state = block.getDefaultState();
        this.isAir = state.isAir();
        this.hardness = block.getHardness();
        this.resistance = block.getBlastResistance();
    }

    public Block(BlockState state, BlockPos blockPos) {
        this(state.getBlock(), blockPos);
    }

    public Block(double x, double y, double z) {
        this(BlockUtils.getBlockState(BlockPos.ofFloored(x, y, z).getBlock(), BlockPos.ofFloored(x, y, z)));
    }

    public Block(String name, Vec3d position) {
        this.name = name;
        this.position = position;
    }

    public Block(String name) {
        this(name, new Vec3d(-1, -1, -1));
    }

    public Block(Vec3d position) {
        this(BlockUtils.getBlockState(BlockPos.ofFloored(position.x, position.y, position.z).getBlock(), BlockPos.ofFloored(position.x, position.y, position.z)));
    }

    public boolean isAir() { return isAir; }
    public String getName() { return name; }
    public Vec3d getPosition() { return position; }

    @Override
    public String toString() {
        return this.name;
    }
}
