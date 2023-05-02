package gl.texture;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.http.util.Asserts;
import org.lwjgl.opengl.GL46;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ParametersAreNonnullByDefault
public final class GLTexture2DSetting {
	final int warpS;
	final int warpT;
	@Nullable
	final float[] warpColor;
	final int filterMin;
	final int filterMag;
	private final GLTexture2DSettingBuilder builder;
	final boolean skipWarp;
	final boolean skipMipmap;

	public Optional<float[]> getWarpColor() {
		return Optional.ofNullable(warpColor);
	}

	public static GLTexture2DSetting BEST = GLTexture2D.ofSetting()
			.getWarpS().repeat()
			.getWarpT().repeat()
			.getFilterMin().mipmapLinearSampleLinear()
			.getFilterMag().linear()
			.build();

	public static GLTexture2DSetting PIXEL = GLTexture2D.ofSetting()
			.getWarpS().repeat()
			.getWarpT().repeat()
			.getFilterMin().mipmapNearestSampleNearest()
			.getFilterMag().nearest()
			.build();

	public static GLTexture2DSetting BEST_SKIP_MIPMAP = BEST
			.toBuilder()
			.skipMipmap()
			.build();

	public GLTexture2DSettingBuilder toBuilder() {
		return builder.copy();
	}

	@AllArgsConstructor
	public static class GLTexture2DSettingBuilder {
		@Getter
		private final WarpSetting warpS;
		@Getter
		private final WarpSetting warpT;
		private float[] warpColor;
		@Getter
		private final FilterMipmapSetting filterMin;
		@Getter
		private final FilterSetting filterMag;
		@Getter
		private boolean skipWarp = false;
		@Getter
		private boolean skipMipmap = false;

		GLTexture2DSettingBuilder() {
			warpS = new WarpSetting();
			warpT = new WarpSetting();
			filterMin = new FilterMipmapSetting();
			filterMag = new FilterSetting();
		}

		public GLTexture2DSetting build() {
			if (!skipWarp) {
				Asserts.check(warpS.warpStyle != -1, "warpS should be set or skipWarp");
				Asserts.check(warpT.warpStyle != -1, "warpT should be set or skipWarp");
				if (warpS.warpStyle == GL46.GL_CLAMP_TO_BORDER || warpT.warpStyle == GL46.GL_CLAMP_TO_BORDER) {
					Asserts.notNull(warpColor, "border color should be set if use warp GL_CLAMP_TO_BORDER");
				}
			}

			if (!skipMipmap) {
				Asserts.check(filterMin.filterStyle != -1, "mipmapMin should be set or skipMipmap");
				Asserts.check(filterMag.filterStyle != -1, "mipmapMag should be set or skipMipmap");
			}

			return new GLTexture2DSetting(warpS.warpStyle, warpT.warpStyle, warpColor,
					filterMin.filterStyle, filterMag.filterStyle, copy(), skipWarp, skipMipmap);
		}

		public GLTexture2DSettingBuilder skipWarp() {
			this.skipWarp = true;
			return this;
		}

		public GLTexture2DSettingBuilder skipMipmap() {
			this.skipMipmap = true;
			return this;
		}

		private GLTexture2DSettingBuilder copy() {
			return new GLTexture2DSettingBuilder(warpS.copy(), warpT.copy(),
					warpColor == null ? null : Arrays.copyOf(warpColor, warpColor.length),
					filterMin.copy(), filterMag.copy(), skipWarp, skipMipmap);
		}

		@AllArgsConstructor
		@NoArgsConstructor
		public class WarpSetting {
			private int warpStyle = -1;

			public GLTexture2DSettingBuilder repeat() {
				warpStyle = GL46.GL_REPEAT;
				return GLTexture2DSettingBuilder.this;
			}

			public GLTexture2DSettingBuilder repeatMirrored() {
				warpStyle = GL46.GL_MIRRORED_REPEAT;
				return GLTexture2DSettingBuilder.this;
			}

			public GLTexture2DSettingBuilder clampToEdge() {
				warpStyle = GL46.GL_CLAMP_TO_EDGE;
				return GLTexture2DSettingBuilder.this;
			}

			public GLTexture2DSettingBuilder calampToBorderColor(float[] color) {
				Asserts.check(color.length == 4, "border color array's size must be 4");
				GLTexture2DSettingBuilder.this.warpColor = color;
				warpStyle = GL46.GL_CLAMP_TO_BORDER;
				return GLTexture2DSettingBuilder.this;
			}

			public GLTexture2DSettingBuilder calampToBorderColor() {
				warpStyle = GL46.GL_CLAMP_TO_BORDER;
				return GLTexture2DSettingBuilder.this;
			}

			private WarpSetting copy() {
				return new WarpSetting(warpStyle);
			}
		}

		@AllArgsConstructor
		@NoArgsConstructor
		public class FilterSetting {
			protected int filterStyle = -1;

			public GLTexture2DSettingBuilder nearest() {
				filterStyle = GL46.GL_NEAREST;
				return GLTexture2DSettingBuilder.this;
			}

			public GLTexture2DSettingBuilder linear() {
				filterStyle = GL46.GL_LINEAR;
				return GLTexture2DSettingBuilder.this;
			}

			private FilterSetting copy() {
				return new FilterSetting(filterStyle);
			}
		}

		@NoArgsConstructor
		public class FilterMipmapSetting extends FilterSetting {
			private FilterMipmapSetting(int filterStyle) {
				super(filterStyle);
			}

			public GLTexture2DSettingBuilder mipmapNearestSampleNearest() {
				filterStyle = GL46.GL_NEAREST_MIPMAP_NEAREST;
				return GLTexture2DSettingBuilder.this;
			}

			public GLTexture2DSettingBuilder mipmapLinearSampleNeareset() {
				filterStyle = GL46.GL_LINEAR_MIPMAP_NEAREST;
				return GLTexture2DSettingBuilder.this;
			}

			public GLTexture2DSettingBuilder mimpapNearestSampleLinear() {
				filterStyle = GL46.GL_NEAREST_MIPMAP_LINEAR;
				return GLTexture2DSettingBuilder.this;
			}

			public GLTexture2DSettingBuilder mipmapLinearSampleLinear() {
				filterStyle = GL46.GL_LINEAR_MIPMAP_LINEAR;
				return GLTexture2DSettingBuilder.this;
			}

			private FilterMipmapSetting copy() {
				return new FilterMipmapSetting(filterStyle);
			}
		}

	}


}