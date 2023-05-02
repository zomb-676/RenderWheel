package gl;

import org.checkerframework.checker.units.qual.C;
import org.lwjgl.opengl.GL46;

public class GLConstant {
	public static class BufferBit {
		public static final int COLOR = GL46.GL_COLOR_BUFFER_BIT;
		public static final int STENCIL = GL46.GL_STENCIL_BUFFER_BIT;
		public static final int DEPTH = GL46.GL_DEPTH_BUFFER_BIT;
		public static final int COLOR_STENCIL = COLOR | STENCIL;
		public static final int COLOR_DEPTH = COLOR | DEPTH;
		public static final int SENTICL_DEPHT  = STENCIL | DEPTH;
		public static final int ALL = COLOR | STENCIL | DEPTH;
	}
}
