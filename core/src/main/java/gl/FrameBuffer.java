package gl;

import gl.texture.GLTexture2D;
import gl.texture.GLTexture2DSetting;
import gl.texture.TextureData2D;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps.UnmodifiableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.util.Asserts;
import org.lwjgl.opengl.GL46;

import javax.annotation.Nullable;
import java.util.Arrays;

public class FrameBuffer extends GLObject {

	private static final int FRAMEBUFFER = GL46.GL_FRAMEBUFFER;
	private static final int READ_FRAMEBUFFER = GL46.GL_READ_FRAMEBUFFER;
	private static final int DRAW_FRAMEBUFFER = GL46.GL_DRAW_FRAMEBUFFER;
	private static final int MAX_COLOR_ATTACHMENT = GL46.glGetInteger(GL46.GL_MAX_COLOR_ATTACHMENTS);

	@Getter
	private final Int2ObjectMaps.UnmodifiableMap<FrameBufferAttachment> colors;
	@Nullable
	@Getter
	private final FrameBufferAttachment stencil;
	@Nullable
	@Getter
	private final FrameBufferAttachment depth;
	@Nullable
	@Getter
	private final FrameBufferAttachment combined;
	@Getter
	private final int width;
	@Getter
	private final int height;
	private final int[] drawBuffers = new int[MAX_COLOR_ATTACHMENT];

	protected FrameBuffer(String name, UnmodifiableMap<FrameBufferAttachment> colors, @Nullable FrameBufferAttachment stencil, @Nullable FrameBufferAttachment depth, @Nullable FrameBufferAttachment combined, int width, int height) {
		super(name);
		this.colors = colors;
		this.depth = depth;
		this.stencil = stencil;
		this.combined = combined;
		this.width = width;
		this.height = height;
		this.initId();
	}

	protected FrameBuffer(String name) {
		super(name);
		this.colors = null;
		this.depth = null;
		this.stencil = null;
		this.combined = null;
		this.width = 0;
		this.height = 0;
		this.initId();
	}

	@Override
	protected void release() {
		GL46.glDeleteFramebuffers(this.getId());
	}

	@Override
	protected int generateId() {
		return GL46.glCreateFramebuffers();
	}

	private void checkStatus() {
		var status = GL46.glCheckNamedFramebufferStatus(this.getId(), FRAMEBUFFER);
		if (status == GL46.GL_FRAMEBUFFER_COMPLETE) return;
		var error = switch (status) {
			case GL46.GL_FRAMEBUFFER_UNDEFINED -> "GL_FRAMEBUFFER_UNDEFINED";
			case GL46.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
			case GL46.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
			case GL46.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER -> "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER";
			case GL46.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER -> "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER";
			case GL46.GL_FRAMEBUFFER_UNSUPPORTED -> "GL_FRAMEBUFFER_UNSUPPORTED";
			case GL46.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE -> "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE";
			case GL46.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS -> "GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS";
			default -> "unknown status";
		};
		Log.LOGGER.error("framebuffer status error:" + error + ",name:" + this.getName());
		throw new RuntimeException(error);
	}

	private void attachTexture(int attachmentType, GLTexture2D texture, int level) {
		GL46.glNamedFramebufferTexture(this.getId(), attachmentType, texture.getId(), level);
	}

	private void attachRenderBuffer(int attachmentType, RenderBufferObject renderBuffer) {
		GL46.glNamedFramebufferRenderbuffer(this.getId(), attachmentType, GL46.GL_RENDERBUFFER, renderBuffer.getId());
	}

	public void bindBoth() {
		GL46.glBindFramebuffer(FRAMEBUFFER, this.getId());
	}

	public void bindRead() {
		GL46.glBindFramebuffer(READ_FRAMEBUFFER, this.getId());
	}

	public void bindDraw() {
		GL46.glBindFramebuffer(DRAW_FRAMEBUFFER, this.getId());
	}

	/**
	 * @param mask {@link GLConstant.BufferBit}
	 */
	public void blitTo(FrameBuffer to, int mask) {
		GL46.glBlitNamedFramebuffer(this.getId(), to.getId(), 0, 0,
				width, height, 0, 0, to.width, to.height, mask, GL46.GL_LINEAR);
	}

	public void drawBuffers(int... buffers) {
		if (!Arrays.equals(buffers, drawBuffers)) {
			Asserts.check(buffers.length <= MAX_COLOR_ATTACHMENT,
					"MAX_COLOR_ATTACHMENT is " + MAX_COLOR_ATTACHMENT + " now use " + buffers.length);
			for (int index = 0; index < buffers.length - 1; index++) {
				var buffer = drawBuffers[index];
				if (buffer >= GL46.GL_DRAW_BUFFER0) {
					Asserts.check(buffer <= GL46.GL_DRAW_BUFFER0 + MAX_COLOR_ATTACHMENT - 1,
							"draw buffer must between GL_DRAW_BUFFER0 and GL_DRAW_BUFFER" + (MAX_COLOR_ATTACHMENT - 1));
					drawBuffers[index] = buffer;
				} else {
					Asserts.check(buffer <= MAX_COLOR_ATTACHMENT,
							"draw buffer must between 0 and " + (MAX_COLOR_ATTACHMENT - 1));
					drawBuffers[index] = buffer + GL46.GL_DRAW_BUFFER0;
				}
			}
		}
		GL46.glNamedFramebufferDrawBuffers(this.getId(), drawBuffers);
	}

	public static FrameBufferBuilder builder(String name, int width, int height) {
		return new FrameBufferBuilder(name, width, height);
	}

	public static FrameBufferBuilder builder(String name) {
		var window = Window.getInstance();
		return new FrameBufferBuilder(name, window.getWidth(), window.getHeight());
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class FrameBufferBuilder {
		private final String name;
		private final int width;
		private final int height;
		private final Int2ObjectMap<FrameBufferAttachment> colors = new Int2ObjectOpenHashMap<>(0);
		@Nullable
		private FrameBufferAttachment depth;
		@Nullable
		private FrameBufferAttachment stencil;
		@Nullable
		private FrameBufferAttachment combined;

		public FrameBuffer build() {
			Asserts.check(colors.size() >= 1, "frameBuffer must have at least one color attachment");

			var framebuffer = new FrameBuffer(name, (UnmodifiableMap<FrameBufferAttachment>)
					Int2ObjectMaps.unmodifiable(colors), stencil, depth, combined, width, height);
			colors.forEach((index, texture) -> {
				if (texture instanceof GLTexture2D colorTexture) {
					framebuffer.attachTexture(GL46.GL_COLOR_ATTACHMENT0 + index, colorTexture, 0);
				} else if (texture instanceof RenderBufferObject colorRenderBuffer) {
					framebuffer.attachRenderBuffer(GL46.GL_COLOR_ATTACHMENT0 + index, colorRenderBuffer);
				} else
					throw new RuntimeException("unknown color implementation, must be GLTexture2D or RenderBufferObject");
				framebuffer.checkStatus();
			});
			if (stencil != null && combined != null)
				Log.LOGGER.error("both set stencil and depth separately may be no supported and produce GL_FRAMEBUFFER_UNSUPPORTED");
			if (depth != null) {
				if (depth instanceof GLTexture2D depthTexture) {
					framebuffer.attachTexture(GL46.GL_DEPTH_ATTACHMENT, depthTexture, 0);
				} else if (depth instanceof RenderBufferObject depthRenderBuffer) {
					framebuffer.attachRenderBuffer(GL46.GL_DEPTH_ATTACHMENT, depthRenderBuffer);
				} else
					throw new RuntimeException("unknown depth implementation, must be GLTexture2D or RenderBufferObject");
				framebuffer.checkStatus();
			}
			if (stencil != null) {
				if (stencil instanceof GLTexture2D stencilTexture) {
					framebuffer.attachTexture(GL46.GL_STENCIL_ATTACHMENT, stencilTexture, 0);
				} else if (stencil instanceof RenderBufferObject stencilRenderBuffer) {
					framebuffer.attachRenderBuffer(GL46.GL_STENCIL_ATTACHMENT, stencilRenderBuffer);
				} else
					throw new RuntimeException("unknown stencil implementation, must be GLTexture2D or RenderBufferObject");
				framebuffer.checkStatus();
			}
			if (combined != null) {
				if (combined instanceof GLTexture2D combinedTexture) {
					framebuffer.attachTexture(GL46.GL_DEPTH_STENCIL_ATTACHMENT, combinedTexture, 0);
				} else if (combined instanceof RenderBufferObject combinedRenderBuffer) {
					framebuffer.attachRenderBuffer(GL46.GL_DEPTH_STENCIL_ATTACHMENT, combinedRenderBuffer);
				} else
					throw new RuntimeException("unknown combined implementation, must be GLTexture2D or RenderBufferObject");
				framebuffer.checkStatus();
			}

			framebuffer.checkStatus();
			return framebuffer;
		}

		public FrameBufferBuilder attachStencilTexture(int stencilType) {
			checkStencil(stencilType);
			stencil = GLTexture2D.ofEmpty(name + "_stencil", GLTexture2DSetting.BEST_SKIP_MIPMAP, TextureData2D.ofType(width, height, stencilType));
			return this;
		}

		private void checkStencil(int stencilType) {
			Asserts.check(stencil == null, "can't attach stencil multi-times");
			Asserts.check(combined == null, "can't attach stencil with combined attached");
			Asserts.check(FrameBufferAttachment.isStencil(stencilType), "unknown stencil type:" + stencilType);
		}

		public FrameBufferBuilder attachStencilRenderBuffer(int stencilType) {
			checkStencil(stencilType);
			stencil = RenderBufferObject.of(name + "_stencil", stencilType, width, height);
			return this;
		}

		private void checkDepth(int depthType) {
			Asserts.check(depth == null, "can't attach depth multi-times");
			Asserts.check(combined == null, "can't attach depth with combined attached");
			Asserts.check(FrameBufferAttachment.isDepth(depthType), "unknown depth type" + depthType);
		}

		public FrameBufferBuilder attachDepthTexture(int depthType) {
			checkDepth(depthType);
			depth = GLTexture2D.ofEmpty(name + "_depth", GLTexture2DSetting.BEST_SKIP_MIPMAP, TextureData2D.ofType(width, height, depthType));
			return this;
		}

		public FrameBufferBuilder attachDepthRenderBuffer(int depthType) {
			checkDepth(depthType);
			depth = RenderBufferObject.of(name + "_depth", depthType, width, height);
			return this;
		}

		private void checkCombined(int combinedType) {
			Asserts.check(stencil == null, "can't attach combined with stencil attached");
			Asserts.check(depth == null, "can't attach combined with depth attached");
			Asserts.check(FrameBufferAttachment.isCombined(combinedType), "unknown combined type" + combinedType);
		}

		public FrameBufferBuilder combinedTexture(int combinedType) {
			checkCombined(combinedType);
			combined = GLTexture2D.ofEmpty(name + "_combined", GLTexture2DSetting.BEST_SKIP_MIPMAP, TextureData2D.ofType(width, height, combinedType));
			return this;
		}

		public FrameBufferBuilder combinedRenderBuffer(int combinedType) {
			checkCombined(combinedType);
			combined = RenderBufferObject.of(name + "_combined", combinedType, width, height);
			return this;
		}

		private int nextColorAttachIndex() {
			int next = colors.size();
			while (colors.containsKey(next)) next++;
			return next;
		}

		public FrameBufferBuilder attachColor(FrameBufferAttachment texture, int attachIndex) {
			attachIndex -= attachIndex >= GL46.GL_COLOR_ATTACHMENT0 ? GL46.GL_COLOR_ATTACHMENT0 : 0;
			Asserts.check(!colors.containsKey(attachIndex), "attach index " + attachIndex + " has already be used");
			Asserts.check(attachIndex >= 0 && attachIndex <= MAX_COLOR_ATTACHMENT, "attach index should between 0 and " + (MAX_COLOR_ATTACHMENT - 1) + " " + "or GL_COLOR_ATTACHMENT0 and GL_COLOR_ATTACHMENT" + (MAX_COLOR_ATTACHMENT - 1));
			colors.put(attachIndex, texture);
			return this;
		}

		public FrameBufferBuilder attachNextColor(FrameBufferAttachment texture) {
			return attachColor(texture, nextColorAttachIndex());
		}

		public FrameBufferBuilder attachNextColorTexture(int textureStyle) {
			return attachColorTexture(textureStyle, nextColorAttachIndex());
		}

		public FrameBufferBuilder attachColorTexture(int textureStyle, int attachIndex) {
			var texture = GLTexture2D.ofEmpty(name + "_color", GLTexture2DSetting.BEST_SKIP_MIPMAP, TextureData2D.ofType(width, height, textureStyle));
			return attachColor(texture, attachIndex);
		}

		public FrameBufferBuilder attachNextColorRenderBuffer(int textureStyle) {
			return attachColorRenderBuffer(textureStyle, nextColorAttachIndex());
		}

		public FrameBufferBuilder attachColorRenderBuffer(int textureStyle, int attachIndex) {
			var texture = RenderBufferObject.of(name + "_color", textureStyle, width, height);
			return attachColor(texture, attachIndex);
		}
	}
}
