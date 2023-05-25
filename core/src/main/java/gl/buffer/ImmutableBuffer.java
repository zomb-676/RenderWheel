package gl.buffer;

import lombok.Getter;
import org.apache.http.util.Asserts;
import org.checkerframework.checker.units.qual.C;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

public sealed class ImmutableBuffer extends GLBuffer permits AtomicCounterBuffer, ShaderStorageBuffer, UniformBufferObject {

	@Getter
	private final int bufferSize;

	/**
	 * @param bufferSize       the buffer size measured in bytes
	 * @param bufferUsageFlags {@link GL46#glNamedBufferStorage}
	 */
	protected ImmutableBuffer(String name, int bufferSize, int bufferUsageFlags) {
		super(name, bufferUsageFlags);
		Asserts.check(bufferSize > 0, "buffer:" + name + " must have a positive size instead of " + bufferSize);
		this.bufferSize = bufferSize;
		this.init();
	}

	private void init() {
		GL46.glNamedBufferStorage(getId(), bufferSize, this.getBufferUsageFlags());
		this.buffer = MemoryUtil.memAlloc(bufferSize);
	}

	public void bufferData(int offset) {
		GL46.glNamedBufferSubData(this.getId(), offset, this.getBuffer());
	}

	public static ImmutableBuffer of(String bufferName, int bufferSize, int bufferUsageFlags) {
		return new ImmutableBuffer(bufferName, bufferSize, bufferUsageFlags);
	}
}
