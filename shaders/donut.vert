#version 330

in vec2 inPosition;
out vec3 Normal;

uniform mat4 uView;
uniform mat4 uProj;
uniform mat4 uTransform;

uniform float uTime;

void main() {

    float mjR = 1.0;
    float mnR = 0.3;

    float theta = inPosition.x * 6.28;
    float phi = inPosition.y * 6.28;

    float x = (mjR + mnR * cos(phi)) * cos(theta);
    float y = (mjR + mnR * cos(phi)) * sin(theta);
    float z = mnR * sin(phi);

    vec3 objNormal = vec3(
    -mnR * cos(phi) * cos(theta),
    -mnR * cos(phi) * sin(theta),
    mnR * sin(phi)
    );

    // Přepočítáme normálu do soustavy pozorovatele
    vec3 viewNormal = normalize(mat3(uView * uTransform) * objNormal);

    Normal = viewNormal;

    vec4 pos = uProj * uView * uTransform * vec4(x, y, z, 1.0);
    gl_Position = pos;
}
