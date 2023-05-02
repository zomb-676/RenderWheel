package gl;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import org.apache.http.util.Asserts;
import org.lwjgl.opengl.GL46;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.function.ToIntFunction;

/**
 * the common class for program
 */
@ParametersAreNonnullByDefault
public abstract sealed class IProgram extends GLObject permits ComputeProgram, Program {

	/**
	 * flag filed
	 */
	protected boolean linked = false;
	@Getter
	private final UniformCache uniform = new UniformCache(this);
	protected final Object2IntMap<String> uboNameIndexMap = new Object2IntOpenHashMap<>(0);
	protected final HashMap<String, Object2IntMap<String>> unoOffset = new HashMap<>(0);

	protected final Object2IntMap<String> ssboNameIndexMap = new Object2IntOpenHashMap<>(0);

	protected IProgram(String name) {
		super(name);
		initId();
	}

	@Override
	protected final void release() {
		GL46.glDeleteProgram(this.getId());
		uniform.close();
	}

	@Override
	protected final int generateId() {
		return GL46.glCreateProgram();
	}

	/**
	 * attach shader to this program
	 */
	protected final void attach(Shader shader) {
		Asserts.check(shader.isCompiled(), "can't attach an un-compiled shader:" + shader.getName() + " to program:" + this.getName());
		Asserts.check(!this.linked, "can't attach a shader:" + shader.getName() + " to an already linked program:" + this.getName());
		this.attachCheck(shader);
		GL46.glAttachShader(this.getId(), shader.getId());
	}

	protected abstract void attachCheck(Shader shader);

	/**
	 * link this program
	 */
	protected final void link() {
		Asserts.check(!this.linked, "can't link an already linked program:" + this.getName());
		this.linkCheck();
		GL46.glLinkProgram(this.getId());
		this.linked = true;
	}

	protected abstract void linkCheck();

	public abstract void use();

	// Uniform Buffer Object
	public int getUnifromBlockIndex(String bufferName) {
		return uboNameIndexMap.computeIfAbsent(bufferName, new ToIntFunction<>() {
			@Override
			public int applyAsInt(String name) {
				var index = GL46.glGetUniformBlockIndex(getId(), name);
				Asserts.check(index != -1, "can't get ubo called:" + name + " in program:" + getName());
				return index;
			}
		});
	}

	/**
	 * @param uniformBlockIndex can be gotten by {@link #getUnifromBlockIndex(String)}
	 */
	public void bindToUniformBlock(int uniformBlockIndex, int uniformBlockBindingPoint) {
		GL46.glUniformBlockBinding(this.getId(), uniformBlockIndex, uniformBlockBindingPoint);
	}

	public int getUniformBlockIndex(String uniformBlockName) {
		return GL46.glGetUniformBlockIndex(this.getId(), uniformBlockName);
	}

	public int queryUBOoffsets(String uniformBlockName, String uniformName) {
		var offsets = unoOffset.computeIfAbsent(uniformBlockName, blockName -> {
			int id = this.getId();
			var uniformBlockIndex = this.getUniformBlockIndex(blockName);
			var uniformCounts = GL46.glGetActiveUniformBlocki(id, uniformBlockIndex, GL46.GL_UNIFORM_BLOCK_ACTIVE_UNIFORMS);
			var map = new Object2IntOpenHashMap<String>(uniformCounts);
			for (int index = 1; index < uniformCounts; index++) {
				var uniformIndex = GL46.glGetActiveUniformBlocki(id, uniformBlockIndex, GL46.GL_UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES);
				var name = GL46.glGetActiveUniformName(id, uniformIndex);
				var offset = GL46.glGetActiveUniformsi(id, uniformIndex, GL46.GL_UNIFORM_OFFSET);
				map.put(name, offset);
			}
			return map;
		});
		Asserts.check(offsets.containsKey(uniformName), "can't get uniform:" + uniformName + " from ubo:" + uniformBlockName + " in program:" + this.getName());
		return offsets.getInt(uniformBlockName);
	}

	//SSBO
	public int getSSBOindex(String ssboName) {
		return ssboNameIndexMap.computeIfAbsent(ssboName, new ToIntFunction<String>() {
			@Override
			public int applyAsInt(String value) {
				var index = GL46.glGetProgramResourceIndex(getId(), GL46.GL_SHADER_STORAGE_BLOCK, ssboName);
				Asserts.check(index != -1, "can't get ssbo:" + value + "from program:" + getName());
				return index;
			}
		});
	}

	public void bindToSSB(int storageBlockIndex, int shaderStorageBindingPoint) {
		GL46.glShaderStorageBlockBinding(this.getId(), storageBlockIndex, shaderStorageBindingPoint);
	}


}
