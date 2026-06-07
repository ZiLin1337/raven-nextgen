package keystrokesmod.utility.math;

/**
 * 4x4 Matrix for 3D transformations.
 */
public class Matrix4f {
    public float[] m = new float[16];

    public Matrix4f() {
        identity();
    }

    public Matrix4f identity() {
        for (int i = 0; i < 16; i++) m[i] = 0;
        m[0] = m[5] = m[10] = m[15] = 1;
        return this;
    }

    public Matrix4f multiply(Matrix4f other) {
        Matrix4f result = new Matrix4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += m[i + k * 4] * other.m[k + j * 4];
                }
                result.m[i + j * 4] = sum;
            }
        }
        return result;
    }

    public Vec3d transform(Vec3d v) {
        float w = m[3] * (float)v.x + m[7] * (float)v.y + m[11] * (float)v.z + m[15];
        float x = m[0] * (float)v.x + m[4] * (float)v.y + m[8] * (float)v.z + m[12];
        float y = m[1] * (float)v.x + m[5] * (float)v.y + m[9] * (float)v.z + m[13];
        float z = m[2] * (float)v.x + m[6] * (float)v.y + m[10] * (float)v.z + m[14];
        if (w != 0) { x /= w; y /= w; z /= w; }
        return new Vec3d(x, y, z);
    }

    public static Matrix4f perspective(float fov, float aspect, float near, float far) {
        Matrix4f m = new Matrix4f();
        float tanFov = (float) Math.tan(Math.toRadians(fov) / 2);
        m.m[0] = 1 / (aspect * tanFov);
        m.m[5] = 1 / tanFov;
        m.m[10] = -(far + near) / (far - near);
        m.m[11] = -1;
        m.m[14] = -2 * far * near / (far - near);
        m.m[15] = 0;
        return m;
    }

    public static Matrix4f lookAt(Vec3d eye, Vec3d center, Vec3d up) {
        Vec3d f = center.subtract(eye).normalize();
        Vec3d s = f.cross(up).normalize();
        Vec3d u = s.cross(f);
        Matrix4f m = new Matrix4f();
        m.m[0] = (float) s.x; m.m[4] = (float) s.y; m.m[8] = (float) s.z;
        m.m[1] = (float) u.x; m.m[5] = (float) u.y; m.m[9] = (float) u.z;
        m.m[2] = (float) -f.x; m.m[6] = (float) -f.y; m.m[10] = (float) -f.z;
        m.m[12] = (float) -s.dot(eye);
        m.m[13] = (float) -u.dot(eye);
        m.m[14] = (float) f.dot(eye);
        return m;
    }
}