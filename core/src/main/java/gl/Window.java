package gl;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import renderDoc.RenderDoc;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Window {
	private static Window INSTANCE = new Window();
	@Getter
	private int width;
	@Getter
	private int height;
	@Getter
	private int posX;
	@Getter
	private int posY;

	public static Window getInstance() {
		return INSTANCE;
	}

	@SuppressWarnings("resource")
	public static void registerCallback(long windowPtr) {
		var instance = getInstance();

		@Cleanup MemoryStack memoryStack = MemoryStack.stackPush();
		var intBuffer = memoryStack.mallocInt(2);
		long address = MemoryUtil.memAddress(intBuffer);

		GLFW.glfwSetWindowSizeCallback(windowPtr, (window, width, height) -> {
			instance.width = width;
			instance.height = height;
		});

		GLFW.nglfwGetWindowSize(windowPtr, address, address + Integer.BYTES);
		instance.width = intBuffer.get(0);
		instance.height = intBuffer.get(1);

		GLFW.glfwSetWindowPosCallback(windowPtr, (window, posX, posY) -> {
			instance.posX = posX;
			instance.posY = posY;
		});

		GLFW.nglfwGetWindowPos(windowPtr, address, address + Integer.BYTES);
		instance.posX = intBuffer.get(0);
		instance.posY = intBuffer.get(1);

		GLFW.glfwSetKeyCallback(windowPtr, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_M && action == GLFW.GLFW_PRESS) {
				Log.LOGGER.info("launch RenderDoc UI");
				RenderDoc.launchReplayUI(true);
			}
			if (key == GLFW.GLFW_KEY_ESCAPE) {
				GLFW.glfwSetWindowShouldClose(window, true);
				Log.LOGGER.info("close windows");
			}
		});
	}
}
