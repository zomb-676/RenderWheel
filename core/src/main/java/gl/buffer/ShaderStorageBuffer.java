package gl.buffer;

import org.lwjgl.opengl.GL46;

public final class ShaderStorageBuffer extends ImmutableBuffer {

	private int bindingPoint = -1;

	private ShaderStorageBuffer(String name, int bufferSize, int bufferUsageFlags) {
		super(name, bufferSize, bufferUsageFlags);
	}


	public static ShaderStorageBuffer of(String name, int bufferSize, int bufferUsageFlags) {
		return new ShaderStorageBuffer(name, bufferSize, bufferUsageFlags);
	}

	public void bindToSSBO(int bindingPoint) {
		if (this.bindingPoint != bindingPoint) {
			this.bindingPoint = bindingPoint;
			this.bindingPoint(GL46.GL_SHADER_STORAGE_BUFFER, bindingPoint);
		}
	}

	public void bindToSSBORange(int bindingPoint, int offset, int size) {
		if (this.bindingPoint != bindingPoint) {
			this.bindingPoint = bindingPoint;
			this.bindPointRange(GL46.GL_SHADER_STORAGE_BUFFER, bindingPoint, offset, size);
		}
	}


}
