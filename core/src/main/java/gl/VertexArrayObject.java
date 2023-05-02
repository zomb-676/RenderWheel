package gl;

import gl.buffer.GLBuffer;
import org.lwjgl.opengl.GL46;

/**
 * Vertex Array Object(VAO)
 */
public final class VertexArrayObject extends GLObject {

	private GLBuffer indexBuffer;

	private VertexArrayObject(String name) {
		super(name);
		initId();
	}

	@Override
	protected void release() {
		GL46.glDeleteVertexArrays(this.getId());
	}

	@Override
	protected int generateId() {
		return GL46.glCreateVertexArrays();
	}

	public void bind() {
		GL46.glBindVertexArray(this.getId());
	}

	public static VertexArrayObject of(String vaoName) {
		return new VertexArrayObject(vaoName);
	}

	public void setupAttributes(VertexAttribute attribute1, GLBuffer buffer1) {
		attribute1.setup(this, buffer1, 0, 0);
	}

	public void setupAttributes(VertexAttribute attribute1, GLBuffer buffer1, VertexAttribute attribute2, GLBuffer buffer2) {
		attribute1.setup(this, buffer1, 0, 0);
		attribute2.setup(this, buffer2, 1, attribute1.getCount());
	}

	public void setupAttributes(VertexAttribute attribute1, GLBuffer buffer1, VertexAttribute attribute2, GLBuffer buffer2, VertexAttribute attribute3, GLBuffer buffer3) {
		attribute1.setup(this, buffer1, 0, 0);
		attribute2.setup(this, buffer2, 1, attribute1.getCount());
		attribute3.setup(this, buffer3, 2, attribute1.getCount() + attribute2.getCount());
	}

	public void bindIndexBuffer(GLBuffer indexBuffer) {
		if (this.indexBuffer != indexBuffer) {
			this.indexBuffer = indexBuffer;
			GL46.glVertexArrayElementBuffer(this.getId(), indexBuffer.getId());
		}
	}

}
