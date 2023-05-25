package gl.buffer;

import gl.GLObject;
import lombok.Getter;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * Vertex Buffer Object
 */
public abstract sealed class GLBuffer extends GLObject permits ImmutableBuffer, MutableBuffer {

	@Getter
	private final int bufferUsageFlags;
	@Getter
	protected ByteBuffer buffer;

	protected GLBuffer(String name, int bufferUsageFlags) {
		super(name);
		this.bufferUsageFlags = bufferUsageFlags;
		initId();
	}

	@Override
	protected final void release() {
		GL46.glDeleteBuffers(getId());
		MemoryUtil.memFree(buffer);
	}

	@Override
	protected final int generateId() {
		return GL46.glCreateBuffers();
	}

	protected final void bindingPoint(int targetType, int bindingPoint) {
		GL46.glBindBufferBase(targetType, bindingPoint, this.getId());
	}

	protected final void bindPointRange(int targetType, int bindingPoint, int offset, int size) {
		GL46.glBindBufferRange(targetType, bindingPoint, this.getId(), offset, size);
	}

	abstract int getBufferSize();

	public void clear(int internalFormat, int format, int type, ByteBuffer data) {
		GL46.glClearNamedBufferData(this.getId(), internalFormat, format, type, data);
	}

	public void copyDataFrom(GLBuffer buffer, int readOffset, int writeOffset, int size) {
		GL46.glCopyNamedBufferSubData(buffer.getId(), this.getId(), readOffset, writeOffset, size);
	}

	/**
	 * @param access {@link GL46#glMapNamedBuffer}
	 */
	public ByteBuffer map(int access) {
		return GL46.glMapNamedBuffer(this.getId(), access);
	}

	public boolean unmap() {
		return GL46.glUnmapNamedBuffer(this.getId());
	}

	public void invalidate() {
		GL46.glInvalidateBufferData(this.getId());
	}


}
