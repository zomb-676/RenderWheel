package gl;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.util.Asserts;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

/**
 * the object represents a program which only contains a computer shader
 */
@ParametersAreNonnullByDefault
public final class ComputeProgram extends IProgram {

	/**
	 * the contained shader
	 */
	@Getter
	private Shader computeShader;

	@Setter
	@Getter
	private int groupX = 1;
	@Setter
	@Getter
	private int groupY = 1;
	@Setter
	@Getter
	private int groupZ = 1;
	@Getter
	private int localX;
	@Getter
	private int localY;
	@Getter
	private int localZ;

	private ComputeProgram(String name) {
		super(name);
	}

	@Override
	protected void attachCheck(Shader shader) {
		Asserts.check(shader.getShaderType() == GL_COMPUTE_SHADER, "only compute shader can bed attached to compute program");
		this.computeShader = shader;
	}

	@Override
	protected void linkCheck() {
		Asserts.notNull(computeShader, "can't link program while compute shader is null");
	}

	@Override
	public void use() {
		Asserts.check(this.linked, "can't use an un-linked compute program");
		GL46.glUseProgram(this.getId());
		GL46.glDispatchCompute(this.groupX, this.groupY, this.groupZ);
	}

	public void setGroups(int x, int y, int z) {
		Asserts.check(x > 0, "work group x " + x + "must >= 1 , program:" + this.getName());
		Asserts.check(y > 0, "work group y " + y + "must >= 1 , program:" + this.getName());
		Asserts.check(z > 0, "work group z " + z + "must >= 1 , program:" + this.getName());
		this.groupX = x;
		this.groupY = y;
		this.groupZ = z;
	}

	public static ComputeProgram of(Shader computeShader) {
		Asserts.check(computeShader.getShaderType() == GL_COMPUTE_SHADER, "compute program only receive compute shader");
		Asserts.check(computeShader.isCompiled(), "can't attach an un-compiled shader");

		var computeProgram = new ComputeProgram(computeShader.getName() + "Program");
		computeProgram.attach(computeShader);
		computeProgram.link();

		@Cleanup MemoryStack stack = MemoryStack.stackPush();
		var size = stack.mallocInt(3);
		GL46.glGetProgramiv(computeProgram.getId(), GL46.GL_COMPUTE_WORK_GROUP_SIZE, size);
		computeProgram.localX = size.get();
		computeProgram.localY = size.get();
		computeProgram.localZ = size.get();

		return computeProgram;
	}

	public static ComputeProgram of(File shaderFile) {
		return of(Shader.of(shaderFile));
	}
}
