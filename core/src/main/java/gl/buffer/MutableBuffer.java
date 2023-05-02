package gl.buffer;

import lombok.Getter;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

public final class MutableBuffer extends GLBuffer {

	@Getter
	private int bufferSize;

	private MutableBuffer(String name, int initialBufferSize, int bufferUsageFlags) {
		super(name, bufferUsageFlags);
		this.bufferSize = initialBufferSize;
		this.init();
	}

	private void init() {
		GL46.glNamedBufferData(this.getId(), this.bufferSize, this.getBufferUsageFlags());
		this.buffer = MemoryUtil.memAlloc(bufferSize);
	}

	public void bufferData(int offset) {
		bufferSize = this.getBuffer().limit() + 1;
		GL46.glNamedBufferSubData(this.getId(), offset, this.getBuffer());
	}

	public static MutableBuffer of(String bufferName, int initialBufferSize, int bufferUsageFlags) {
		return new MutableBuffer(bufferName, initialBufferSize, bufferUsageFlags);
	}

}
