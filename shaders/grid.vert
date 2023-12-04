#version 330

in vec2 inPosition;

uniform mat4 uView;
uniform mat4 uProj;
uniform float uTime;
uniform vec3 u_LightPos;

out vec3 toLightVector;
out vec3 normal;
out vec2 texCoord;
out vec3 debugColor;
out float lightDistance;


vec3 getNormal() {
    return vec3(0, 0, 1);
}

void main() {
    texCoord = inPosition;
    // převést na rozsah <-1; 1>
    vec2 pos = inPosition * 2 - 1;
    // spočítat z
    //float z = 0.5 * cos(sqrt(20 * pow(pos.x, 2) + 20 * pow(pos.y, 2)) + uTime);
    float z = 0.f;

    // Phong
    vec4 pos_W = vec4(pos, z, 1.);
    vec4 pos_V = uView * pos_W;
    vec4 lightPos_V = uView * vec4(u_LightPos, 1.f);
    toLightVector = lightPos_V.xyz - pos_V.xyz;
    normal = transpose(inverse(mat3(uView))) * getNormal();
    lightDistance = length(toLightVector);

    debugColor = vec3(pos, 0.f);

    // Final position
    vec4 pos_MVP = uProj * pos_V;
    gl_Position = pos_MVP;
}
