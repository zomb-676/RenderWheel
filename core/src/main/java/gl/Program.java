package gl;

import org.apache.http.util.Asserts;
import org.lwjgl.opengl.GL46;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class Program extends IProgram {

	private Shader vertex;
	private Shader fragment;

	private Program(String name) {
		super(name);
	}

	@Override
	protected void attachCheck(Shader shader) {
		switch (shader.getShaderType()) {
			case GL46.GL_VERTEX_SHADER -> this.vertex = shader;
			case GL46.GL_FRAGMENT_SHADER -> this.fragment = shader;
			default ->
					throw new IllegalStateException("can't attach shader:" + this.getName() + " to program, type:" + shader.getShaderType());
		}
	}

	@Override
	protected void linkCheck() {
		Asserts.notNull(vertex, "missing vertex shader for program:" + this.getName());
		Asserts.notNull(fragment, "missing fragment shader for program" + this.getName());
	}

	public void use() {
		Asserts.check(linked, "can't use an un-linked program");
		GL46.glUseProgram(this.getId());
	}

	public static Program of(Shader vertex, Shader frag, String name) {
		var program = new Program(name);
		program.attach(vertex);
		program.attach(frag);
		program.link();
		return program;
	}
}
