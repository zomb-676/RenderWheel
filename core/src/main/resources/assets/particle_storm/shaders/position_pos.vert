#version 430

in layout(location = 0) vec3 Position;
in layout(location = 1) vec4 Color;

out vec4 color;

void main() {
    gl_Position = vec4(Position, 1.0);
    color = Color;
}