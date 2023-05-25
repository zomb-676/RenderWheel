package gl;

import org.apache.http.util.Asserts;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import renderDoc.RenderDoc;

public class Init {

	private static long winPtr = 0;

	public static long init(int width, int height) {
		if (winPtr != 0) {
			Log.LOGGER.error("don't try init multi-times");
			return winPtr;
		}

		Log.init();
		RenderDoc.isAvailable();
		var windowPtr = initGL(width, height);
		Log.logGLInfo();
		Log.registerDebug(true);
		Window.registerCallback(windowPtr);

		Init.winPtr = windowPtr;
		return windowPtr;
	}

	private static long initGL(int width, int height) {
		if (!GLFW.glfwInit()) throw new RuntimeException("failed to init");

		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_DEBUG, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

		Asserts.check(width > 0, "window width with must be positive instead of " + width);
		Asserts.check(height > 0, "window height with must be positive instead of " + height);
		var winPtr = GLFW.glfwCreateWindow(width, height, "ParticleStorm", MemoryUtil.NULL, MemoryUtil.NULL);

		if (winPtr == MemoryUtil.NULL) throw new RuntimeException("failed to init window");

		GLFW.glfwMakeContextCurrent(winPtr);
		GL.createCapabilities();

		return winPtr;
	}
}
