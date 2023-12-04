#version 330

in vec3 Normal;
out vec4 outColor;

void main() {
    vec3 color = 0.5 * Normal + 0.5;
    outColor = vec4(color, 1.0);
}
