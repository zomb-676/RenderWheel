package gl;

import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.http.util.Asserts;
import org.intellij.lang.annotations.MagicConstant;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import static gl.Log.LOGGER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

/**
 * Shader Object
 */
@ParametersAreNonnullByDefault
public final class Shader extends GLObject {

	@Getter
	private final int shaderType;
	@Getter
	private boolean compiled = false;

	private Shader(String name, @MagicConstant(intValues = {GL_VERTEX_SHADER, GL_FRAGMENT_SHADER, GL_COMPUTE_SHADER}) int shaderType) {
		super(name);
		Asserts.check(shaderType == GL_VERTEX_SHADER || shaderType == GL_FRAGMENT_SHADER || shaderType == GL_COMPUTE_SHADER,
				"only vertex/fragment/compute shader is supported, illegal shader type:" + shaderType);
		this.shaderType = shaderType;
		initId();
	}

	@Override
	protected void release() {
		GL46.glDeleteShader(this.getId());
	}

	@Override
	protected int generateId() {
		return GL46.glCreateShader(shaderType);
	}

	/**
	 * attach shader source to this shader object
	 */
	private void setShaderSource(String source) {
		GL46.glShaderSource(this.getId(), source);
	}

	public String retrieveShaderSource() {
		return GL46.glGetShaderSource(this.getId());
	}

	/**
	 * compile this shader
	 *
	 * @throws IllegalStateException when shader source is valid
	 */
	private void compile() {
		if (this.compiled) return;

		this.compiled = true;
		GL46.glCompileShader(this.getId());
		if (GL46.glGetShaderi(this.getId(), GL46.GL_COMPILE_STATUS) != GLFW.GLFW_TRUE) {
			var descriptionStr = "failed to compile shader, name:" + this.getName();
			LOGGER.error(descriptionStr);
			var errorLog = GL46.glGetShaderInfoLog(this.getId());
			LOGGER.error(errorLog);
			throw new IllegalStateException(descriptionStr);
		} else {
			LOGGER.info("compile shader:" + this.getName() + " success");
		}
	}

	@SneakyThrows
	public static Shader of(File shaderFile) {
		var rawName = shaderFile.getName();
		int index = rawName.indexOf(".");
		var extension = rawName.substring(index + 1);
		var glType = switch (extension) {
			case "vert", "vsh" -> GL_VERTEX_SHADER;
			case "frag", "fsh" -> GL_FRAGMENT_SHADER;
			case "comp", "csh" -> GL_COMPUTE_SHADER;
			default -> throw new RuntimeException("unknown extension:" + extension);
		};
		@SuppressWarnings("MagicConstant")
		var shader = new Shader(rawName, glType);
		@Cleanup var stream = new BufferedInputStream(new FileInputStream(shaderFile));
		shader.setShaderSource(new String(stream.readAllBytes()));
		shader.compile();
		return shader;
	}
}
