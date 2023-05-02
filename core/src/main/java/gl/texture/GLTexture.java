package gl.texture;

import gl.GLObject;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

public abstract class GLTexture extends GLObject {

	private static final boolean flipY = true;
	private final int textureType;

	static {
		STBImage.stbi_set_flip_vertically_on_load(flipY);
	}

	protected GLTexture(String textureName, int textureType) {
		super(textureName);
		this.textureType = textureType;
		initId();
	}

	@Override
	protected void release() {
		GL46.glDeleteTextures(this.getId());
	}

	@Override
	protected final int generateId() {
		return GL46.glCreateTextures(textureType);
	}

	protected abstract void setupTextureSetting(boolean uploadData);

	public final void bindToTextureUnit(int unit) {
		GL46.glBindTextureUnit(unit, this.getId());
	}

	public final void bingToImage(int imageUnit) {
		GL46.glBindImageTexture(imageUnit, this.getId(), 0, false, 0,
				GL46.GL_WRITE_ONLY, GL46.GL_RGBA8);
	}

	abstract int getTextureDimension();
}
