#version 430

in layout(location = 0) vec3 Position;
in layout(location = 1) vec4 Color;
in layout(location = 2) vec2 Offset;

out vec4 color;

void main() {
    gl_Position = vec4(Position.xy + Offset, Position.z, 1.0);
    color = Color * gl_InstanceID / 100.0;
}