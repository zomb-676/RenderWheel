package gl;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import org.jetbrains.annotations.Nullable;

public class DefaultFrameBuffer extends FrameBuffer {

	private static final DefaultFrameBuffer DEFAULT_FRAMEBUFFER = new DefaultFrameBuffer();

	public static FrameBuffer getInstance() {
		return DEFAULT_FRAMEBUFFER;
	}

	private DefaultFrameBuffer() {
		super("DefaultFrameBuffer");
	}

	@Override
	protected int generateId() {
		return 0;
	}

	@Override
	public int getHeight() {
		return Window.getInstance().getWidth();
	}

	@Override
	public int getWidth() {
		return Window.getInstance().getHeight();
	}

	@Override
	public Int2ObjectMaps.UnmodifiableMap<FrameBufferAttachment> getColors() {
		throw new UnsupportedOperationException("default framebuffer doesn't support get stencil");
	}

	@Nullable
	@Override
	public FrameBufferAttachment getStencil() {
		throw new UnsupportedOperationException("default framebuffer doesn't support get stencil");
	}

	@Nullable
	@Override
	public FrameBufferAttachment getDepth() {
		throw new UnsupportedOperationException("default framebuffer doesn't support get depth");
	}

	@Nullable
	@Override
	public FrameBufferAttachment getCombined() {
		throw new UnsupportedOperationException("default framebuffer doesn't support get combined");
	}

	@Override
	public void close() {
		Log.LOGGER.info("default framebuffer can't be closed");
	}

	@Override
	public int getId() {
		return 0;
	}


}
