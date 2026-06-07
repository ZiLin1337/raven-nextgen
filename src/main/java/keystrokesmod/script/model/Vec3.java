package keystrokesmod.script.model;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3dd;

public class Vec3d {
    public double x, y, z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d(net.minecraft.util.math.Vec3dd minecraftVec3d) {
        this(minecraftVec3d.x, minecraftVec3d.y, minecraftVec3d.z);
    }

    public Vec3d(BlockPos blockPos) {
        this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Vec3d add(Vec3d position) {
        return new Vec3d(this.x + position.x, this.y + position.y, this.z + position.z);
    }

    public Vec3d add(double x, double y, double z) {
        return new Vec3d(this.x + x, this.y + y, this.z + z);
    }

    public Vec3d ceil() { return new Vec3d(Math.ceil(this.x), Math.ceil(this.y), Math.ceil(this.z)); }
    public Vec3d floor() { return new Vec3d(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z)); }
    public Vec3d negate() { return new Vec3d(-this.x, -this.y, -this.z); }
    public double distanceTo(Vec3d v) {
        double dx = this.x - v.x, dy = this.y - v.y, dz = this.z - v.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Vec3d convert(BlockPos blockPos) {
        return new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static Vec3d convert(Vec3dd vec3d) {
        return new Vec3d(vec3d.x, vec3d.y, vec3d.z);
    }

    public static BlockPos getBlockPos(Vec3d blockPos) {
        return BlockPos.ofFloored(blockPos.x, blockPos.y, blockPos.z);
    }

    public static Vec3dd getVec3d(Vec3d vec3) {
        return new Vec3dd(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public String toString() {
        return "Vec3d(" + this.x + "," + this.y + "," + this.z + ")";
    }
}
