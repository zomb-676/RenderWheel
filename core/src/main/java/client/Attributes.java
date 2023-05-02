package client;

import gl.GlType;
import gl.VertexAttribute;
import gl.VertexAttributeElement;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Attributes {
	public static final VertexAttributeElement COLOR4 = VertexAttributeElement.of()
			.setType(GlType.FLOAT)
			.needNormalize()
			.setName("color")
			.setCount(4)
			.build();

	public static final VertexAttributeElement POSITION3 = VertexAttributeElement.of()
			.setType(GlType.FLOAT)
			.noNeedNormalize()
			.setName("position")
			.setCount(3)
			.build();

	public static final VertexAttributeElement UV2 = VertexAttributeElement.of()
			.setType(GlType.FLOAT)
			.noNeedNormalize()
			.setName("uv")
			.setCount(2)
			.build();

	public static final VertexAttributeElement OFFSET = VertexAttributeElement.of()
			.setType(GlType.FLOAT)
			.noNeedNormalize()
			.setName("offset")
			.setCount(2)
			.setDivisor(1)
			.build();


	public static final VertexAttribute POSITION3_COLOR4 = VertexAttribute.of()
			.appendElement(POSITION3)
			.appendElement(COLOR4)
			.build();

	public static final VertexAttribute POSITION3_UV2 = VertexAttribute.of()
			.appendElement(POSITION3)
			.appendElement(UV2)
			.build();
}
