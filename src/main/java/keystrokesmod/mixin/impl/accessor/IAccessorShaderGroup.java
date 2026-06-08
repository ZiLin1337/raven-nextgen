package keystrokesmod.mixin.impl.accessor;

import net.minecraft.client.gl.Shader;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.util.Identifier;

import java.util.List;

public interface IAccessorShaderGroup {
    List<Shader> getListShaders();
    void setShaders(List<Shader> shaders);
}
