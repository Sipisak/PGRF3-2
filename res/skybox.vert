#version 330
in vec3 inPosition;
in vec3 inNormal;
out vec3 vertColor;
out vec3 vertPosition;
uniform mat4 mat;
void main() {
    vertPosition = 2*inPosition.xyz-1.0;
    gl_Position = mat*vec4(inPosition, 1.0);
    vertColor = inNormal * 0.5 + 0.5;
}
