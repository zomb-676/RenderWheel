package gl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.lwjgl.opengl.GL46;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GlType {

	INT(Integer.BYTES, Integer.SIZE, GL46.GL_INT, "Int"),
	UNSIGNED_INT(Integer.BYTES, Integer.SIZE, GL46.GL_UNSIGNED_INT, "Unsigned Int"),
	BYTE(Byte.BYTES, Byte.SIZE, GL46.GL_BYTE, "Byte"),
	UNSIGNED_BYTE(Byte.BYTES, Byte.SIZE, GL46.GL_UNSIGNED_BYTE, "Unsigned Byte"),
	SHORT(Short.BYTES, Short.SIZE, GL46.GL_SHORT, "Short"),
	UNSIGNED_SHORT(Short.BYTES, Short.SIZE, GL46.GL_UNSIGNED_SHORT, "Unsigned Short"),
	FLOAT(Float.BYTES, Float.SIZE, GL46.GL_FLOAT, "Float"),
	DOUBLE(Double.BYTES, Double.SIZE, GL46.GL_DOUBLE, "Double");

	final int size;
	final int bitSize;
	final int glType;
	final String name;

	public int typeSize(int count) {
		return count * size;
	}

	public int typeBitSize(int count) {
		return count * bitSize;
	}

}
