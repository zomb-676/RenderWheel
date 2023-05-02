package gl;

import it.unimi.dsi.fastutil.ints.IntList;
import org.lwjgl.opengl.GL46;

public interface FrameBufferAttachment {
	int getId();

	int getInternalFormat();

	int getWidth();

	int getHeight();

	int DEPTH_COMPONENT16 = GL46.GL_DEPTH_COMPONENT16;
	int DEPTH_COMPONENT24 = GL46.GL_DEPTH_COMPONENT24;
	int DEPTH_COMPONENT32 = GL46.GL_DEPTH_COMPONENT32;
	int DEPTH_COMPONENT32F = GL46.GL_DEPTH_COMPONENT32F;
	IntList DEPTH_TYPES = IntList.of(DEPTH_COMPONENT16, DEPTH_COMPONENT24, DEPTH_COMPONENT32, DEPTH_COMPONENT32F);

	//	int STENCIL1 = GL46.GL_STENCIL_INDEX1;
	//	int STENCIL4 = GL46.GL_STENCIL_INDEX4;
	int STENCIL8 = GL46.GL_STENCIL_INDEX8;
	//	int STENCIL16 = GL46.GL_STENCIL_INDEX16;
	IntList STENCIL_TYPES = IntList.of(STENCIL8);

	int DEPTH24_STENCIL8 = GL46.GL_DEPTH24_STENCIL8;
	int DEPTH32_STENCIL8 = GL46.GL_DEPTH32F_STENCIL8;

	static boolean isDepth(int depthType) {
		return DEPTH_TYPES.contains(depthType);
	}

	static boolean isStencil(int stencilType) {
		return STENCIL_TYPES.contains(stencilType);
	}

	static boolean isCombined(int combinedType) {
		return combinedType == DEPTH24_STENCIL8 || combinedType == DEPTH32_STENCIL8;
	}
}
