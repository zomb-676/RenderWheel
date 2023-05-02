package resource;

import gl.Log;
import gl.texture.TextureData2D;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.http.util.Asserts;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

@UtilityClass
public class ResourceLoader {
	private static final Path resourcePath = new File("./core/src/main/resources/").toPath();
	private static final Path assetsPath = resourcePath.resolve("assets");
	private static final Path nameSpacePath = assetsPath.resolve("particle_storm");
	private static final Path shaderPath = nameSpacePath.resolve("shaders");
	private static final Path texturePath = nameSpacePath.resolve("textures");

	public static File ofShaderFile(String shaderName) {
		File shaderFile = shaderPath.resolve(shaderName).toFile();
		Asserts.check(shaderFile.exists(), "shader file no exist, path:" + shaderFile);
		Asserts.check(shaderFile.isFile(), "shader file must be a file, path:" + shaderFile);
		return shaderFile;
	}

	@SneakyThrows
	public static String ofShader(String shaderName) {
		File shaderFile = ofShaderFile(shaderName);
		@Cleanup var stream = new FileInputStream(shaderFile);
		return new String(stream.readAllBytes());
	}

	@SneakyThrows
	public static TextureData2D ofTexture(String textureName) {
		File textureFile = texturePath.resolve(textureName).toFile();
		Asserts.check(textureFile.exists(), "texture file not exists, path:" + textureFile);
		Asserts.check(textureFile.canRead(), "texture file can't be read, path:" + textureFile);

		@Cleanup var stream = new FileInputStream(textureFile);
		var size = textureFile.length();
		Asserts.check(size <= Integer.MAX_VALUE, "texture's size measured in bytes is bigger than Integer.MAX_VALUE");
		var data = MemoryUtil.memAlloc((int) size);
		data.put(stream.readAllBytes());
		data.flip();

		@Cleanup var stack = MemoryStack.stackPush();
		IntBuffer width = stack.mallocInt(1);
		IntBuffer height = stack.mallocInt(1);
		IntBuffer channels = stack.mallocInt(1);
		var desiredChannels = 4;

		ByteBuffer textureBuffer = STBImage.stbi_load_from_memory(data, width, height, channels, desiredChannels);

		if (textureBuffer == null) {
			var failureReason = STBImage.stbi_failure_reason();
			Asserts.notNull(failureReason, "can't get failure reason while loading image by stbi_image");
			Log.LOGGER.error("failed to load image, path:" + textureFile);
			Log.LOGGER.error("reason:" + failureReason);
			throw new IllegalStateException("failed to load image, file:" + textureFile + ",reason:" + failureReason);
		}
		Log.LOGGER.info("load texture:" + textureName);

		return new TextureData2D(width.get(), height.get(), desiredChannels, channels.get(), textureBuffer, GL46.GL_RGBA8);
	}

}
