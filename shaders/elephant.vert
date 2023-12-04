#version 330

in vec2 inPosition;
out vec3 color;

uniform mat4 uView;
uniform mat4 uProj;
uniform float uTime;

void main() {
    float zenith = inPosition.x * 3.14159265359;
    float azimuth = inPosition.y * 2.0 * 3.14159265359;

    vec3 position = vec3(
    cos(azimuth) * sin(zenith),
    cos(zenith),
    sin(azimuth) * sin(zenith)
    );

    float radius = 3.0 * cos(4.0 * azimuth);
    vec3 newPosition = position * radius;

    vec3 offset = vec3(5.0, 3.8, 3.0);
    newPosition = newPosition + offset;


    gl_Position = uProj * uView * vec4(newPosition, 1.0);
    color = vec3(1.0, 0.0, 0.0);
}
