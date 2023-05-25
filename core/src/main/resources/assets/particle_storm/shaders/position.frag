#version 430

layout(binding = 0) uniform atomic_uint count;
uniform int width;
uniform int height;

out vec4 fragColor;


void main() {
    uint oldCount = atomicCounterIncrement(count);
    float pixels = float(width * height);
    float percent = float(oldCount) / pixels;
    fragColor = vec4(vec3(percent), 1.0);
}