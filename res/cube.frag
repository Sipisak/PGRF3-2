#version 330
in vec3 vertColor;
in vec3 vertPosition;
out vec4 outColor;
uniform samplerCube textureSampler;
void main() {
    outColor = vec4(texture(textureSampler, vertPosition).rgb,1.0);
}


