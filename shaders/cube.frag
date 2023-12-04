#version 330

in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec3 color;

void main() {
    // fragColor = texture(textureSampler, texCoord);
    fragColor = vec4(color, 1.0);
}
