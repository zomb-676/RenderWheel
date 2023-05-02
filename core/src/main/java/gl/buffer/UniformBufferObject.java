package gl.buffer;

import org.lwjgl.opengl.GL46;

public final class UniformBufferObject extends ImmutableBuffer {

	private int bindingPoint = -1;

	private UniformBufferObject(String name, int bufferSize, int bufferUsageFlags) {
		super(name, bufferSize, bufferUsageFlags);
	}

	public static UniformBufferObject of(String name, int bufferSize, int bufferUsageFlags) {
		return new UniformBufferObject(name, bufferSize, bufferUsageFlags);
	}

	public void bindToUnifromBlock(int bindingPoint) {
		if (this.bindingPoint != bindingPoint) {
			this.bindingPoint = bindingPoint;
			this.bindingPoint(GL46.GL_UNIFORM_BUFFER, bindingPoint);
		}
	}

	public void bindToUnifromBlockRange(int bindingPoint, int offset, int size) {
		if (this.bindingPoint != bindingPoint) {
			this.bindingPoint = bindingPoint;
			this.bindPointRange(GL46.GL_UNIFORM_BUFFER, bindingPoint, offset, size);
		}
	}
}
