package gl;

import com.google.common.collect.ImmutableList;
import gl.buffer.GLBuffer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.http.util.Asserts;
import org.jetbrains.annotations.Contract;
import org.lwjgl.opengl.GL46;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public final class VertexAttribute {
	private final ImmutableList<VertexAttributeElement> elements;
	private final int[] offsets;
	private final int stride;
	@Getter
	private final int count;

	private VertexAttribute(ImmutableList<VertexAttributeElement> elements) {
		this.elements = elements;
		this.offsets = calculateOffsets(elements);
		this.stride = offsets[offsets.length - 1];
		this.count = elements.size();
	}

	public void setup(VertexArrayObject vao, GLBuffer buffer, int bufferBindPoint, int baseAttributeIndex) {
		long baseOffset = 0;
		GL46.glVertexArrayVertexBuffer(vao.getId(), bufferBindPoint,
				buffer.getId(), baseOffset, stride);

		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).setUp(vao, bufferBindPoint, i + baseAttributeIndex, offsets[i]);
		}
	}

	@Contract(pure = true, value = "_,_->new")
	public VertexAttribute with(VertexAttributeElement element, int divisor) {
		Asserts.check(elements.contains(element), "can't call with with element un-contained");
		var builder = VertexAttribute.of();
		for (var e : elements) {
			builder.appendElement(e == element ? e.withDivisor(divisor) : e);
		}
		return builder.build();
	}

	@Contract(pure = true, value = "_->new")
	public VertexAttribute append(VertexAttributeElement element) {
		Asserts.check(!elements.contains(element), "can't append an already contained element");
		return VertexAttribute.of(ImmutableList.<VertexAttributeElement>builder().addAll(elements).add(element).build());
	}

	private static int[] calculateOffsets(List<VertexAttributeElement> elements) {
		var data = new int[elements.size() + 1];
		var accumulate = 0;
		for (int i = 0; i < elements.size(); i++) {
			data[i] = accumulate;
			accumulate += elements.get(i).getSize();
		}
		data[elements.size()] = accumulate;
		return data;
	}

	public static VertexAttributeBuilder of() {
		return new VertexAttributeBuilder();
	}

	public static VertexAttribute of(ImmutableList<VertexAttributeElement> elements) {
		return new VertexAttribute(elements);
	}

	@NoArgsConstructor
	public static class VertexAttributeBuilder {
		private final ImmutableList.Builder<VertexAttributeElement> builder = ImmutableList.builder();

		public VertexAttributeBuilder appendElement(VertexAttributeElement element) {
			builder.add(element);
			return this;
		}

		public VertexAttributeBuilder appendElements(VertexAttributeElement... element) {
			builder.add(element);
			return this;
		}

		public VertexAttribute build() {
			ImmutableList<VertexAttributeElement> list = builder.build();
			Asserts.check(list.size() != 0,"don't build an empty VertexAttribute");
			return new VertexAttribute(list);
		}

	}

}
