package keystrokesmod.script;

public class SecureClassLoader extends ClassLoader {
    public SecureClassLoader(ClassLoader parent) {
        super(parent);
    }
    public void addClass(String name, byte[] bytes) {
    }
}
