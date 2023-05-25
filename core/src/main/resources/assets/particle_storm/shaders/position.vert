#version 430

in layout(location = 0) vec3 Position;

uniform vec4 color;

void main() {
    gl_Position = vec4(Position, 1.0);
}