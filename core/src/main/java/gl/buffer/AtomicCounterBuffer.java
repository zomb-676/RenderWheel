package gl.buffer;

import gl.Log;
import lombok.Getter;
import org.lwjgl.opengl.GL46;

public final class AtomicCounterBuffer extends ImmutableBuffer {

	@Getter
	private int bindingPoint = -1;

	private AtomicCounterBuffer(String name, int bufferSize, int bufferUsageFlags) {
		super(name, bufferSize, bufferUsageFlags);
	}

	public static AtomicCounterBuffer of(String name, int bufferSize, int bufferUsageFlags) {
		return new AtomicCounterBuffer(name, bufferSize, bufferUsageFlags);
	}

	public void bindToAtomicCounter(int bindingPoint) {
		if (this.bindingPoint != bindingPoint) {
			this.bindingPoint = bindingPoint;
			this.bindingPoint(GL46.GL_ATOMIC_COUNTER_BUFFER, bindingPoint);
		}
	}

	public void bindToAtomicCounterRange(int bindingPoint, int offset, int size) {
		if (this.bindingPoint != bindingPoint) {
			this.bindingPoint = bindingPoint;
			this.bindPointRange(GL46.GL_ATOMIC_COUNTER_BUFFER, bindingPoint, offset, size);
		}
	}

}
