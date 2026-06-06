package keystrokesmod.script.model;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Vec3 {
    public double x, y, z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(net.minecraft.util.math.Vec3d minecraftVec3) {
        this(minecraftVec3.x, minecraftVec3.y, minecraftVec3.z);
    }

    public Vec3(BlockPos blockPos) {
        this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Vec3 add(Vec3 position) {
        return new Vec3(this.x + position.x, this.y + position.y, this.z + position.z);
    }

    public Vec3 add(double x, double y, double z) {
        return new Vec3(this.x + x, this.y + y, this.z + z);
    }

    public Vec3 ceil() { return new Vec3(Math.ceil(this.x), Math.ceil(this.y), Math.ceil(this.z)); }
    public Vec3 floor() { return new Vec3(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z)); }
    public Vec3 negate() { return new Vec3(-this.x, -this.y, -this.z); }
    public double distanceTo(Vec3 v) {
        double dx = this.x - v.x, dy = this.y - v.y, dz = this.z - v.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Vec3 convert(BlockPos blockPos) {
        return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static Vec3 convert(Vec3d vec3d) {
        return new Vec3(vec3d.x, vec3d.y, vec3d.z);
    }

    public static BlockPos getBlockPos(Vec3 blockPos) {
        return BlockPos.ofFloored(blockPos.x, blockPos.y, blockPos.z);
    }

    public static Vec3d getVec3(Vec3 vec3) {
        return new Vec3d(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public String toString() {
        return "Vec3(" + this.x + "," + this.y + "," + this.z + ")";
    }
}
