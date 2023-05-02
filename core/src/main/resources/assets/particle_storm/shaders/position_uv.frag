#version 430

uniform sampler2D texture;

in vec2 uv;

out vec4 fragColor;

void main() {
    fragColor = texture2D(texture, uv);
}