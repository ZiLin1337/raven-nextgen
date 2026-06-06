package keystrokesmod.utility.math;

/**
 * 3D Vector class for math operations.
 */
public class Vec3 {
    public double x, y, z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d() {
        this(0, 0, 0);
    }

    public Vec3d add(Vec3d other) {
        return new Vec3d(x + other.x, y + other.y, z + other.z);
    }

    public Vec3d add(double x, double y, double z) {
        return new Vec3d(this.x + x, this.y + y, this.z + z);
    }

    public Vec3d subtract(Vec3d other) {
        return new Vec3d(x - other.x, y - other.y, z - other.z);
    }

    public Vec3d multiply(double scalar) {
        return new Vec3d(x * scalar, y * scalar, z * scalar);
    }

    public Vec3d divide(double scalar) {
        return new Vec3d(x / scalar, y / scalar, z / scalar);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public Vec3d normalize() {
        double len = length();
        if (len == 0) return new Vec3d();
        return divide(len);
    }

    public double distanceTo(Vec3d other) {
        return subtract(other).length();
    }

    public double distanceSquaredTo(Vec3d other) {
        return subtract(other).lengthSquared();
    }

    public double dot(Vec3d other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vec3d cross(Vec3d other) {
        return new Vec3d(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }

    public Vec3d lerp(Vec3d target, double t) {
        return new Vec3d(
            x + (target.x - x) * t,
            y + (target.y - y) * t,
            z + (target.z - z) * t
        );
    }

    public Vec3d clamp(Vec3d min, Vec3d max) {
        return new Vec3d(
            Math.max(min.x, Math.min(max.x, x)),
            Math.max(min.y, Math.min(max.y, y)),
            Math.max(min.z, Math.min(max.z, z))
        );
    }

    public static Vec3d fromPitchYaw(float yaw, float pitch) {
        double radPitch = Math.toRadians(pitch);
        double radYaw = Math.toRadians(yaw);
        double x = -Math.sin(radYaw) * Math.cos(radPitch);
        double y = -Math.sin(radPitch);
        double z = Math.cos(radYaw) * Math.cos(radPitch);
        return new Vec3d(x, y, z);
    }

    public float toYaw() {
        return (float) Math.toDegrees(Math.atan2(-x, z));
    }

    public float toPitch() {
        return (float) Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
    }

    @Override
    public Vec3d clone() {
        return new Vec3d(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("Vec3d(%.2f, %.2f, %.2f)", x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vec3d other)) return false;
        return Double.compare(x, other.x) == 0 &&
               Double.compare(y, other.y) == 0 &&
               Double.compare(z, other.z) == 0;
    }
}