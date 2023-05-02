#version 430

in layout(location = 0) vec3 Position;
in layout(location = 1) vec2 UV;

out vec2 uv;

void main() {
    gl_Position = vec4(Position, 1.0);
    uv = UV;
}