package gl;

import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.http.util.Asserts;
import org.jetbrains.annotations.Contract;
import org.lwjgl.opengl.GL46;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ParametersAreNonnullByDefault
public final class VertexAttributeElement {

	@Getter
	private final String name;
	@Getter
	private final int type;
	@Getter
	@Nonnegative
	private final int count;
	@Getter
	@Nonnegative
	private final int size;
	@Getter
	private final boolean needNormalize;
	@Getter
	@Nonnegative
	private final int divisor;
	@Nullable
	@LazyInit
	private VertexAttribute single;

	public void setUp(VertexArrayObject vao, int bufferBindPoint, int attributeLocation, int offset) {
		var vaoId = vao.getId();
		GL46.glEnableVertexArrayAttrib(vaoId, attributeLocation);
		GL46.glVertexArrayAttribFormat(vaoId, attributeLocation, count, type, needNormalize, offset);
		GL46.glVertexArrayAttribBinding(vaoId, attributeLocation, bufferBindPoint);
		if (divisor != 0) GL46.glVertexArrayBindingDivisor(vaoId, bufferBindPoint, divisor);
	}

	@Contract(pure = true, value = "_->new")
	public VertexAttributeElement withDivisor(int divisor) {
		Asserts.check(divisor >= 0, "divisor must > 0");
		return new VertexAttributeElement(name, type, count, size, needNormalize, divisor);
	}

	@Contract("->!null")
	public VertexAttribute single() {
		if (single == null) {
			single = VertexAttribute.of().appendElement(this).build();
		}
		return single;
	}

	public static VertexAttributeElementBuilder of() {
		return new VertexAttributeElementBuilder();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class VertexAttributeElementBuilder {
		private String name;
		private GlType type;
		private int count = -1;
		private boolean needNormalize;
		private boolean hasNormalizedBeenSet = false;
		private int divisor = 0;

		public VertexAttributeElementBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public VertexAttributeElementBuilder setDivisor(int divisor) {
			Asserts.check(divisor >= 0, "divisor must >= 0");
			this.divisor = divisor;
			return this;
		}

		public VertexAttributeElementBuilder setCount(int count) {
			Asserts.check(count >= 1 && count <= 4, "vertex attribute must be 1,2,3 or 4");
			this.count = count;
			return this;
		}

		public VertexAttributeElementBuilder setType(GlType type) {
			this.type = type;
			return this;
		}

		public VertexAttributeElementBuilder needNormalize() {
			this.needNormalize = true;
			this.hasNormalizedBeenSet = true;
			return this;
		}

		public VertexAttributeElementBuilder noNeedNormalize() {
			needNormalize = false;
			this.hasNormalizedBeenSet = true;
			return this;
		}

		public VertexAttributeElement build() {
			Asserts.notNull(name, "name must be set for VertexAttributeElement");
			Asserts.notNull(type, "type must be set for VertexAttributeElement");
			Asserts.check(count != -1, "count must be set for VertexAttributeElement");
			Asserts.check(hasNormalizedBeenSet, "needNormalize/noNeedNormalize must be called");
			return new VertexAttributeElement(name, type.glType, count, type.byteSize * count, needNormalize, divisor);
		}

	}

}
