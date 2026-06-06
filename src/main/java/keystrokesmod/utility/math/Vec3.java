package keystrokesmod.utility.math;

/**
 * 3D Vector class for math operations.
 */
public class Vec3 {
    public double x, y, z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3() {
        this(0, 0, 0);
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }

    public Vec3 add(double x, double y, double z) {
        return new Vec3(this.x + x, this.y + y, this.z + z);
    }

    public Vec3 subtract(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }

    public Vec3 multiply(double scalar) {
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }

    public Vec3 divide(double scalar) {
        return new Vec3(x / scalar, y / scalar, z / scalar);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public Vec3 normalize() {
        double len = length();
        if (len == 0) return new Vec3();
        return divide(len);
    }

    public double distanceTo(Vec3 other) {
        return subtract(other).length();
    }

    public double distanceSquaredTo(Vec3 other) {
        return subtract(other).lengthSquared();
    }

    public double dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }

    public Vec3 lerp(Vec3 target, double t) {
        return new Vec3(
            x + (target.x - x) * t,
            y + (target.y - y) * t,
            z + (target.z - z) * t
        );
    }

    public Vec3 clamp(Vec3 min, Vec3 max) {
        return new Vec3(
            Math.max(min.x, Math.min(max.x, x)),
            Math.max(min.y, Math.min(max.y, y)),
            Math.max(min.z, Math.min(max.z, z))
        );
    }

    public static Vec3 fromPitchYaw(float yaw, float pitch) {
        double radPitch = Math.toRadians(pitch);
        double radYaw = Math.toRadians(yaw);
        double x = -Math.sin(radYaw) * Math.cos(radPitch);
        double y = -Math.sin(radPitch);
        double z = Math.cos(radYaw) * Math.cos(radPitch);
        return new Vec3(x, y, z);
    }

    public float toYaw() {
        return (float) Math.toDegrees(Math.atan2(-x, z));
    }

    public float toPitch() {
        return (float) Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
    }

    @Override
    public Vec3 clone() {
        return new Vec3(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("Vec3(%.2f, %.2f, %.2f)", x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vec3 other)) return false;
        return Double.compare(x, other.x) == 0 &&
               Double.compare(y, other.y) == 0 &&
               Double.compare(z, other.z) == 0;
    }
}