package gl.texture;

import gl.FrameBufferAttachment;
import org.apache.http.util.Asserts;
import org.lwjgl.opengl.GL46;

public final class GLTexture2D extends GLTexture implements FrameBufferAttachment {

	private final GLTexture2DSetting setting;
	private boolean hasSetup = false;
	private final TextureData2D data;

	private GLTexture2D(String textureName, GLTexture2DSetting setting, TextureData2D textureData) {
		super(textureName, GL46.GL_TEXTURE_2D);
		this.data = textureData;
		this.setting = setting;
	}

	@Override
	protected void release() {
		super.release();
		data.close();
	}

	@Override
	protected void setupTextureSetting(boolean uploadData) {
		Asserts.check(!hasSetup, "can't setup texture multi times");
		hasSetup = true;

		int id = this.getId();
		if (!setting.skipWarp) {
			GL46.glTextureParameteri(id, GL46.GL_TEXTURE_WRAP_S, setting.warpS);
			GL46.glTextureParameteri(id, GL46.GL_TEXTURE_WRAP_T, setting.warpT);
			setting.getWarpColor().ifPresent(color ->
					GL46.glTextureParameterfv(id, GL46.GL_TEXTURE_BORDER_COLOR, color));
		}

		if (!setting.skipMipmap) {
			GL46.glTextureParameteri(id, GL46.GL_TEXTURE_MIN_FILTER, setting.filterMin);
			GL46.glTextureParameteri(id, GL46.GL_TEXTURE_MAG_FILTER, setting.filterMag);
		}

		GL46.glTextureStorage2D(id, 1, data.getInternalFormat(), data.getWidth(), data.getHeight());

		if (uploadData) {
			GL46.glTextureSubImage2D(id, 0, 0, 0,
					data.getWidth(), data.getHeight(), data.getFileFormat(), GL46.GL_UNSIGNED_BYTE, data.getData());
			GL46.glGenerateTextureMipmap(id);
		}

	}

	@Override
	int getTextureDimension() {
		return 2;
	}

	/**
	 * texture without data for data transform or generate
	 */
	public static GLTexture2D ofEmpty(String textureName, GLTexture2DSetting setting, TextureData2D textureData) {
		GLTexture2D texture = new GLTexture2D(textureName, setting, textureData);
		texture.setupTextureSetting(false);
		return texture;
	}

	/**
	 * texture from exist
	 */
	public static GLTexture2D of(String textureName, GLTexture2DSetting setting, TextureData2D textureData) {
		GLTexture2D texture = new GLTexture2D(textureName, setting, textureData);
		texture.setupTextureSetting(true);
		return texture;
	}

	/**
	 * @return the setting builder
	 */
	public static GLTexture2DSetting.GLTexture2DSettingBuilder ofSetting() {
		return new GLTexture2DSetting.GLTexture2DSettingBuilder();
	}

	@Override
	public int getInternalFormat() {
		return data.getInternalFormat();
	}

	@Override
	public int getWidth() {
		return data.getWidth();
	}

	@Override
	public int getHeight() {
		return data.getHeight();
	}
}
