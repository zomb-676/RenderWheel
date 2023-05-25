import client.Attributes;
import gl.*;
import gl.buffer.MutableBuffer;
import gl.texture.GLTexture2D;
import gl.texture.GLTexture2DSetting;
import gl.texture.TextureData2D;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL46;
import resource.ResourceLoader;

public class ComputeShaderTest {
	public static void main(String[] args) {
		var windowPtr = Init.init(1920,1080);

		var positionPosVert = Shader.of(ResourceLoader.ofShaderFile("position_uv.vert"));
		var positionPosFrag = Shader.of(ResourceLoader.ofShaderFile("position_uv.frag"));
		var program = Program.of(positionPosVert, positionPosFrag, "position_pos");

		var buffer = MutableBuffer.of("buffer", GlType.FLOAT.typeByteSize(128), GL46.GL_STREAM_READ);

		var vao = VertexArrayObject.of("mainVAO");

		Attributes.POSITION3_UV2.setup(vao, buffer, 0, 0);

		buffer.getBuffer().asFloatBuffer().put(new float[]{
				-0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
				-0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
				0.5f, 0.5f, 0.0f, 1.0f, 1.0f,
				0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
		}).flip();

		var index = MutableBuffer.of("index", GlType.INT.typeByteSize(100), GL46.GL_STREAM_DRAW);
		index.getBuffer().asIntBuffer().put(new int[]{0, 1, 3, 1, 2, 3}).flip();

		buffer.bufferData(0);
		index.bufferData(0);

		var texture = GLTexture2D.of("test.bmp", GLTexture2DSetting.BEST, ResourceLoader.ofTexture("test.bmp"));
		texture.bindToTextureUnit(0);

		var testTexture = GLTexture2D.ofEmpty("test", GLTexture2DSetting.PIXEL,
				TextureData2D.ofNullData(255, 255, 4));

		var cs = ComputeProgram.of(ResourceLoader.ofShaderFile("test.comp"));
		cs.setGroups(255, 255, 255);
		cs.use();
		GL46.glFlush();

		var frameBuffer1 = FrameBuffer.builder("main")
				.attachNextColorRenderBuffer(GL46.GL_RGBA8)
				.attachDepthTexture(FrameBufferAttachment.DEPTH_COMPONENT32)
				.build();

		var context = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_DEBUG);

		NVGColor color = NVGColor.malloc();
		color.r(0);
		color.g(0);
		color.b(100);
		color.a(255);

		while (!GLFW.glfwWindowShouldClose(windowPtr)) {
			Log.checkError();
			frameBuffer1.bindBoth();
			GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,0);
			GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_STENCIL_BUFFER_BIT);
//
			testTexture.bingToImage(0);
			testTexture.bindToTextureUnit(0);
			var v = System.currentTimeMillis() % 2000 / 1000.0f;
			cs.getUniform().uniformFloat1("time", (float) Math.abs(v - 1.0));
			cs.use();

			program.use();
			program.getUniform().uniformInt1("texture", 0);
			vao.bind();
			vao.bindIndexBuffer(index);
			GL46.glDrawElements(GL46.GL_TRIANGLES, 6, GL46.GL_UNSIGNED_INT, 0);

			var window = Window.getInstance();
			var width = window.getWidth();
			var height = window.getHeight();

			NanoVG.nvgBeginFrame(context,width,height,1);
			NanoVG.nvgBeginPath(context);
			NanoVG.nvgRect(context, 100, 100, 100, 100);

			NanoVG.nvgText(context,200,200,"Thonk");
			NanoVG.nvgFillColor(context, color);
			NanoVG.nvgFill(context);

			NanoVG.nvgEndFrame(context);

			frameBuffer1.blitTo(DefaultFrameBuffer.getInstance(),GLConstant.BufferBit.ALL);

			GLFW.glfwSwapBuffers(windowPtr);
			GLFW.glfwPollEvents();
		}

		GLFW.glfwTerminate();

	}


}
