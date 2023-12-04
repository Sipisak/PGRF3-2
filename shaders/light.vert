#version 330

in vec2 inPosition;

uniform mat4 uView;
uniform mat4 uProj;

void main() {
    vec4 pos = uProj * uView * vec4(inPosition, 1.f, 1.);
    gl_Position = pos;
}
