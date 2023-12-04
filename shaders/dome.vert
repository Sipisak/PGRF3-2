#version 330

in vec2 inPosition;
out vec3 color;

uniform mat4 uView;
uniform mat4 uProj;
uniform float uTime;
uniform mat4 uTransform;

void main() {
    float x = inPosition.x * 2.0 - 1.0;
    float y = inPosition.y * 2.0 - 1.0;
    float z2 = max(1.0 - x * x - y * y, 0.0);
    float z = sqrt(z2);

    // Parciální derivace podle x a y
    float dz_dx = -x / sqrt(max(1.0 - x * x - y * y, 0.0));
    float dz_dy = -y / sqrt(max(1.0 - x * x - y * y, 0.0));

    vec3 normalSphere = normalize(vec3(x, y, z));
    vec3 normalCube = normalize(vec3(x, y, z));

    float angle = uTime * 3.14;

    vec3 normal = mix(normalSphere, normalCube, smoothstep(0.0, 1.0, sin(angle)));

    // Použití parciálních derivací pro otočení normály
    normal.x += dz_dx * cos(angle);
    normal.y += dz_dy * cos(angle);
    normal.z += -z * sin(angle);

    // Normalizace normály
    normal = normalize(normal);

    float height = z * cos(angle);
    vec3 spherePos = vec3(x, y, height);
    vec3 cubePos = normalize(vec3(x, y, z)) * 0.5;
    vec3 result = mix(spherePos, cubePos, smoothstep(0.0, 1.0, sin(angle)));

    gl_Position = uProj * uView * uTransform * vec4(result, 1.0);

    color = vec3(0.5 + 0.5 * normal);
}
