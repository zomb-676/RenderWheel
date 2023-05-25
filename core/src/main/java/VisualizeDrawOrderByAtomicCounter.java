import client.Attributes;
import gl.*;
import gl.buffer.AtomicCounterBuffer;
import gl.buffer.MutableBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import resource.ResourceLoader;

public class VisualizeDrawOrderByAtomicCounter {
	public static void main(String[] args) {
		var windowPtr = Init.init(2560,1440);

		var positionPosVert = Shader.of(ResourceLoader.ofShaderFile("position.vert"));
		var positionPosFrag = Shader.of(ResourceLoader.ofShaderFile("position.frag"));
		var program = Program.of(positionPosVert, positionPosFrag, "position");

		var buffer = MutableBuffer.of("buffer", GlType.FLOAT.typeByteSize(128), GL46.GL_STREAM_READ);

		var vao = VertexArrayObject.of("mainVAO");

		Attributes.POSITION3.single().setup(vao, buffer, 0, 0);

		buffer.getBuffer().asFloatBuffer().put(new float[]{
				-1f, -1f, 0.0f,
				-1f, 1f, 0.0f,
				1f, 1f, 0.0f,
				1f, -1f, 0.0f,
		}).flip();

		var index = MutableBuffer.of("index", GlType.INT.typeByteSize(100), GL46.GL_STREAM_DRAW);
		index.getBuffer().asIntBuffer().put(new int[]{1, 2, 0, 0, 2, 3}).flip();

		index.bufferData(0);
		buffer.bufferData(0);

		Log.checkError();


		var counter = AtomicCounterBuffer.of("counter",GlType.INT.typeByteSize(1),GL46.GL_DYNAMIC_STORAGE_BIT);

		counter.bindToAtomicCounter(0);

		buffer.bufferData(0);

		Log.checkError();

		while (!GLFW.glfwWindowShouldClose(windowPtr)) {
			GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_STENCIL_BUFFER_BIT);

			program.use();

			vao.bind();
			vao.bindIndexBuffer(index);

			program.getUniform().uniformInt1("width",Window.getInstance().getWidth());
			program.getUniform().uniformInt1("height",Window.getInstance().getHeight());

			counter.getBuffer().asIntBuffer().put(0).flip();
			counter.bufferData(0);

			GL46.glDrawElements(GL46.GL_TRIANGLES,6,GL46.GL_UNSIGNED_INT,0);

			GLFW.glfwSwapBuffers(windowPtr);
			GLFW.glfwPollEvents();
		}

		GLFW.glfwTerminate();

	}


}
