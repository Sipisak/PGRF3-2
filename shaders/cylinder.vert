#version 330

in vec2 inPosition;

uniform mat4 uView;
uniform mat4 uProj;
uniform mat4 uTransform;

const float PI = 3.14159265359;

void main() {
    float radius = 1.0;

    // Azimutální úhel
    float theta = inPosition.x * 2.0 * PI;

    // Výška (upravena na normální rozsah [-1, 1])
    float height = inPosition.y * 2.0 - 1.0;

    // Výpočet polohy v cylindrických souřadnicích
    float x = radius * cos(theta);
    float y = radius * sin(theta);
    float z = height;

    vec4 pos = uProj * uView * uTransform * vec4(x, y, z, 1.0);

    gl_Position = pos;
}
