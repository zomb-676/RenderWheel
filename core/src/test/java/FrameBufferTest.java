import com.google.common.collect.ImmutableMap;
import gl.FrameBuffer;
import gl.Init;
import gl.Log;
import org.lwjgl.opengl.GL46;

public class FrameBufferTest {

	public static void main(String[] args) {
		Init.init();
		testSeparateDepthStencilSupport();
	}

	public static void testSeparateDepthStencilSupport() {
		var depths = ImmutableMap.<Integer, String>builder()
				.put(GL46.GL_DEPTH_COMPONENT16, "depth_16")
				.put(GL46.GL_DEPTH_COMPONENT32, "depth_32")
				.put(GL46.GL_DEPTH_COMPONENT32F, "depth_32F")
				.build();

		var stencils = ImmutableMap.<Integer, String>builder()
				.put(GL46.GL_STENCIL_INDEX1, "stencil_1")
				.put(GL46.GL_STENCIL_INDEX4, "stencil_4")
				.put(GL46.GL_STENCIL_INDEX8, "stencil_8")
				.put(GL46.GL_STENCIL_INDEX16, "stencil_16")
				.build();

		stencils.forEach((stencil, stencilName) -> {
			depths.forEach((depth, depthName) -> {
				var str = stencilName + " + " + depthName;
				try {
					FrameBuffer.builder("test")
							.attachNextColorRenderBuffer(GL46.GL_RGBA8)
							.attachStencilRenderBuffer(stencil)
							.attachDepthRenderBuffer(depth)
							.build();
					Log.LOGGER.info("success " + str);
				} catch (Throwable e) {
					Log.LOGGER.info("failed " + str);
					System.out.println("reason:" + e.getMessage());
				}
			});
		});
	}
}
