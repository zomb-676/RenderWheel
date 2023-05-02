package gl;

import lombok.Getter;
import org.lwjgl.opengl.GL46;

public final class RenderBufferObject extends GLObject implements FrameBufferAttachment {

	@Getter
	private final int internalFormat;
	@Getter
	private final int width;
	@Getter
	private final int height;

	private RenderBufferObject(String name, int type, int width, int height) {
		super(name);
		this.internalFormat = type;
		this.width = width;
		this.height = height;
		initId();
		initRenderBuffer();
	}

	@Override
	protected void release() {
		GL46.glDeleteFramebuffers(this.getId());
	}

	@Override
	protected int generateId() {
		return GL46.glCreateRenderbuffers();
	}

	private void initRenderBuffer() {
		GL46.glNamedRenderbufferStorage(this.getId(), internalFormat, width, height);
	}

	public static RenderBufferObject of(String name, int type, int width, int height) {
		return new RenderBufferObject(name, type, width, height);
	}

}
