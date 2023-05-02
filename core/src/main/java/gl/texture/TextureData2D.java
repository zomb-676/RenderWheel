package gl.texture;

import gl.Log;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.util.Asserts;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

import java.io.Closeable;
import java.nio.ByteBuffer;

@RequiredArgsConstructor
public final class TextureData2D implements Closeable {
	@Getter
	private final int width;
	@Getter
	private final int height;
	@Getter
	private final int channels;
	private final int actualChannels;
	private final ByteBuffer data;
	private boolean dataAvailable = true;
	@Getter
	private final int internalFormat;
	@Getter
	private final int fileFormat = GL46.GL_RGBA;

	public ByteBuffer getData() {
		Asserts.check(dataAvailable, "can't get unavailable data");
		return data;
	}

	@Override
	public void close() {
		if (dataAvailable) {
			STBImage.stbi_image_free(data);
			dataAvailable = false;
		} else {
			Log.LOGGER.error("can't close an already closed TextureData2D buffer");
		}
	}

	public static TextureData2D ofNullData(int width, int height, int channel) {
		TextureData2D data2D = new TextureData2D(width, height, channel, channel, null, GL46.GL_RGBA8);
		data2D.dataAvailable = false;
		return data2D;
	}

	public static TextureData2D ofType(int width, int height, int internalFormat) {
		TextureData2D data2D = new TextureData2D(width, height, 0, 0, null, internalFormat);
		data2D.dataAvailable = false;
		return data2D;
	}
}
