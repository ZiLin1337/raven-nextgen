package keystrokesmod.script;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class JavaSourceFromString extends SimpleJavaFileObject {
    private final String code;
    
    public JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }
    
    public JavaSourceFromString(String name, String code, int startingLine) {
        this(name, code);
    }
    
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
